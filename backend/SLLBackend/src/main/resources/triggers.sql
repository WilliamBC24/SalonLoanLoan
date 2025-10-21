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
        staff_username
TEXT;
BEGIN
    name_parts := string_to_array(NEW.name, ' ');

    staff_username := name_parts[array_length(name_parts, 1)];

for i in 1..array_length(name_parts, 1) - 1 LOOP
            staff_username := staff_username || left(name_parts[i], 1);
END LOOP;

INSERT INTO staff_account(staff_id, username, password)
VALUES (NEW.id, staff_username, crypt('default123', gen_salt('bf',10)));
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
UPDATE product_change_history
SET effective_to = NOW()
WHERE product_id = NEW.id AND effective_to IS NULL;

INSERT INTO product_change_history(product_id, product_name, unit_price, product_description) VALUES
    (NEW.id, NEW.product_name, NEW.current_price, NEW.product_description);

RETURN NULL;
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
    AFTER UPDATE ON product
    FOR EACH ROW
    EXECUTE FUNCTION record_product_change();

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
^;