CREATE TYPE gender_enum AS ENUM ('male', 'female');
CREATE TYPE account_status_enum AS ENUM ('active', 'deactivated');
CREATE TYPE account_role_enum AS ENUM ('customer', 'staff', 'manager', 'owner', 'admin');
CREATE TYPE staff_position_enum AS ENUM ('staff', 'manager', 'owner');
CREATE TYPE staff_status_enum AS ENUM ('active', 'on_leave', 'terminated');
CREATE TYPE appointment_status_enum AS ENUM ('pending', 'registered', 'started', 'completed', 'rescheduled', 'cancelled');
CREATE TYPE service_type_enum AS ENUM ('single', 'combo');
CREATE TYPE service_image_state_enum AS ENUM ('before', 'after');
CREATE TYPE reminder_reason_enum AS ENUM ('promotion', 'remind');
CREATE TYPE reminder_type_enum AS ENUM ('zalo', 'email', 'text');
CREATE TYPE shift_attendance_status_enum AS ENUM ('full', 'partial', 'missed');
CREATE TYPE promotion_status_enum AS ENUM ('activated', 'deactivated');
CREATE TYPE discount_type_enum AS ENUM ('amount', 'percentage');
CREATE TYPE inventory_invoice_status_enum AS ENUM ('awaiting', 'complete');
CREATE TYPE inventory_request_status_enum AS ENUM ('pending', 'approved', 'rejected');
CREATE TYPE inventory_transaction_type_enum AS ENUM ('in', 'out');
CREATE TYPE inventory_transaction_reason_enum AS ENUM ('service', 'shipment');

CREATE TABLE user_account(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username TEXT NOT NULL,
    gender gender_enum NOT NULL,
    role account_role_enum NOT NULL DEFAULT 'customer',
    phone_number VARCHAR(20) NOT NULL,
    email TEXT,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    account_status account_status_enum DEFAULT 'active'
);

CREATE UNIQUE INDEX uq_user_phone_active
    ON user_account(phone_number)
    WHERE account_status = 'active';

CREATE UNIQUE INDEX uq_user_email_active
    ON user_account(LOWER(email))
    WHERE account_status = 'active';

CREATE TABLE staff(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id INT NOT NULL UNIQUE REFERENCES user_account(id),
    salary NUMERIC(12,2) NOT NULL CHECK (salary > 0),
    date_hired DATE NOT NULL DEFAULT CURRENT_DATE,
    end_of_contract_date DATE,
    position staff_position_enum NOT NULL DEFAULT 'staff',
    staff_status staff_status_enum NOT NULL DEFAULT 'active'
);

CREATE TABLE staff_salary(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    role account_role_enum NOT NULL UNIQUE,
    base NUMERIC(12,2) NOT NULL CHECK (base > 0)
);

INSERT INTO staff_salary(role, base) VALUES
    ('staff', 10000.0),
    ('manager', 20000.0),
    ('owner', 30000.0);

CREATE TABLE promotion(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    code TEXT NOT NULL UNIQUE,
    discount_type discount_type_enum NOT NULL DEFAULT 'amount',
    discount_amount NUMERIC(12,4) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    max_usage INT NOT NULL DEFAULT 0,
    used_count INT NOT NULL DEFAULT 0,
    status promotion_status_enum NOT NULL DEFAULT 'deactivated',
    CHECK (used_count <= max_usage),
    CHECK (
        (discount_type = 'percentage' AND discount_amount > 0 AND discount_amount <= 1)
        OR (discount_type = 'amount' AND discount_amount > 0)
    )
);

CREATE TABLE appointment(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    registered_at TIMESTAMP NOT NULL DEFAULT NOW(),
    scheduled_at TIMESTAMP,
    name TEXT NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    status appointment_status_enum NOT NULL DEFAULT 'pending',
    CHECK (scheduled_at IS NULL OR scheduled_at >= registered_at)
);

CREATE TABLE appointment_details(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    appointment_id INT NOT NULL REFERENCES appointment(id),
    user_id INT REFERENCES user_account(id),
    scheduled_start TIMESTAMP NOT NULL,
    scheduled_end TIMESTAMP NOT NULL,
    actual_start TIMESTAMP,
    actual_end TIMESTAMP,
    duration_minutes INT GENERATED AS (
        CASE WHEN actual_end IS NOT NULL AND actual_start IS NOT NULL
        THEN ROUND(EXTRACT(EPOCH FROM (actual_end - actual_start)) / 60)::int
        ELSE NULL END
    ) STORED,
    UNIQUE (appointment_id, user_id),
    CHECK (scheduled_end > scheduled_start),
    CHECK (
        (actual_start IS NULL AND actual_end IS NULL) OR
        (actual_start IS NULL AND actual_end IS NOT NULL) OR
        (actual_start IS NOT NULL AND actual_end IS NULL) OR
        (actual_start IS NOT NULL AND actual_end > actual_start)
        )
);

CREATE TABLE appointment_invoice(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    appointment_id INT NOT NULL REFERENCES appointment(id),
    total_price NUMERIC(12,2) NOT NULL CHECK (total_price >= 0),
    promotion_id INT REFERENCES promotion(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE appointment_feedback(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    appointment_id INT NOT NULL UNIQUE REFERENCES appointment(id),
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT
);

CREATE TABLE appointment_feedback_image(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    appointment_feedback_id INT NOT NULL REFERENCES appointment_feedback(id),
    image_path TEXT NOT NULL
);

CREATE TABLE satisfaction_rating(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    appointment_id INT NOT NULL UNIQUE REFERENCES appointment(id),
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT
);

CREATE TABLE service_category(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE service(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    service_name TEXT NOT NULL,
    service_category_id INT NOT NULL REFERENCES service_category(id),
    type service_type_enum NOT NULL DEFAULT 'single',
    price NUMERIC(12,2) CHECK (
        (type = 'single' AND price IS NOT NULL AND price > 0) OR
        (type = 'combo')
    ),
    duration_minutes INT NOT NULL CHECK (duration_minutes > 0),
    description TEXT
);

CREATE TABLE service_combo(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    combo_id INT NOT NULL REFERENCES service(id),
    service_id INT NOT NULL REFERENCES service(id),
    UNIQUE (combo_id, service_id)
);

CREATE TABLE service_image(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    service_id INT NOT NULL REFERENCES service(id),
    state service_image_state_enum NOT NULL,
    image_path TEXT,
    UNIQUE (service_id, state)
);

CREATE TABLE requested_service(
    appointment_id INT NOT NULL REFERENCES appointment(id),
    service_id INT NOT NULL REFERENCES service(id),
    PRIMARY KEY (appointment_id, service_id)
);

CREATE TABLE reminder_log(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id INT NOT NULL REFERENCES user_account(id),
    reminder_reason reminder_reason_enum NOT NULL,
    reminder_type reminder_type_enum NOT NULL,
    reminded_date DATE
);

CREATE TABLE loyalty_level(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
    point_required INT NOT NULL CHECK (point_required >= 0)
);

INSERT INTO loyalty_level (name, point_required) VALUES
     ('Bronze', 0),
     ('Silver', 10000),
     ('Gold', 50000),
     ('Platinum', 100000);

CREATE TABLE loyalty(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id INT NOT NULL UNIQUE REFERENCES user_account(id),
    point INT NOT NULL DEFAULT 0 CHECK (point >= 0),
    level_id INT NOT NULL REFERENCES loyalty_level(id) DEFAULT 1
);

CREATE TABLE shift(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    shift_start TIMESTAMP NOT NULL,
    shift_end TIMESTAMP NOT NULL,
    CHECK (shift_end > shift_start),
    UNIQUE (shift_start, shift_end)
);

CREATE TABLE shift_assignment(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    shift_id INT NOT NULL REFERENCES shift(id),
    assignment_date DATE NOT NULL,
    assigned_staff INT NOT NULL REFERENCES staff(id),
    UNIQUE (shift_id, assignment_date, assigned_staff)
);

CREATE TABLE shift_attendance(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    assignment_id INT NOT NULL REFERENCES shift_assignment(id),
    check_in TIMESTAMP,
    check_out TIMESTAMP,
    duration_hours NUMERIC(12,2) GENERATED AS (
        CASE WHEN check_in IS NOT NULL AND check_out IS NOT NULL 
        THEN EXTRACT(EPOCH FROM (check_out - check_in)) / 3600
        ELSE NULL END
    ) STORED,
    status shift_attendance_status_enum NOT NULL DEFAULT 'missed',
    CHECK (
        (check_in IS NULL AND check_out IS NULL) OR
        (check_in IS NOT NULL AND check_out IS NULL) OR
        (check_in IS NOT NULL AND check_out > check_in)
        )
);

CREATE TABLE product(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL,
    unit_price NUMERIC(12,2) NOT NULL CHECK (unit_price > 0),
    description TEXT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE product_image(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_id INT NOT NULL REFERENCES product(id),
    image_path TEXT
);

CREATE TABLE supplier(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
    phone_number VARCHAR(20),
    email TEXT,
    note TEXT
);

CREATE TABLE expense_category(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE expense(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    category INT NOT NULL REFERENCES expense_category(id),
    amount NUMERIC(12,2) NOT NULL CHECK (amount > 0),
    note TEXT,
    date DATE NOT NULL
);

CREATE TABLE financial_transaction_category(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE financial_transaction(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    category INT NOT NULL REFERENCES financial_transaction_category(id),
    amount NUMERIC(12,2) NOT NULL CHECK (amount > 0),
    note TEXT,
    date DATE NOT NULL
);

CREATE TABLE promotion_condition(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    promotion_id INT NOT NULL REFERENCES promotion(id),
    min_user_level_id INT REFERENCES loyalty_level(id),
    min_bill NUMERIC(12,2),
    first_time_user BOOLEAN
);

CREATE TABLE promotion_redemption(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    promotion_id INT NOT NULL REFERENCES promotion(id),
    user_id INT NOT NULL REFERENCES user_account(id),
    redeemed_date TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE location(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    location TEXT NOT NULL
);

CREATE TABLE cart(
    user_id INT NOT NULL REFERENCES user_account(id),
    product_id INT NOT NULL REFERENCES product(id),
    amount INT NOT NULL,
    PRIMARY KEY (user_id, product_id)
);

CREATE TABLE inventory_invoice(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    staff_id INT NOT NULL REFERENCES staff(id),
    supplier_id INT NOT NULL REFERENCES supplier(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    note TEXT, 
    status inventory_invoice_status_enum NOT NULL DEFAULT 'awaiting'
);

CREATE TABLE inventory_invoice_detail(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    inventory_invoice_id INT NOT NULL REFERENCES inventory_invoice(id),
    product_id INT NOT NULL REFERENCES product(id),
    ordered_quantity INT NOT NULL,
    unit_price NUMERIC(12,2) NOT NULL,
    CHECK (ordered_quantity > 0),
    CHECK (unit_price > 0)
);

CREATE TABLE inventory_consignment(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    inventory_invoice_detail_id INT NOT NULL REFERENCES inventory_invoice_detail(id),
    product_id INT NOT NULL REFERENCES product(id),
    supplier_id INT NOT NULL REFERENCES supplier(id),
    received_quantity INT NOT NULL,
    CHECK (received_quantity > 0)
);

CREATE TABLE inventory_request(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    staff_id INT NOT NULL REFERENCES staff(id),
    request_date TIMESTAMP NOT NULL DEFAULT NOW(),
    status inventory_request_status_enum NOT NULL DEFAULT 'pending'
);

CREATE TABLE inventory_request_detail(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    inventory_request_id INT NOT NULL REFERENCES inventory_request(id),
    consignment_id INT NOT NULL REFERENCES inventory_consignment(id),
    product_id INT NOT NULL REFERENCES product(id),
    requested_quantity INT NOT NULL,
    expiry_date DATE NOT NULL,
    CHECK (requested_quantity > 0)
);

CREATE TABLE inventory_lot(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    consignment_id INT NOT NULL REFERENCES inventory_consignment(id),
    product_id INT NOT NULL REFERENCES product(id),
    available_quantity INT NOT NULL,
    expiry_date DATE NOT NULL,
    CHECK (available_quantity >= 0)
);

CREATE TABLE inventory_transaction(
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    lot_id INT NOT NULL REFERENCES inventory_lot(id),
    staff_id INT NOT NULL REFERENCES staff(id),
    transaction_type inventory_transaction_type_enum NOT NULL,
    transaction_time TIMESTAMP NOT NULL DEFAULT NOW(),
    quantity INT NOT NULL,
    reason inventory_transaction_reason_enum NOT NULL,
    CHECK (quantity > 0)
);

CREATE FUNCTION normalize_phone_number()
RETURNS trigger AS $$
BEGIN
    IF NEW.phone_number IS NOT NULL THEN
        NEW.phone_number := REPLACE(TRIM(NEW.phone_number), ' ', '');
        IF NEW.phone_number !~ '^\+?\d+$' THEN
            RAISE EXCEPTION 'Invalid phone number: %', NEW.phone_number;
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE FUNCTION normalize_email_and_phone()
RETURNS trigger AS $$
BEGIN
    IF NEW.email IS NOT NULL THEN
        NEW.email := LOWER(TRIM(NEW.email));

        IF NEW.email !~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$' THEN
            RAISE EXCEPTION 'Invalid email format: %', NEW.email;
        END IF;
    END IF;
    IF NEW.phone_number IS NOT NULL THEN
        NEW.phone_number := REPLACE(TRIM(NEW.phone_number), ' ', '');
        IF NEW.phone_number !~ '^\+?\d+$' THEN
            RAISE EXCEPTION 'Invalid phone number: %', NEW.phone_number;
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE FUNCTION create_staff()
RETURNS trigger AS $$
DECLARE
    base_salary NUMERIC(12,2);
BEGIN
    IF NEW.role = ANY(ARRAY['staff', 'manager', 'owner']) THEN
        IF EXISTS (SELECT 1 FROM staff WHERE user_id = NEW.id) THEN
            RAISE NOTICE 'Staff record already exists for user %', NEW.id;
            RETURN NULL;
        END IF;

        SELECT base
        INTO base_salary
        FROM staff_salary
        WHERE role = NEW.role;

        IF NOT FOUND THEN
            RAISE EXCEPTION 'No base salary defined for role %', NEW.role;
        END IF;

        INSERT INTO staff(user_id, salary, position)
        VALUES (NEW.id, base_salary, NEW.role);

        RETURN NULL;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE FUNCTION update_staff()
RETURNS trigger AS $$
DECLARE
    base_salary NUMERIC(12,2);
    staff_roles TEXT[] := ARRAY['staff', 'manager', 'owner'];
BEGIN
    IF NEW.role = ANY(staff_roles) AND OLD.role <> NEW.role THEN
        SELECT base
        INTO base_salary
        FROM staff_salary
        WHERE role = NEW.role;

        IF NOT FOUND THEN
            RAISE EXCEPTION 'No base salary defined for role %', NEW.role;
        END IF;
        IF EXISTS (SELECT 1 FROM staff WHERE user_id = NEW.id) THEN
            UPDATE staff 
            SET position = NEW.role,
                salary = base_salary
            WHERE user_id = NEW.id;
        ELSE 
            INSERT INTO staff(user_id, salary, position)
            VALUES (NEW.id, base_salary, NEW.role);
        END IF;
    ELSIF NEW.role <> ALL(staff_roles) AND OLD.role = ANY(staff_roles) THEN
        UPDATE staff
        SET end_of_contract_date = CURRENT_DATE,
            staff_status = 'terminated'
        WHERE user_id = NEW.id;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE FUNCTION update_appointment_status_registered()
RETURNS trigger AS $$
BEGIN
    NEW.status := 'registered';

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE FUNCTION update_appointment_status_rescheduled()
RETURNS trigger AS $$
BEGIN
    NEW.status := 'rescheduled';

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE FUNCTION update_appointment_status_started()
RETURNS trigger AS $$
BEGIN
    UPDATE appointment
    SET status = 'started'
    WHERE id = NEW.appointment_id;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE FUNCTION update_appointment_status_completed()
RETURNS trigger AS $$
BEGIN
    UPDATE appointment
    SET status = 'completed'
    WHERE id = NEW.appointment_id;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE FUNCTION update_appointment_status_cancelled()
RETURNS trigger AS $$
BEGIN
    UPDATE appointment
    SET status = 'cancelled'
    WHERE id = NEW.appointment_id;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE FUNCTION create_appointment_detail()
RETURNS trigger AS $$
DECLARE
    selected_service_ids INT[];
    total_time INT;
    calculated_scheduled_end TIMESTAMP;
BEGIN
    SELECT ARRAY_AGG(service_id)
    INTO selected_service_ids
    FROM requested_service
    WHERE appointment_id = NEW.id;

    SELECT COALESCE(SUM(duration_minutes), 0)
    INTO total_time 
    FROM service
    WHERE id = ANY(selected_service_ids);

    calculated_scheduled_end := NEW.scheduled_at + make_interval(mins => total_time);

    INSERT INTO appointment_details(appointment_id, scheduled_start, scheduled_end)
    VALUES (NEW.id, NEW.scheduled_at, calculated_scheduled_end);

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE FUNCTION create_appointment_invoice()
RETURNS trigger AS $$
DECLARE
    selected_service_ids INT[];
    total_price NUMERIC(12,2);
BEGIN
    IF EXISTS (
        SELECT 1 FROM appointment_invoice WHERE appointment_id = NEW.id
    ) THEN
        RETURN NULL;
    END IF;

    SELECT ARRAY_AGG(service_id)
    INTO selected_service_ids
    FROM requested_service
    WHERE appointment_id = NEW.id;

    SELECT SUM(price)
    INTO total_price
    FROM service s
    WHERE s.type = 'single'
    AND s.id IN (
        SELECT id FROM service WHERE id = ANY(selected_service_ids) AND type = 'single'
        UNION
        SELECT sc.service_id
        FROM service_combo sc
        WHERE sc.combo_id = ANY(selected_service_ids)
    );

    INSERT INTO appointment_invoice(appointment_id, total_price)
    VALUES (NEW.id, total_price);

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE FUNCTION prevent_taking_expired_product()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.expiry_date <= CURRENT_DATE THEN
        RAISE EXCEPTION 'Expiry date must be in the future';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE FUNCTION create_inventory_consignment()
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

CREATE FUNCTION add_loyalty_record()
RETURNS trigger AS $$
BEGIN
    INSERT INTO loyalty(user_id)
    VALUES (NEW.id)
    ON CONFLICT (user_id) DO NOTHING;

    RETURN NULL;     
END;
$$ LANGUAGE plpgsql;

CREATE FUNCTION add_loyalty_point()
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
    ELSE 
        UPDATE loyalty
        SET point = point + ROUND(NEW.total_price / 100)::int
        WHERE user_id = serviced_user_id;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE FUNCTION assign_loyalty_rank()
RETURNS trigger AS $$
DECLARE
    current_rank INT;
    new_rank INT;
BEGIN
    SELECT id 
    INTO current_rank
    FROM loyalty_level
    WHERE point_required <= OLD.point
    ORDER BY point_required DESC LIMIT 1;

    SELECT id 
    INTO new_rank
    FROM loyalty_level
    WHERE point_required <= NEW.point
    ORDER BY point_required DESC LIMIT 1;

    IF current_rank IS DISTINCT FROM new_rank THEN
        UPDATE loyalty
        SET level_id = new_rank
        WHERE user_id = NEW.user_id;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER normalize_email_and_phone_trigger_account
BEFORE INSERT OR UPDATE ON user_account
FOR EACH ROW
EXECUTE FUNCTION normalize_email_and_phone();

CREATE TRIGGER normalize_email_and_phone_trigger_supplier
BEFORE INSERT OR UPDATE ON supplier
FOR EACH ROW
EXECUTE FUNCTION normalize_email_and_phone();

CREATE TRIGGER normalize_phone_number_trigger_appointment
BEFORE INSERT OR UPDATE ON appointment
FOR EACH ROW
EXECUTE FUNCTION normalize_phone_number();

CREATE TRIGGER create_staff_trigger
AFTER INSERT ON user_account
FOR EACH ROW
EXECUTE FUNCTION create_staff();

CREATE TRIGGER update_staff_trigger
AFTER UPDATE ON user_account
FOR EACH ROW
WHEN (OLD.role <> NEW.role)
EXECUTE FUNCTION update_staff();

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

CREATE TRIGGER update_appointment_status_started_trigger
AFTER UPDATE ON appointment_details
FOR EACH ROW
WHEN (NEW.actual_start IS NOT NULL AND (OLD.actual_start IS NULL OR OLD.actual_start <> NEW.actual_start))
EXECUTE FUNCTION update_appointment_status_started();

CREATE TRIGGER update_appointment_status_completed_trigger
AFTER UPDATE ON appointment_details
FOR EACH ROW
WHEN (NEW.actual_end IS NOT NULL AND OLD.actual_end IS NULL)
EXECUTE FUNCTION update_appointment_status_completed();

CREATE TRIGGER update_appointment_status_cancelled_trigger
AFTER UPDATE ON appointment_details
FOR EACH ROW
WHEN (NEW.actual_end IS NOT NULL AND NEW.actual_start IS NULL)
EXECUTE FUNCTION update_appointment_status_cancelled();

CREATE TRIGGER create_appointment_detail_trigger
AFTER UPDATE ON appointment
FOR EACH ROW
WHEN (NEW.scheduled_at IS NOT NULL AND NEW.status = 'registered')
EXECUTE FUNCTION create_appointment_detail();

CREATE TRIGGER create_appointment_invoice_trigger
AFTER UPDATE ON appointment
FOR EACH ROW
WHEN (NEW.status = 'completed' AND OLD.status IS DISTINCT FROM NEW.status)
EXECUTE FUNCTION create_appointment_invoice();

CREATE TRIGGER prevent_taking_expired_product_trigger
BEFORE INSERT ON inventory_request_detail
FOR EACH ROW
EXECUTE FUNCTION prevent_taking_expired_product();

CREATE TRIGGER create_inventory_consignment_trigger
AFTER INSERT ON inventory_invoice_detail
FOR EACH ROW
EXECUTE FUNCTION create_inventory_consignment();

CREATE TRIGGER add_loyalty_record_trigger
AFTER INSERT ON user_account
FOR EACH ROW
EXECUTE FUNCTION add_loyalty_record();

CREATE TRIGGER add_loyalty_point_trigger
AFTER INSERT OR UPDATE ON appointment_invoice
FOR EACH ROW
EXECUTE FUNCTION add_loyalty_point();

CREATE TRIGGER assign_loyalty_rank_trigger
AFTER UPDATE ON loyalty
FOR EACH ROW
WHEN (NEW.point <> OLD.point)
EXECUTE FUNCTION assign_loyalty_rank();