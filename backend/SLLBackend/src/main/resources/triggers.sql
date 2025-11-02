CREATE EXTENSION IF NOT EXISTS pgcrypto;
--
-- CREATE TYPE gender_enum AS ENUM ('MALE', 'FEMALE');
-- CREATE TYPE account_status_enum AS ENUM ('ACTIVE', 'DEACTIVATED', 'BANNED');
-- CREATE TYPE commission_type_enum AS ENUM ('APPOINTMENT', 'PRODUCT');
-- CREATE TYPE staff_status_enum AS ENUM ('ACTIVE', 'ON_LEAVE', 'TERMINATED');
-- CREATE TYPE appointment_status_enum AS ENUM ('PENDING', 'REGISTERED', 'STARTED', 'COMPLETED', 'RESCHEDULED', 'CANCELLED');
-- CREATE TYPE service_type_enum AS ENUM ('SINGLE', 'COMBO');
-- CREATE TYPE shift_attendance_status_enum AS ENUM ('FULL', 'PARTIAL', 'MISSED');
-- CREATE TYPE discount_type_enum AS ENUM ('AMOUNT', 'PERCENTAGE');
-- CREATE TYPE inventory_invoice_status_enum AS ENUM ('AWAITING', 'COMPLETE', 'CANCELLED');
-- CREATE TYPE inventory_request_status_enum AS ENUM ('PENDING', 'APPROVED', 'REJECTED');
-- CREATE TYPE inventory_transaction_type_enum AS ENUM ('IN', 'OUT');
-- CREATE TYPE inventory_transaction_reason_enum AS ENUM ('SERVICE', 'SHIPMENT');
-- CREATE TYPE payroll_adjustment_enum AS ENUM ('BONUS', 'DEDUCTION');
-- CREATE TYPE job_posting_status_enum AS ENUM ('ACTIVE', 'DEACTIVATED');
-- CREATE TYPE job_posting_application_status_enum AS ENUM ('PENDING', 'ACCEPTED', 'REJECTED');

CREATE
OR REPLACE FUNCTION create_staff_account()
    RETURNS trigger AS $$
    DECLARE
name_parts TEXT[];
    staff_username TEXT;
    base_username TEXT;
    suffix INT := 1;
BEGIN
    name_parts := string_to_array(NEW.name, ' ');
    base_username := name_parts[array_length(name_parts,1)];
FOR i IN 1..array_length(name_parts,1)-1 LOOP
        base_username := base_username || left(name_parts[i],1);
END LOOP;

    staff_username := base_username;

    WHILE EXISTS (SELECT 1 FROM staff_account WHERE username = staff_username) LOOP
        suffix := suffix + 1;
        staff_username := base_username || suffix;
END LOOP;

INSERT INTO staff_account(staff_id, username, password)
VALUES (NEW.id, staff_username, crypt('default123', gen_salt('bf')));

RETURN NULL;
END;
    $$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION add_loyalty_record()
RETURNS trigger AS $$
BEGIN
INSERT INTO loyalty(user_id)
VALUES (NEW.id)
ON CONFLICT (user_id) DO NOTHING;

RETURN NULL;
END;
$$ LANGUAGE plpgsql;

       CREATE OR REPLACE FUNCTION assign_loyalty_rank()
RETURNS TRIGGER AS $$
DECLARE
new_rank INT;
BEGIN

SELECT id
INTO new_rank
FROM loyalty_level
WHERE point_required >= NEW.point
ORDER BY point_required ASC
    LIMIT 1;

IF new_rank IS DISTINCT FROM NEW.level_id THEN
UPDATE loyalty
SET level_id = new_rank
WHERE user_id = NEW.user_id;
END IF;

RETURN NULL;
END;
$$ LANGUAGE plpgsql;


            CREATE OR REPLACE FUNCTION record_product_change()
    RETURNS trigger AS $$
BEGIN
        IF (OLD.product_name IS DISTINCT FROM NEW.product_name
            OR OLD.current_price IS DISTINCT FROM NEW.current_price
            OR OLD.product_description IS DISTINCT FROM NEW.product_description
        ) THEN
UPDATE product_change_history
SET effective_to = NOW()
WHERE product_id = OLD.id AND effective_to IS NULL;

INSERT INTO product_change_history(product_id, product_name, unit_price, product_description) VALUES
    (OLD.id, OLD.product_name, OLD.current_price, OLD.product_description);
END IF;
RETURN NEW;
END;
    $$ LANGUAGE plpgsql;

    CREATE OR REPLACE FUNCTION record_service_change()
    RETURNS trigger AS $$
BEGIN
        IF (OLD.service_name IS DISTINCT FROM NEW.service_name
            OR OLD.service_category_id IS DISTINCT FROM NEW.service_category_id
            OR OLD.service_price IS DISTINCT FROM NEW.service_price
            OR OLD.duration_minutes IS DISTINCT FROM NEW.duration_minutes
            OR OLD.service_description IS DISTINCT FROM NEW.service_description
            OR OLD.active_status IS DISTINCT FROM NEW.active_status
        ) THEN
UPDATE service_change_history
SET effective_to = NOW()
WHERE service_id = OLD.id AND effective_to IS NULL;

INSERT INTO service_change_history(service_id, service_name, service_category_id, service_price, duration_minutes, service_description) VALUES
    (OLD.id, OLD.service_name, OLD.service_category_id, OLD.service_price, OLD.duration_minutes, OLD.service_description);
END IF;
RETURN NEW;
END;
    $$ LANGUAGE plpgsql;

       CREATE OR REPLACE FUNCTION prevent_taking_expired_product()
    RETURNS TRIGGER AS $$
BEGIN
        IF NEW.expiry_date <= CURRENT_DATE THEN
            RAISE EXCEPTION 'Expiry date must be in the future';
END IF;

RETURN NEW;
END;
    $$ LANGUAGE plpgsql;

           CREATE OR REPLACE FUNCTION create_inventory_consignment()
    RETURNS TRIGGER AS $$
    DECLARE
source_supplier_id INT;
BEGIN
SELECT supplier_id
INTO source_supplier_id
FROM inventory_invoice
WHERE id = NEW.inventory_invoice_id;

INSERT INTO inventory_consignment(inventory_invoice_detail_id, product_id, supplier_id, received_quantity)
VALUES (NEW.id, NEW.product_id, source_supplier_id, NEW.ordered_quantity);

RETURN NULL;
END;
    $$ LANGUAGE plpgsql;

         CREATE OR REPLACE FUNCTION update_appointment_status_registered()
    RETURNS trigger AS $$
BEGIN
        NEW.status := 'REGISTERED';

RETURN NEW;
END;
    $$ LANGUAGE plpgsql;

        CREATE OR REPLACE FUNCTION update_appointment_status_rescheduled()
    RETURNS trigger AS $$
BEGIN
        NEW.status := 'RESCHEDULED';

RETURN NEW;
END;
    $$ LANGUAGE plpgsql;

       CREATE OR REPLACE FUNCTION update_appointment_actual_start()
    RETURNS trigger AS $$
BEGIN
UPDATE appointment_details
SET actual_start = NOW()
WHERE appointment_id = NEW.id;

RETURN NULL;
END;
    $$ LANGUAGE plpgsql;

    CREATE OR REPLACE FUNCTION update_appointment_actual_end()
    RETURNS trigger AS $$
BEGIN
UPDATE appointment_details
SET actual_end = NOW()
WHERE appointment_id = NEW.id;

RETURN NULL;
END;
    $$ LANGUAGE plpgsql;

        CREATE OR REPLACE FUNCTION credit_loyalty_point()
    RETURNS trigger AS $$
    DECLARE
serviced_user_id INT;
BEGIN
SELECT user_id
INTO serviced_user_id
FROM appointment_details
WHERE appointment_id = NEW.appointment_id
    LIMIT 1;

IF serviced_user_id IS NULL THEN
            RETURN NULL;
END IF;

UPDATE loyalty
SET point = point + ROUND(NEW.total_price / 100)::int
WHERE user_id = serviced_user_id;

RETURN NULL;
END;
    $$ LANGUAGE plpgsql;

       CREATE OR REPLACE FUNCTION create_appointment_detail()
    RETURNS trigger AS $$
    DECLARE
        time_displacement INT;
BEGIN
SELECT COALESCE(SUM(s.duration_minutes), 0)
INTO time_displacement
FROM requested_service rs
         JOIN service s ON s.id = rs.service_id
WHERE rs.appointment_id = NEW.id;

INSERT INTO appointment_details(appointment_id, scheduled_start, scheduled_end)
VALUES (NEW.id, NEW.scheduled_at, NEW.scheduled_at + COALESCE(time_displacement, 0) * INTERVAL '1 minute');

RETURN NULL;
END;
    $$ LANGUAGE plpgsql;

           CREATE OR REPLACE FUNCTION update_service_combo_details()
    RETURNS trigger AS $$
    DECLARE
total_duration INT;
        total_price INT;
BEGIN
SELECT
    SUM(s.duration_minutes),
    SUM(s.service_price)
INTO total_duration, total_price
FROM service s
         JOIN service_combo sc ON s.id = sc.service_id
WHERE sc.combo_id = NEW.id;

UPDATE service
SET duration_minutes = COALESCE(total_duration, 0),
    service_price = COALESCE(total_price, 0)
WHERE id = NEW.id;

RETURN NULL;
END;
    $$ LANGUAGE plpgsql;

          CREATE OR REPLACE FUNCTION refresh_service_combo_details()
    RETURNS trigger AS $$
    DECLARE
combo_id INT;
        total_duration INT;
        total_price INT;
BEGIN
        combo_id := COALESCE(NEW.combo_id, OLD.combo_id);

SELECT
    SUM(s.duration_minutes),
    SUM(s.service_price)
INTO total_duration, total_price
FROM service s
         JOIN service_combo sc ON s.id = sc.service_id
WHERE sc.combo_id = combo_id;

UPDATE service
SET duration_minutes = COALESCE(total_duration, 0),
    service_price = COALESCE(total_price, 0)
WHERE id = combo_id;

RETURN NULL;
END;
    $$ LANGUAGE plpgsql;

           CREATE OR REPLACE FUNCTION propagate_service_details_to_combo()
    RETURNS trigger AS $$
    DECLARE
combo INT[];
        total_duration INT;
        total_price INT;
BEGIN
SELECT ARRAY_AGG(combo_id)
INTO combo
FROM service_combo
WHERE service_id = NEW.id;

SELECT
    SUM(s.duration_minutes),
    SUM(s.service_price)
INTO total_duration, total_price
FROM service s
         JOIN service_combo sc ON s.id = sc.service_id
WHERE sc.combo_id = ANY(combo);

IF combo IS NOT NULL THEN
UPDATE service
SET duration_minutes = COALESCE(total_duration, 0),
    service_price = COALESCE(total_price, 0)
WHERE id = ANY(combo);
END IF;
RETURN NULL;
END;
    $$ LANGUAGE plpgsql;

CREATE TRIGGER create_staff_account_trigger
    AFTER INSERT
    ON staff
    FOR EACH ROW
    EXECUTE FUNCTION create_staff_account();

CREATE TRIGGER add_loyalty_record_trigger
    AFTER INSERT ON user_account
    FOR EACH ROW
    EXECUTE FUNCTION add_loyalty_record();

CREATE TRIGGER assign_loyalty_rank_trigger
    AFTER UPDATE OF point ON loyalty
    FOR EACH ROW
    WHEN (NEW.point <> OLD.point)
    EXECUTE FUNCTION assign_loyalty_rank();

CREATE TRIGGER record_product_change_trigger
    BEFORE UPDATE ON product
    FOR EACH ROW
    EXECUTE FUNCTION record_product_change();

CREATE TRIGGER record_service_change_trigger
    BEFORE UPDATE ON service
    FOR EACH ROW
    EXECUTE FUNCTION record_service_change();

CREATE TRIGGER prevent_taking_expired_product_trigger
    BEFORE INSERT ON inventory_request_detail
    FOR EACH ROW
    EXECUTE FUNCTION prevent_taking_expired_product();

CREATE TRIGGER create_inventory_consignment_trigger
    AFTER INSERT ON inventory_invoice_detail
    FOR EACH ROW
    EXECUTE FUNCTION create_inventory_consignment();

CREATE TRIGGER update_appointment_status_registered_trigger
    BEFORE UPDATE ON appointment
    FOR EACH ROW
    WHEN (NEW.scheduled_at IS NOT NULL AND OLD.scheduled_at IS NULL)
    EXECUTE FUNCTION update_appointment_status_registered();

CREATE TRIGGER update_appointment_status_rescheduled_trigger
    BEFORE UPDATE ON appointment
    FOR EACH ROW
    WHEN (NEW.scheduled_at IS NOT NULL AND OLD.scheduled_at IS NOT NULL AND OLD.scheduled_at <> NEW.scheduled_at)
    EXECUTE FUNCTION update_appointment_status_rescheduled();

CREATE TRIGGER update_appointment_actual_start_trigger
    AFTER UPDATE OF status ON appointment
    FOR EACH ROW
    WHEN (NEW.status = 'STARTED')
    EXECUTE FUNCTION update_appointment_actual_start();

CREATE TRIGGER update_appointment_actual_end_trigger
    AFTER UPDATE OF status ON appointment
    FOR EACH ROW
    WHEN (NEW.status = 'COMPLETED')
    EXECUTE FUNCTION update_appointment_actual_end();

CREATE TRIGGER credit_loyalty_point_trigger
    AFTER INSERT ON appointment_invoice
    FOR EACH ROW
    EXECUTE FUNCTION credit_loyalty_point();

CREATE TRIGGER create_appointment_detail_trigger
    AFTER INSERT ON appointment
    FOR EACH ROW
    WHEN (NEW.scheduled_at IS NOT NULL)
    EXECUTE FUNCTION create_appointment_detail();

CREATE TRIGGER update_service_combo_details_trigger
    AFTER INSERT ON service
    FOR EACH ROW
    WHEN (NEW.service_type = 'COMBO')
    EXECUTE FUNCTION update_service_combo_details();

CREATE TRIGGER refresh_service_combo_details_trigger
    AFTER INSERT OR DELETE OR UPDATE ON service_combo
    FOR EACH ROW
    EXECUTE FUNCTION refresh_service_combo_details();

CREATE TRIGGER propagate_service_details_to_combo_trigger
    AFTER UPDATE OF service_price, duration_minutes ON service
    FOR EACH ROW
    EXECUTE FUNCTION propagate_service_details_to_combo();
^;