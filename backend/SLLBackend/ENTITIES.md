# Entity Classes Generated from OLTP.sql

This document lists all entity classes created based on the database schema in `database/OLTP.sql`.

## Summary

- **Total Entity Classes**: 65
- **Total Enumerator Classes**: 16 (3 existing + 13 new)

## Enumerators

### Existing Enumerators (3)
1. `Gender` - Male, Female
2. `AccountStatus` - Active, Deactivated, Banned
3. `AccountRole` - (Note: Not used in current schema)

### New Enumerators (13)
1. `CommissionType` - Appointment, Product
2. `StaffStatus` - Active, On_Leave, Terminated
3. `AppointmentStatus` - Pending, Registered, Started, Completed, Rescheduled, Cancelled
4. `ServiceType` - Single, Combo
5. `ShiftAttendanceStatus` - Full, Partial, Missed
6. `DiscountType` - Amount, Percentage
7. `InventoryInvoiceStatus` - Awaiting, Complete, Cancelled
8. `InventoryRequestStatus` - Pending, Approved, Rejected
9. `InventoryTransactionType` - In, Out
10. `InventoryTransactionReason` - Service, Shipment
11. `PayrollAdjustment` - Bonus, Deduction
12. `JobPostingStatus` - Active, Deactivated
13. `JobPostingApplicationStatus` - Pending, Accepted, Rejected

## Entity Classes by Category

### User and Authentication (4 entities)
1. `UserAccount` - User account information (Updated)
2. `UserShippingInfo` - User shipping addresses
3. `CustomerInfo` - Customer information for transactions
4. `PaymentType` - Payment method types

### Staff Management (8 entities)
1. `Staff` - Staff member information
2. `StaffAccount` - Staff login accounts
3. `StaffPosition` - Staff position types
4. `StaffCurrentPosition` - Current position assignments
5. `StaffPromotionHistory` - Position change history
6. `StaffCommission` - Commission rates by position
7. `StaffCommissionHistory` - Commission rate history
8. `StaffPayroll` - Staff payroll records
9. `StaffPayrollAdjustment` - Payroll adjustments (bonuses/deductions)

### Appointment Management (8 entities)
1. `Appointment` - Appointment bookings
2. `AppointmentDetails` - Appointment timing details
3. `AppointmentInvoice` - Appointment billing
4. `AppointmentFeedback` - Customer feedback
5. `AfterAppointmentImage` - Post-appointment images
6. `AppointmentFeedbackImage` - Feedback images
7. `SatisfactionRating` - Service satisfaction ratings
8. `RequestedService` - Services requested in appointments

### Service Management (6 entities)
1. `Service` - Service catalog
2. `ServiceCategory` - Service categories
3. `ServiceChangeHistory` - Service price/detail history
4. `ServiceCombo` - Combo service compositions
5. `ServiceImage` - Service images
6. `RequestedService` - Services in appointments

### Product and Inventory (12 entities)
1. `Product` - Product catalog
2. `ProductChangeHistory` - Product price/detail history
3. `ProductImage` - Product images
4. `Supplier` - Supplier information
5. `SupplierCategory` - Supplier categories
6. `InventoryInvoice` - Purchase orders
7. `InventoryInvoiceDetail` - Purchase order line items
8. `InventoryConsignment` - Received inventory batches
9. `InventoryRequest` - Inventory usage requests
10. `InventoryRequestDetail` - Request line items
11. `InventoryLot` - Available inventory lots
12. `InventoryTransaction` - Inventory movement records

### Order Management (4 entities)
1. `Cart` - Shopping cart items
2. `OrderInvoice` - Customer orders
3. `OrderInvoiceDetails` - Order line items
4. `CustomerInfo` - Customer information

### Loyalty and Promotions (9 entities)
1. `Loyalty` - Customer loyalty accounts
2. `LoyaltyLevel` - Loyalty tier definitions (Bronze, Silver, Gold, Platinum)
3. `LoyaltyHistory` - Loyalty point transactions
4. `Voucher` - Discount vouchers
5. `VoucherStatus` - Voucher status types
6. `VoucherCondition` - Voucher usage conditions
7. `VoucherRedemption` - Voucher redemption records
8. `Promotion` - Active promotions
9. `PromotionStatus` - Promotion status types

### Financial Management (4 entities)
1. `Expense` - Business expenses
2. `ExpenseCategory` - Expense categories
3. `FinancialTransaction` - Financial transaction records
4. `FinancialTransactionCategory` - Transaction categories

### Shift Management (5 entities)
1. `ShiftTemplate` - Shift time templates
2. `ShiftInstance` - Scheduled shift instances
3. `ShiftAssignment` - Staff shift assignments
4. `ShiftAttendance` - Shift check-in/out records
5. `ShiftCashPaymentRecord` - Cash payments during shifts

### Miscellaneous (5 entities)
1. `Location` - Location/branch information
2. `ReminderLog` - Customer reminder records
3. `ReminderReason` - Reminder reason types
4. `ReminderType` - Reminder delivery types
5. `JobPosting` - Job postings
6. `JobPostingApplication` - Job applications

## Key Features

### JPA Annotations Used
- `@Entity` - Marks class as JPA entity
- `@Table` - Maps to database table
- `@Id` - Primary key field
- `@GeneratedValue` - Auto-generated ID (IDENTITY strategy)
- `@Column` - Column mapping with constraints
- `@ManyToOne` / `@OneToOne` - Relationships
- `@Enumerated` - Enum field mapping
- `@JdbcType(PostgreSQLEnumJdbcType.class)` - PostgreSQL enum support
- `@Builder.Default` - Default values for builder pattern

### Lombok Annotations Used
- `@Data` - Generates getters, setters, toString, equals, hashCode
- `@Builder` - Builder pattern support
- `@NoArgsConstructor` - No-argument constructor
- `@AllArgsConstructor` - All-arguments constructor

### Special Considerations
1. **Generated Columns**: Some entities have comments noting fields that are database-generated (e.g., `duration_minutes`, `subtotal`)
2. **Composite Keys**: `Cart` entity uses `@IdClass` for composite primary key
3. **Lazy Loading**: All relationships use `FetchType.LAZY` for performance
4. **Unique Constraints**: Applied where needed (e.g., voucher codes, usernames)
5. **Check Constraints**: Documented in database but not enforced at JPA level (handled by PostgreSQL)

## Build Status
✅ All entity classes compile successfully
✅ No compilation errors
✅ Build successful with `./gradlew clean build -x test`

## Next Steps
1. Create repository interfaces for entities
2. Create service layer for business logic
3. Create REST controllers for API endpoints
4. Add validation annotations where needed
5. Create DTOs for API requests/responses
6. Add comprehensive unit tests
