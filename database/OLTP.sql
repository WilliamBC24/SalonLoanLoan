    --STORE MONEY AS THE SMALLEST UNIT WITH INT
    CREATE EXTENSION IF NOT EXISTS pgcrypto;
    CREATE EXTENSION IF NOT EXISTS pg_trgm;

    -- CREATE TYPE gender_enum AS ENUM ('MALE', 'FEMALE');
    -- CREATE TYPE account_status_enum AS ENUM ('ACTIVE', 'DEACTIVATED', 'BANNED');
    -- CREATE TYPE commission_type_enum AS ENUM ('APPOINTMENT', 'PRODUCT');
    -- CREATE TYPE staff_status_enum AS ENUM ('ACTIVE', 'ON_LEAVE', 'TERMINATED');
    -- CREATE TYPE appointment_status_enum AS ENUM ('PENDING', 'REGISTERED', 'REJECTED', 'STARTED', 'COMPLETED', 'RESCHEDULED', 'CANCELLED');
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

    CREATE TABLE IF NOT EXISTS user_account(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        username VARCHAR(100) NOT NULL UNIQUE,
        password VARCHAR(100) NOT NULL,
        gender TEXT NOT NULL,
        birth_date DATE,
        phone_number VARCHAR(20) NOT NULL,
        email VARCHAR(100),
        phone_verified BOOLEAN NOT NULL DEFAULT FALSE,
        account_status TEXT NOT NULL DEFAULT 'ACTIVE'
    );

    CREATE TABLE IF NOT EXISTS user_shipping_info(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        user_account_id INT NOT NULL REFERENCES user_account(id),
        shipping_address TEXT NOT NULL,
        phone_number TEXT NOT NULL
    );

    --INSERT ON TRANSACTION
    CREATE TABLE IF NOT EXISTS customer_info(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        name TEXT NOT NULL,
        phone_number VARCHAR(20) NOT NULL,
        email VARCHAR(100),
        shipping_address TEXT,
        city TEXT,
        ward TEXT
    );

    CREATE UNIQUE INDEX uq_user_phone_active
        ON user_account(phone_number)
        WHERE account_status = 'ACTIVE';

    CREATE UNIQUE INDEX uq_user_email_active
        ON user_account(LOWER(email))
        WHERE account_status = 'ACTIVE';

    CREATE TABLE IF NOT EXISTS staff(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        date_hired DATE NOT NULL DEFAULT CURRENT_DATE,
        end_of_contract_date DATE,
        social_security_num VARCHAR(60),
        name TEXT NOT NULL,
        birth_date DATE NOT NULL,
        email TEXT,
        staff_status TEXT NOT NULL DEFAULT 'ACTIVE'
    );

    CREATE TABLE IF NOT EXISTS staff_position(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        position_name TEXT NOT NULL
    );

    -- INSERT INTO staff_position(position_name) VALUES 
    -- ('staff'),
    -- ('manager'),
    -- ('admin');

    --GENERATE WITH TRIGGER
    CREATE TABLE IF NOT EXISTS staff_account(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        staff_id INT NOT NULL UNIQUE REFERENCES staff(id),
        username VARCHAR(100) NOT NULL UNIQUE,
        password VARCHAR(100) NOT NULL,
        account_status TEXT NOT NULL DEFAULT 'ACTIVE'
    );

    CREATE TABLE IF NOT EXISTS staff_current_position(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        staff_id INT NOT NULL REFERENCES staff(id),
        position_id INT NOT NULL REFERENCES staff_position(id)
    );

    CREATE TABLE IF NOT EXISTS staff_promotion_history(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        staff_id INT NOT NULL REFERENCES staff(id),
        position_id INT NOT NULL REFERENCES staff_position(id),
        effective_from TIMESTAMP NOT NULL DEFAULT NOW(),
        effective_to TIMESTAMP,
        CHECK (effective_to IS NULL OR effective_to > effective_from)
    );

    --INSERT DEFAULT VALUES
    CREATE TABLE IF NOT EXISTS staff_commission(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        position_id INT NOT NULL REFERENCES staff_position(id),
        commission_type TEXT NOT NULL,
        commission SMALLINT NOT NULL CHECK (commission >= 0 AND commission <= 100),
        UNIQUE (position_id, commission_type)
    );

    CREATE TABLE IF NOT EXISTS staff_commission_history(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        staff_commission_id INT NOT NULL REFERENCES staff_commission(id),
        commission_type TEXT NOT NULL,
        commission SMALLINT NOT NULL CHECK (commission >= 0 AND commission <= 100),
        effective_from TIMESTAMP NOT NULL DEFAULT NOW(),
        effective_to TIMESTAMP,
        CHECK (effective_to IS NULL OR effective_to > effective_from)
    );

    --CALCULATE TOTAL WITH TRIGGER USING COMMISSION, APPOINTMENT AND ADJUSTMENT, MONTHLY
    CREATE TABLE IF NOT EXISTS staff_payroll(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        staff_id INT NOT NULL REFERENCES staff(id),
        pay_period_start DATE NOT NULL,
        pay_period_end DATE NOT NULL,
        appointment_commission INT NOT NULL CHECK (appointment_commission >= 0),
        product_commission INT NOT NULL CHECK (product_commission >= 0),
        payroll_deduction INT NOT NULL DEFAULT 0 CHECK (payroll_deduction >= 0),
        payroll_bonus INT NOT NULL DEFAULT 0 CHECK (payroll_bonus >= 0),
        total_payment INT GENERATED ALWAYS AS (appointment_commission + product_commission + payroll_bonus - payroll_deduction) STORED
    );

    CREATE TABLE IF NOT EXISTS staff_payroll_adjustment (
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        staff_id INT NOT NULL REFERENCES staff(id),
        adjustment_type TEXT NOT NULL,
        amount INT NOT NULL CHECK (amount > 0),
        note TEXT,
        effective_date DATE NOT NULL DEFAULT CURRENT_DATE
    );

    CREATE TABLE IF NOT EXISTS voucher_status(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        name TEXT NOT NULL
    );

    CREATE TABLE IF NOT EXISTS promotion_status(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        name TEXT NOT NULL
    );

    CREATE TABLE IF NOT EXISTS voucher(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        voucher_name TEXT NOT NULL,
        voucher_description TEXT NOT NULL,
        voucher_code TEXT NOT NULL UNIQUE,
        discount_type TEXT NOT NULL DEFAULT 'AMOUNT',
        discount_amount INT NOT NULL,
        effective_from TIMESTAMP NOT NULL DEFAULT NOW(),
        effective_to TIMESTAMP NOT NULL,
        max_usage INT NOT NULL CHECK (max_usage > 0),
        used_count INT NOT NULL DEFAULT 0,
        voucher_status INT NOT NULL REFERENCES voucher_status(id),
        CHECK (used_count <= max_usage),
        CHECK (
            (discount_type = 'PERCENTAGE' AND discount_amount > 0 AND discount_amount <= 100)
            OR (discount_type = 'AMOUNT' AND discount_amount > 0)
        )
    );

    CREATE TABLE IF NOT EXISTS promotion(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        promotion_name TEXT NOT NULL,
        promotion_description TEXT,
        discount_type TEXT NOT NULL DEFAULT 'AMOUNT',
        discount_amount INT NOT NULL,
        effective_from TIMESTAMP NOT NULL DEFAULT NOW(),
        effective_to TIMESTAMP NOT NULL,
        promotion_status INT NOT NULL REFERENCES promotion_status(id),
        CHECK (
            (discount_type = 'PERCENTAGE' AND discount_amount > 0 AND discount_amount <= 100)
            OR (discount_type = 'AMOUNT' AND discount_amount > 0)
        )
    );

    CREATE TABLE IF NOT EXISTS appointment(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        registered_at TIMESTAMP NOT NULL DEFAULT NOW(),
        scheduled_at TIMESTAMP,
        name TEXT NOT NULL,
        phone_number VARCHAR(20) NOT NULL,
        status TEXT NOT NULL DEFAULT 'PENDING',
        preferred_staff_id INT REFERENCES staff(id),
        responsible_staff_id INT REFERENCES staff(id)
    );

    CREATE TABLE IF NOT EXISTS appointment_details(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        appointment_id INT NOT NULL REFERENCES appointment(id),
        user_id INT REFERENCES user_account(id),
        scheduled_start TIMESTAMP,
        scheduled_end TIMESTAMP,
        actual_start TIMESTAMP,
        actual_end TIMESTAMP,
        duration_minutes INT GENERATED ALWAYS AS (
            CASE WHEN actual_end IS NOT NULL AND actual_start IS NOT NULL
            THEN ROUND(EXTRACT(EPOCH FROM (actual_end - actual_start)) / 60)::int
            ELSE NULL END
        ) STORED,
        CHECK (scheduled_end > scheduled_start),
        CHECK (
            (actual_start IS NULL AND actual_end IS NULL) OR
            (actual_start IS NULL AND actual_end IS NOT NULL) OR
            (actual_start IS NOT NULL AND actual_end IS NULL) OR
            (actual_start IS NOT NULL AND actual_end > actual_start)
            )
    );

    CREATE TABLE IF NOT EXISTS payment_type(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        name TEXT NOT NULL
    );

    CREATE TABLE IF NOT EXISTS appointment_invoice(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        appointment_id INT NOT NULL REFERENCES appointment(id),
        total_price INT NOT NULL CHECK (total_price >= 0),
        payment_type_id INT NOT NULL REFERENCES payment_type(id),
        voucher_id INT REFERENCES voucher(id),
        responsible_staff_id INT NOT NULL REFERENCES staff(id),
        created_at TIMESTAMP NOT NULL DEFAULT NOW()
    );

    CREATE TABLE IF NOT EXISTS appointment_feedback(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        appointment_id INT NOT NULL UNIQUE REFERENCES appointment(id),
        rating SMALLINT NOT NULL CHECK (rating >= 1 AND rating <= 5),
        comment TEXT
    );

    CREATE TABLE IF NOT EXISTS before_appointment_image(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        appointment_id INT NOT NULL REFERENCES appointment(id),
        image_path TEXT NOT NULL
    );

    CREATE TABLE IF NOT EXISTS after_appointment_image(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        appointment_id INT NOT NULL REFERENCES appointment(id),
        image_path TEXT NOT NULL
    );

    CREATE TABLE IF NOT EXISTS appointment_feedback_image(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        appointment_feedback_id INT NOT NULL REFERENCES appointment_feedback(id),
        image_path TEXT NOT NULL
    );

    CREATE TABLE IF NOT EXISTS satisfaction_rating(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        appointment_id INT NOT NULL UNIQUE REFERENCES appointment(id),
        rating SMALLINT NOT NULL CHECK (rating >= 1 AND rating <= 5),
        comment TEXT
    );

    CREATE TABLE IF NOT EXISTS service_category(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        name TEXT NOT NULL
    );

    -- CHANGE PRICE WITH TRIGGER
    CREATE TABLE IF NOT EXISTS service(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        service_name TEXT NOT NULL,
        service_category_id INT NOT NULL REFERENCES service_category(id),
        service_type TEXT NOT NULL DEFAULT 'SINGLE',
        service_price INT NOT NULL CHECK (service_price > 0),
        duration_minutes SMALLINT NOT NULL CHECK (duration_minutes > 0),
        service_description TEXT,
        active_status BOOLEAN NOT NULL DEFAULT FALSE
    );

    CREATE UNIQUE INDEX unique_active_service_name
        ON service(service_name)
        WHERE active_status = TRUE;

    CREATE TABLE IF NOT EXISTS service_change_history(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        service_id INT NOT NULL REFERENCES service(id),
        service_name TEXT NOT NULL,
        service_category_id INT NOT NULL REFERENCES service_category(id),
        service_price INT NOT NULL CHECK (service_price > 0),
        duration_minutes SMALLINT NOT NULL CHECK (duration_minutes > 0),
        service_description TEXT,
        effective_from TIMESTAMP NOT NULL DEFAULT NOW(),
        effective_to TIMESTAMP,
        CHECK (effective_to IS NULL OR effective_to > effective_from)
    );

    CREATE TABLE IF NOT EXISTS service_combo(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        combo_id INT NOT NULL REFERENCES service(id),
        service_id INT NOT NULL REFERENCES service(id),
        UNIQUE (combo_id, service_id),
        CHECK (combo_id <> service_id)
    );

    CREATE TABLE IF NOT EXISTS service_image(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        service_id INT NOT NULL REFERENCES service(id),
        image_path TEXT
    );

    CREATE TABLE IF NOT EXISTS requested_service(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        appointment_id INT NOT NULL REFERENCES appointment(id),
        service_id INT NOT NULL REFERENCES service(id),
        price_at_booking INT NOT NULL DEFAULT 0 CHECK (price_at_booking >= 0),
        responsible_staff_id INT REFERENCES staff(id)
    );

    CREATE TABLE IF NOT EXISTS reminder_reason(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        name TEXT NOT NULL
    );

    CREATE TABLE IF NOT EXISTS reminder_type(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        name TEXT NOT NULL
    );

    CREATE TABLE IF NOT EXISTS reminder_log(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        user_account_id INT NOT NULL REFERENCES user_account(id),
        reminder_reason_id INT NOT NULL REFERENCES reminder_reason(id),
        reminder_type_id INT NOT NULL REFERENCES reminder_type(id),
        reminded_date DATE
    );

    CREATE TABLE IF NOT EXISTS shift_template(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        shift_start TIME NOT NULL,
        shift_end TIME NOT NULL,
        CHECK (shift_end > shift_start),
        UNIQUE (shift_start, shift_end)
    );

    CREATE TABLE IF NOT EXISTS shift_instance(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        shift_template_id INT NOT NULL REFERENCES shift_template(id),
        shift_date DATE NOT NULL
    );

    CREATE TABLE IF NOT EXISTS shift_assignment(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        shift_instance_id INT NOT NULL REFERENCES shift_instance(id),
        assigned_staff_id INT NOT NULL REFERENCES staff(id),
        UNIQUE (shift_instance_id, assigned_staff_id)
    );

    CREATE TABLE IF NOT EXISTS shift_assignment_history(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        shift_instance_id INT NOT NULL REFERENCES shift_instance(id),
        assigned_staff_id INT NOT NULL REFERENCES staff(id)
    );

    CREATE TABLE IF NOT EXISTS shift_attendance(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        shift_assignment_id INT NOT NULL REFERENCES shift_assignment(id),
        check_in TIMESTAMP,
        check_out TIMESTAMP,
        duration_minutes INT GENERATED ALWAYS AS (
            CASE WHEN check_in IS NOT NULL AND check_out IS NOT NULL 
            THEN ROUND(EXTRACT(EPOCH FROM (check_out - check_in)) / 60)::int
            ELSE NULL END
        ) STORED,
        status TEXT NOT NULL DEFAULT 'MISSED',
        CHECK (
            (check_in IS NULL AND check_out IS NULL) OR
            (check_in IS NOT NULL AND check_out IS NULL) OR
            (check_in IS NOT NULL AND check_out > check_in)
            )
    );

    CREATE TABLE IF NOT EXISTS shift_cash_payment_record(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        shift_instance_id INT NOT NULL REFERENCES shift_instance(id),
        amount INT NOT NULL CHECK (amount >= 0),
        recorded_at TIMESTAMP NOT NULL DEFAULT NOW(),
        responsible_staff_id INT NOT NULL REFERENCES staff(id),
        note TEXT
    );

    -- CHANGE PRICE WITH TRIGGER
    CREATE TABLE IF NOT EXISTS product(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        product_name TEXT NOT NULL,
        current_price INT NOT NULL CHECK (current_price > 0),
        product_description TEXT NOT NULL,
        active_status BOOLEAN NOT NULL DEFAULT FALSE
    );

    CREATE UNIQUE INDEX unique_active_product_name
        ON product(product_name)
        WHERE active_status = TRUE;

    CREATE TABLE IF NOT EXISTS product_change_history(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        product_id INT NOT NULL REFERENCES product(id),
        product_name TEXT NOT NULL, 
        unit_price INT NOT NULL CHECK (unit_price > 0),
        product_description TEXT NOT NULL,
        effective_from TIMESTAMP NOT NULL DEFAULT NOW(),
        effective_to TIMESTAMP,
        CHECK (effective_to IS NULL OR effective_to > effective_from)
    );

    CREATE TABLE IF NOT EXISTS product_image(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        product_id INT NOT NULL REFERENCES product(id),
        image_path TEXT
    );

    CREATE TABLE IF NOT EXISTS supplier_category(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        name TEXT NOT NULL
    );

    CREATE TABLE IF NOT EXISTS supplier(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        supplier_name VARCHAR(100) NOT NULL UNIQUE,
        phone_number VARCHAR(20),
        email VARCHAR(100),
        supplier_category_id INT NOT NULL REFERENCES supplier_category(id),
        note TEXT
    );

    CREATE TABLE IF NOT EXISTS expense_category(
        id SMALLINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        name TEXT NOT NULL
    );

    CREATE TABLE IF NOT EXISTS expense(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        expense_category SMALLINT NOT NULL REFERENCES expense_category(id),
        amount INT NOT NULL CHECK (amount > 0),
        note TEXT,
        date_incurred DATE NOT NULL
    );

    CREATE TABLE IF NOT EXISTS financial_transaction_category(
        id SMALLINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        name TEXT NOT NULL
    );

    --FOR INCOMING AND OUTGOING MONEY, FOR ANALYTICS
    CREATE TABLE IF NOT EXISTS financial_transaction(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        financial_transaction_category_id SMALLINT NOT NULL REFERENCES financial_transaction_category(id),
        amount INT NOT NULL CHECK (amount > 0),
        note TEXT,
        time_incurred TIMESTAMP NOT NULL
    );

    CREATE TABLE IF NOT EXISTS location(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        location TEXT NOT NULL
    );

    CREATE TABLE IF NOT EXISTS cart(
        user_account_id INT NOT NULL REFERENCES user_account(id),
        product_id INT NOT NULL REFERENCES product(id),
        amount INT NOT NULL,
        PRIMARY KEY (user_account_id, product_id)
    );

    CREATE TABLE IF NOT EXISTS order_invoice(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        user_account_id INT REFERENCES user_account(id),
        customer_info_id INT REFERENCES customer_info(id),
        total_price INT NOT NULL CHECK (total_price > 0),
        payment_method TEXT NOT NULL,
        fulfillment_type TEXT NOT NULL DEFAULT 'DELIVERY',
        shipping_fee INT NOT NULL DEFAULT 0 CHECK (shipping_fee >= 0),
        order_status TEXT NOT NULL DEFAULT 'PENDING',
        created_at TIMESTAMP NOT NULL DEFAULT NOW()
    );

    CREATE TABLE IF NOT EXISTS order_invoice_details(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        order_invoice_id INT NOT NULL REFERENCES order_invoice(id),
        product_id INT NOT NULL REFERENCES product(id),
        quantity INT NOT NULL CHECK (quantity > 0),
        price_at_sale INT NOT NULL CHECK (price_at_sale > 0),
        subtotal INT GENERATED ALWAYS AS (price_at_sale * quantity) STORED
    );

    CREATE TABLE IF NOT EXISTS inventory_invoice(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        staff_id INT NOT NULL REFERENCES staff(id),
        supplier_id INT NOT NULL REFERENCES supplier(id),
        created_at TIMESTAMP NOT NULL DEFAULT NOW(),
        note TEXT, 
        invoice_status TEXT NOT NULL DEFAULT 'AWAITING'
    );

    CREATE TABLE IF NOT EXISTS inventory_invoice_detail(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        inventory_invoice_id INT NOT NULL REFERENCES inventory_invoice(id),
        product_id INT NOT NULL REFERENCES product(id),
        ordered_quantity INT NOT NULL,
        unit_price INT NOT NULL,
        subtotal INT GENERATED ALWAYS AS (unit_price * ordered_quantity) STORED,
        CHECK (ordered_quantity > 0),
        CHECK (unit_price > 0)
    );

    CREATE TABLE IF NOT EXISTS inventory_consignment(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        inventory_invoice_detail_id INT NOT NULL REFERENCES inventory_invoice_detail(id),
        product_id INT NOT NULL REFERENCES product(id),
        supplier_id INT NOT NULL REFERENCES supplier(id),
        received_quantity INT NOT NULL,
        CHECK (received_quantity > 0)
    );

    CREATE TABLE IF NOT EXISTS inventory_request(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        staff_id INT NOT NULL REFERENCES staff(id),
        request_date TIMESTAMP NOT NULL DEFAULT NOW(),
        inventory_request_status TEXT NOT NULL DEFAULT 'PENDING'
    );

    CREATE TABLE IF NOT EXISTS inventory_request_detail(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        inventory_request_id INT NOT NULL REFERENCES inventory_request(id),
        inventory_consignment_id INT NOT NULL REFERENCES inventory_consignment(id),
        product_id INT NOT NULL REFERENCES product(id),
        requested_quantity INT NOT NULL,
        product_expiry_date DATE NOT NULL,
        CHECK (requested_quantity > 0)
    );

    CREATE TABLE IF NOT EXISTS inventory_lot(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        inventory_consignment_id INT NOT NULL REFERENCES inventory_consignment(id),
        product_id INT NOT NULL REFERENCES product(id),
        available_quantity INT NOT NULL,
        product_expiry_date DATE NOT NULL,
        CHECK (available_quantity >= 0)
    );

    CREATE TABLE IF NOT EXISTS inventory_transaction(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        inventory_lot_id INT NOT NULL REFERENCES inventory_lot(id),
        staff_id INT NOT NULL REFERENCES staff(id),
        transaction_type TEXT NOT NULL,
        transaction_time TIMESTAMP NOT NULL DEFAULT NOW(),
        quantity INT NOT NULL,
        reason TEXT NOT NULL,
        CHECK (quantity > 0)
    );

    CREATE TABLE IF NOT EXISTS loyalty_level(
        id SMALLINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        name TEXT NOT NULL UNIQUE,
        point_required INT NOT NULL CHECK (point_required >= 0)
    );

    -- INSERT INTO loyalty_level (name, point_required) VALUES
    --     ('Bronze', 0),
    --     ('Silver', 10000),
    --     ('Gold', 50000),
    --     ('Platinum', 100000);

    CREATE TABLE IF NOT EXISTS loyalty(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        user_id INT NOT NULL UNIQUE REFERENCES user_account(id),
        point INT NOT NULL DEFAULT 0 CHECK (point >= 0),
        level_id SMALLINT NOT NULL REFERENCES loyalty_level(id) DEFAULT 1
    );

    CREATE TABLE IF NOT EXISTS loyalty_history(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        loyalty_id INT NOT NULL REFERENCES loyalty(id),
        appointment_invoice_id INT REFERENCES appointment_invoice(id),
        order_id INT REFERENCES order_invoice(id),
        amount INT NOT NULL,
        creditted_date DATE NOT NULL DEFAULT NOW(),
        CHECK (
            (appointment_invoice_id IS NOT NULL AND order_id IS NULL) OR
            (appointment_invoice_id IS NULL AND order_id IS NOT NULL)
        )
    );

    CREATE TABLE IF NOT EXISTS voucher_condition(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        voucher_id INT NOT NULL REFERENCES voucher(id),
        min_user_level_id SMALLINT REFERENCES loyalty_level(id),
        min_bill INT CHECK (min_bill IS NULL OR min_bill > 0),
        first_time_user BOOLEAN
    );

    CREATE TABLE IF NOT EXISTS voucher_redemption(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        voucher_id INT NOT NULL REFERENCES voucher(id),
        user_account_id INT NOT NULL REFERENCES user_account(id),
        redeemed_date TIMESTAMP NOT NULL DEFAULT NOW(),
        UNIQUE (voucher_id, user_account_id)
    );

    CREATE TABLE IF NOT EXISTS job_posting(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        job_posting_name TEXT NOT NULL,
        job_posting_description TEXT NOT NULL,
        max_application INT NOT NULL CHECK (max_application > 0),
        effective_from DATE NOT NULL DEFAULT CURRENT_DATE,
        effective_to DATE CHECK (effective_to IS NULL OR effective_to >= effective_from),
        status TEXT NOT NULL DEFAULT 'DEACTIVATED'
    );

    CREATE TABLE IF NOT EXISTS job_posting_application(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        job_posting_id INT NOT NULL REFERENCES job_posting(id),
        applicant_name TEXT NOT NULL,
        applicant_dob DATE NOT NULL,
        applicant_phone_number VARCHAR(20) NOT NULL,
        application_date TIMESTAMP NOT NULL DEFAULT NOW(),
        status TEXT NOT NULL DEFAULT 'PENDING'
    );

    CREATE TABLE IF NOT EXISTS product_feedback(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        user_account_id INT NOT NULL REFERENCES user_account(id),
        product_id INT NOT NULL REFERENCES product(id),
        rating SMALLINT NOT NULL CHECK (rating >= 1 AND rating <= 5),
        comment TEXT
    );

    CREATE TABLE IF NOT EXISTS product_feedback_image(
        id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        product_feedback_id INT NOT NULL REFERENCES product_feedback(id),
        image_path TEXT NOT NULL
    );

    CREATE OR REPLACE FUNCTION normalize_phone_number()
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

    CREATE OR REPLACE FUNCTION normalize_email_and_phone()
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

    CREATE OR REPLACE FUNCTION create_staff_account()
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

        --Could probably be more efficient
        WHILE EXISTS (SELECT 1 FROM staff_account WHERE username = staff_username) LOOP
            suffix := suffix + 1;
            staff_username := base_username || suffix;
        END LOOP;

        INSERT INTO staff_account(staff_id, username, password)
        VALUES (NEW.id, staff_username, crypt('default123', gen_salt('bf')));

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

    -- CREATE OR REPLACE FUNCTION create_appointment_invoice()
    -- RETURNS trigger AS $$
    -- DECLARE
    --     selected_service_ids INT[];
    --     total_price INT;
    -- BEGIN
    --     IF EXISTS (
    --         SELECT 1 FROM appointment_invoice WHERE appointment_id = NEW.id
    --     ) THEN
    --         RETURN NULL;
    --     END IF;

    --     SELECT ARRAY_AGG(service_id)
    --     INTO selected_service_ids
    --     FROM requested_service
    --     WHERE appointment_id = NEW.id;

    --     SELECT SUM(price)
    --     INTO total_price
    --     FROM service s
    --     WHERE s.type = 'single'
    --     AND s.id IN (
    --         SELECT id FROM service WHERE id = ANY(selected_service_ids) AND type = 'single'
    --         UNION
    --         SELECT sc.service_id
    --         FROM service_combo sc
    --         WHERE sc.combo_id = ANY(selected_service_ids)
    --     );

    --     INSERT INTO appointment_invoice(appointment_id, total_price)
    --     VALUES (NEW.id, total_price);

    --     RETURN NULL;
    -- END;
    -- $$ LANGUAGE plpgsql;

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

    CREATE OR REPLACE FUNCTION add_loyalty_record()
    RETURNS trigger AS $$
    BEGIN
        INSERT INTO loyalty(user_id)
        VALUES (NEW.id)
        ON CONFLICT (user_id) DO NOTHING;

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

    CREATE OR REPLACE FUNCTION assign_loyalty_rank()
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

    CREATE TRIGGER create_staff_account_trigger
    AFTER INSERT ON staff
    FOR EACH ROW
    EXECUTE FUNCTION create_staff_account();

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

    CREATE TRIGGER create_appointment_detail_trigger
    AFTER INSERT OR UPDATE OF scheduled_at ON appointment
    FOR EACH ROW
    WHEN (NEW.scheduled_at IS NOT NULL AND NEW.status = 'REGISTERED')
    EXECUTE FUNCTION create_appointment_detail();

    --TODO: propagate details on date update

    -- CREATE TRIGGER create_appointment_invoice_trigger
    -- AFTER UPDATE ON appointment
    -- FOR EACH ROW
    -- WHEN (NEW.status = 'COMPLETED' AND OLD.status IS DISTINCT FROM NEW.status)
    -- EXECUTE FUNCTION create_appointment_invoice();

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

    CREATE TRIGGER credit_loyalty_point_trigger
    AFTER INSERT ON appointment_invoice
    FOR EACH ROW
    EXECUTE FUNCTION credit_loyalty_point();

    CREATE TRIGGER update_service_combo_details_trigger
    AFTER INSERT ON service
    FOR EACH ROW
    WHEN (NEW.service_type = 'COMBO')
    EXECUTE FUNCTION update_service_combo_details();

    CREATE TRIGGER propagate_service_details_to_combo_trigger
    AFTER UPDATE OF service_price, duration_minutes ON service
    FOR EACH ROW
    EXECUTE FUNCTION propagate_service_details_to_combo();

    CREATE TRIGGER refresh_service_combo_details_trigger
    AFTER INSERT OR DELETE OR UPDATE ON service_combo
    FOR EACH ROW
    EXECUTE FUNCTION refresh_service_combo_details(); 