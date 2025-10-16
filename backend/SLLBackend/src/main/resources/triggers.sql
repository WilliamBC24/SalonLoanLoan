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

CREATE TRIGGER create_staff_account_trigger
    AFTER INSERT
    ON staff
    FOR EACH ROW
    EXECUTE FUNCTION create_staff_account();

^;