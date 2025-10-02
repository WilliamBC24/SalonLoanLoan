# Entity Classes Documentation

This document provides detailed information about all entity classes and enumerators in the SalonLoanLoan backend application. It serves as a developer knowledge base for understanding the database schema and entity relationships.

## Summary

- **Total Entity Classes**: 66
- **Total Enumerator Classes**: 16

## Enumerators

Enumerators define fixed sets of values used throughout the application.

### AccountRole
**Values**: CUSTOMER, ADMIN

### AccountStatus
**Values**: ACTIVE, DEACTIVATED, BANNED

### AppointmentStatus
**Values**: PENDING, REGISTERED, STARTED, COMPLETED, RESCHEDULED, CANCELLED

### CommissionType
**Values**: APPOINTMENT, PRODUCT

### DiscountType
**Values**: AMOUNT, PERCENTAGE

### Gender
**Values**: MALE, FEMALE

### InventoryInvoiceStatus
**Values**: AWAITING, COMPLETE, CANCELLED

### InventoryRequestStatus
**Values**: PENDING, APPROVED, REJECTED

### InventoryTransactionReason
**Values**: SERVICE, SHIPMENT

### InventoryTransactionType
**Values**: IN, OUT

### JobPostingApplicationStatus
**Values**: PENDING, ACCEPTED, REJECTED

### JobPostingStatus
**Values**: ACTIVE, DEACTIVATED

### PayrollAdjustment
**Values**: BONUS, DEDUCTION

### ServiceType
**Values**: SINGLE, COMBO

### ShiftAttendanceStatus
**Values**: FULL, PARTIAL, MISSED

### StaffStatus
**Values**: ACTIVE, ON_LEAVE, TERMINATED

## Entity Classes by Category

### User and Authentication

#### UserAccount

**Purpose**: Stores user account credentials and basic profile information

**Database Table**: `user_account`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| username | String | username |  | ✓ | ✓ |  |
| password | String | password |  | ✓ |  |  |
| gender | Gender | gender |  | ✓ |  | Enum |
| birthDate | LocalDate | birth_date |  |  |  |  |
| phoneNumber | String | phone_number |  | ✓ |  |  |
| email | String | email |  |  |  |  |
| phoneVerified | Boolean | phone_verified |  | ✓ |  |  |
| accountStatus | AccountStatus | account_status |  |  |  | Enum |

#### UserShippingInfo

**Purpose**: Manages shipping addresses for product orders

**Database Table**: `user_shipping_info`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| userAccount | UserAccount | userAccount | 🔗 ManyToOne |  |  |  |
| shippingAddress | String | shipping_address |  | ✓ |  |  |
| phoneNumber | String | phone_number |  | ✓ |  |  |

#### CustomerInfo

**Purpose**: Records customer contact information for transactions without user accounts

**Database Table**: `customer_info`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| name | String | name |  | ✓ |  |  |
| phoneNumber | String | phone_number |  | ✓ |  |  |
| shippingAddress | String | shipping_address |  |  |  |  |

#### PaymentType

**Purpose**: Defines available payment methods (cash, bank transfer, etc.)

**Database Table**: `payment_type`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| name | String | name |  | ✓ |  |  |


### Staff Management

#### Staff

**Purpose**: Stores staff member personal and employment information

**Database Table**: `staff`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| dateHired | LocalDate | date_hired |  | ✓ |  |  |
| endOfContractDate | LocalDate | end_of_contract_date |  |  |  |  |
| socialSecurityNum | String | social_security_num |  |  |  |  |
| name | String | name |  | ✓ |  |  |
| birthDate | LocalDate | birth_date |  | ✓ |  |  |
| email | String | email |  |  |  |  |
| staffStatus | StaffStatus | staff_status |  | ✓ |  | Enum |

#### StaffAccount

**Purpose**: Login credentials for staff members

**Database Table**: `staff_account`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| staff | Staff | staff | 🔗 OneToOne |  |  |  |
| username | String | username |  | ✓ | ✓ |  |
| password | String | password |  | ✓ |  |  |
| activeStatus | Boolean | active_status |  | ✓ |  |  |

#### StaffPosition

**Purpose**: Defines available staff position types (e.g., stylist, receptionist)

**Database Table**: `staff_position`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| positionName | String | position_name |  | ✓ |  |  |

#### StaffCurrentPosition

**Purpose**: Tracks current position assignment for each staff member

**Database Table**: `staff_current_position`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| staff | Staff | staff | 🔗 ManyToOne |  |  |  |
| position | StaffPosition | position | 🔗 ManyToOne |  |  |  |

#### StaffPromotionHistory

**Purpose**: Records historical position changes and promotions

**Database Table**: `staff_promotion_history`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| staff | Staff | staff | 🔗 ManyToOne |  |  |  |
| position | StaffPosition | position | 🔗 ManyToOne |  |  |  |
| effectiveFrom | LocalDateTime | effective_from |  | ✓ |  |  |
| effectiveTo | LocalDateTime | effective_to |  |  |  |  |

#### StaffCommission

**Purpose**: Commission rates for different positions

**Database Table**: `staff_commission`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| position | StaffPosition | position | 🔗 ManyToOne |  |  |  |
| commissionType | CommissionType | commission_type |  | ✓ |  | Enum |
| commission | Short | commission |  | ✓ |  |  |

#### StaffCommissionHistory

**Purpose**: Historical changes to commission rates

**Database Table**: `staff_commission_history`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| staffCommission | StaffCommission | staffCommission | 🔗 ManyToOne |  |  |  |
| commissionType | CommissionType | commission_type |  | ✓ |  | Enum |
| commission | Short | commission |  | ✓ |  |  |
| effectiveFrom | LocalDateTime | effective_from |  | ✓ |  |  |
| effectiveTo | LocalDateTime | effective_to |  |  |  |  |

#### StaffPayroll

**Purpose**: Monthly payroll records for staff

**Database Table**: `staff_payroll`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| staff | Staff | staff | 🔗 ManyToOne |  |  |  |
| payPeriodStart | LocalDate | pay_period_start |  | ✓ |  |  |
| payPeriodEnd | LocalDate | pay_period_end |  | ✓ |  |  |
| appointmentCommission | Integer | appointment_commission |  | ✓ |  |  |
| productCommission | Integer | product_commission |  | ✓ |  |  |
| payrollDeduction | Integer | payroll_deduction |  | ✓ |  |  |
| payrollBonus | Integer | payroll_bonus |  | ✓ |  |  |

#### StaffPayrollAdjustment

**Purpose**: Additional adjustments to payroll (bonuses, deductions)

**Database Table**: `staff_payroll_adjustment`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| staff | Staff | staff | 🔗 ManyToOne |  |  |  |
| adjustmentType | PayrollAdjustment | adjustment_type |  | ✓ |  | Enum |
| amount | Integer | amount |  | ✓ |  |  |
| note | String | note |  |  |  |  |
| effectiveDate | LocalDate | effective_date |  | ✓ |  |  |


### Appointment Management

#### Appointment

**Purpose**: Customer appointment bookings and scheduling

**Database Table**: `appointment`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| registeredAt | LocalDateTime | registered_at |  | ✓ |  |  |
| scheduledAt | LocalDateTime | scheduled_at |  |  |  |  |
| name | String | name |  | ✓ |  |  |
| phoneNumber | String | phone_number |  | ✓ |  |  |
| status | AppointmentStatus | status |  | ✓ |  | Enum |

#### AppointmentDetails

**Purpose**: Detailed timing and staff assignment for appointments

**Database Table**: `appointment_details`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| appointment | Appointment | appointment | 🔗 ManyToOne |  |  |  |
| user | UserAccount | user | 🔗 ManyToOne |  |  |  |
| scheduledStart | LocalDateTime | scheduled_start |  | ✓ |  |  |
| scheduledEnd | LocalDateTime | scheduled_end |  | ✓ |  |  |
| actualStart | LocalDateTime | actual_start |  |  |  |  |
| actualEnd | LocalDateTime | actual_end |  |  |  |  |

#### AppointmentInvoice

**Purpose**: Billing and payment information for completed appointments

**Database Table**: `appointment_invoice`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| appointment | Appointment | appointment | 🔗 ManyToOne |  |  |  |
| totalPrice | Integer | total_price |  | ✓ |  |  |
| paymentType | PaymentType | paymentType | 🔗 ManyToOne |  |  |  |
| voucher | Voucher | voucher | 🔗 ManyToOne |  |  |  |
| responsibleStaff | Staff | responsibleStaff | 🔗 ManyToOne |  |  |  |
| createdAt | LocalDateTime | created_at |  | ✓ |  |  |

#### AppointmentFeedback

**Purpose**: Customer feedback and ratings for appointments

**Database Table**: `appointment_feedback`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| appointment | Appointment | appointment | 🔗 OneToOne |  |  |  |
| rating | Short | rating |  | ✓ |  |  |
| comment | String | comment |  |  |  |  |

#### AfterAppointmentImage

**Purpose**: Photos taken after service completion

**Database Table**: `after_appointment_image`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| appointment | Appointment | appointment | 🔗 ManyToOne |  |  |  |
| imagePath | String | image_path |  | ✓ |  |  |

#### AppointmentFeedbackImage

**Purpose**: Images attached to customer feedback

**Database Table**: `appointment_feedback_image`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| appointmentFeedback | AppointmentFeedback | appointmentFeedback | 🔗 ManyToOne |  |  |  |
| imagePath | String | image_path |  | ✓ |  |  |

#### SatisfactionRating

**Purpose**: Rating scale definitions for feedback

**Database Table**: `satisfaction_rating`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| appointment | Appointment | appointment | 🔗 OneToOne |  |  |  |
| rating | Short | rating |  | ✓ |  |  |
| comment | String | comment |  |  |  |  |

#### RequestedService

**Purpose**: Services selected for appointments

**Database Table**: `requested_service`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| appointment | Appointment | appointment | 🔗 ManyToOne |  |  |  |
| service | Service | service | 🔗 ManyToOne |  |  |  |
| priceAtBooking | Integer | price_at_booking |  | ✓ |  |  |
| responsibleStaff | Staff | responsibleStaff | 🔗 ManyToOne |  |  |  |


### Service Management

#### Service

**Purpose**: Available salon services catalog

**Database Table**: `service`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| serviceName | String | service_name |  | ✓ |  |  |
| serviceCategory | ServiceCategory | serviceCategory | 🔗 ManyToOne |  |  |  |
| serviceType | ServiceType | service_type |  | ✓ |  | Enum |
| servicePrice | Integer | service_price |  | ✓ |  |  |
| durationMinutes | Short | duration_minutes |  | ✓ |  |  |
| serviceDescription | String | service_description |  |  |  |  |
| activeStatus | Boolean | active_status |  | ✓ |  |  |

#### ServiceCategory

**Purpose**: Categories for organizing services

**Database Table**: `service_category`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| name | String | name |  | ✓ |  |  |

#### ServiceChangeHistory

**Purpose**: Historical changes to service prices and details

**Database Table**: `service_change_history`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| service | Service | service | 🔗 ManyToOne |  |  |  |
| serviceName | String | service_name |  | ✓ |  |  |
| serviceCategory | ServiceCategory | serviceCategory | 🔗 ManyToOne |  |  |  |
| servicePrice | Integer | service_price |  | ✓ |  |  |
| durationMinutes | Short | duration_minutes |  | ✓ |  |  |
| serviceDescription | String | service_description |  |  |  |  |
| effectiveFrom | LocalDateTime | effective_from |  | ✓ |  |  |
| effectiveTo | LocalDateTime | effective_to |  |  |  |  |

#### ServiceCombo

**Purpose**: Bundled service packages

**Database Table**: `service_combo`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| combo | Service | combo | 🔗 ManyToOne |  |  |  |
| service | Service | service | 🔗 ManyToOne |  |  |  |

#### ServiceImage

**Purpose**: Display images for services

**Database Table**: `service_image`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| service | Service | service | 🔗 ManyToOne |  |  |  |
| imagePath | String | image_path |  |  |  |  |


### Product and Inventory

#### Product

**Purpose**: Physical products available for sale

**Database Table**: `product`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| productName | String | product_name |  | ✓ |  |  |
| currentPrice | Integer | current_price |  | ✓ |  |  |
| productDescription | String | product_description |  | ✓ |  |  |
| activeStatus | Boolean | active_status |  | ✓ |  |  |

#### ProductChangeHistory

**Purpose**: Historical changes to product prices and details

**Database Table**: `product_change_history`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| product | Product | product | 🔗 ManyToOne |  |  |  |
| productName | String | product_name |  | ✓ |  |  |
| unitPrice | Integer | unit_price |  | ✓ |  |  |
| productDescription | String | product_description |  | ✓ |  |  |
| effectiveFrom | LocalDateTime | effective_from |  | ✓ |  |  |
| effectiveTo | LocalDateTime | effective_to |  |  |  |  |

#### ProductImage

**Purpose**: Display images for products

**Database Table**: `product_image`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| product | Product | product | 🔗 ManyToOne |  |  |  |
| imagePath | String | image_path |  |  |  |  |

#### Supplier

**Purpose**: Supplier company information

**Database Table**: `supplier`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| supplierName | String | supplier_name |  | ✓ | ✓ |  |
| phoneNumber | String | phone_number |  |  |  |  |
| email | String | email |  |  |  |  |
| supplierCategory | SupplierCategory | supplierCategory | 🔗 ManyToOne |  |  |  |
| note | String | note |  |  |  |  |

#### SupplierCategory

**Purpose**: Categories for supplier types

**Database Table**: `supplier_category`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| name | String | name |  | ✓ |  |  |

#### InventoryInvoice

**Purpose**: Purchase orders from suppliers

**Database Table**: `inventory_invoice`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| staff | Staff | staff | 🔗 ManyToOne |  |  |  |
| supplier | Supplier | supplier | 🔗 ManyToOne |  |  |  |
| createdAt | LocalDateTime | created_at |  | ✓ |  |  |
| note | String | note |  |  |  |  |
| invoiceStatus | InventoryInvoiceStatus | invoice_status |  | ✓ |  | Enum |

#### InventoryInvoiceDetail

**Purpose**: Line items in purchase orders

**Database Table**: `inventory_invoice_detail`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| inventoryInvoice | InventoryInvoice | inventoryInvoice | 🔗 ManyToOne |  |  |  |
| product | Product | product | 🔗 ManyToOne |  |  |  |
| orderedQuantity | Integer | ordered_quantity |  | ✓ |  |  |
| unitPrice | Integer | unit_price |  | ✓ |  |  |

#### InventoryConsignment

**Purpose**: Received inventory shipments

**Database Table**: `inventory_consignment`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| inventoryInvoiceDetail | InventoryInvoiceDetail | inventoryInvoiceDetail | 🔗 ManyToOne |  |  |  |
| product | Product | product | 🔗 ManyToOne |  |  |  |
| supplier | Supplier | supplier | 🔗 ManyToOne |  |  |  |
| receivedQuantity | Integer | received_quantity |  | ✓ |  |  |

#### InventoryRequest

**Purpose**: Internal requests to use inventory for services

**Database Table**: `inventory_request`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| staff | Staff | staff | 🔗 ManyToOne |  |  |  |
| requestDate | LocalDateTime | request_date |  | ✓ |  |  |
| inventoryRequestStatus | InventoryRequestStatus | inventory_request_status |  | ✓ |  | Enum |

#### InventoryRequestDetail

**Purpose**: Items in inventory usage requests

**Database Table**: `inventory_request_detail`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| inventoryRequest | InventoryRequest | inventoryRequest | 🔗 ManyToOne |  |  |  |
| inventoryConsignment | InventoryConsignment | inventoryConsignment | 🔗 ManyToOne |  |  |  |
| product | Product | product | 🔗 ManyToOne |  |  |  |
| requestedQuantity | Integer | requested_quantity |  | ✓ |  |  |
| productExpiryDate | LocalDate | product_expiry_date |  | ✓ |  |  |

#### InventoryLot

**Purpose**: Current inventory stock levels by batch

**Database Table**: `inventory_lot`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| inventoryConsignment | InventoryConsignment | inventoryConsignment | 🔗 ManyToOne |  |  |  |
| product | Product | product | 🔗 ManyToOne |  |  |  |
| availableQuantity | Integer | available_quantity |  | ✓ |  |  |
| productExpiryDate | LocalDate | product_expiry_date |  | ✓ |  |  |

#### InventoryTransaction

**Purpose**: Movement records for inventory (in/out)

**Database Table**: `inventory_transaction`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| inventoryLot | InventoryLot | inventoryLot | 🔗 ManyToOne |  |  |  |
| staff | Staff | staff | 🔗 ManyToOne |  |  |  |
| transactionType | InventoryTransactionType | transaction_type |  | ✓ |  | Enum |
| transactionTime | LocalDateTime | transaction_time |  | ✓ |  |  |
| quantity | Integer | quantity |  | ✓ |  |  |
| reason | InventoryTransactionReason | reason |  | ✓ |  | Enum |


### Order Management

#### Cart

**Purpose**: Shopping cart items for customers

**Database Table**: `cart`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| userAccount | UserAccount | userAccount | 🔗 ManyToOne |  |  |  |
| product | Product | product | 🔗 ManyToOne |  |  |  |
| amount | Integer | amount |  | ✓ |  |  |
| userAccount | Integer | userAccount |  |  |  |  |
| product | Integer | product |  |  |  |  |

#### OrderInvoice

**Purpose**: Customer product orders

**Database Table**: `order_invoice`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| customerInfo | CustomerInfo | customerInfo | 🔗 ManyToOne |  |  |  |
| totalPrice | Integer | total_price |  | ✓ |  |  |
| paymentType | PaymentType | paymentType | 🔗 ManyToOne |  |  |  |
| createdAt | LocalDateTime | created_at |  | ✓ |  |  |

#### OrderInvoiceDetails

**Purpose**: Line items in customer orders

**Database Table**: `order_invoice_details`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| orderInvoice | OrderInvoice | orderInvoice | 🔗 ManyToOne |  |  |  |
| product | Product | product | 🔗 ManyToOne |  |  |  |
| quantity | Integer | quantity |  | ✓ |  |  |
| priceAtSale | Integer | price_at_sale |  | ✓ |  |  |


### Loyalty and Promotions

#### Loyalty

**Purpose**: Customer loyalty program accounts

**Database Table**: `loyalty`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| user | UserAccount | user | 🔗 OneToOne |  |  |  |
| point | Integer | point |  | ✓ |  |  |
| level | LoyaltyLevel | level | 🔗 ManyToOne |  |  |  |

#### LoyaltyLevel

**Purpose**: Tier definitions (Bronze, Silver, Gold, Platinum)

**Database Table**: `loyalty_level`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Short | id | 🔑 PK |  |  |  |
| name | String | name |  | ✓ | ✓ |  |
| pointRequired | Integer | point_required |  | ✓ |  |  |

#### LoyaltyHistory

**Purpose**: Point transaction history

**Database Table**: `loyalty_history`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| loyalty | Loyalty | loyalty | 🔗 ManyToOne |  |  |  |
| appointmentInvoice | AppointmentInvoice | appointmentInvoice | 🔗 ManyToOne |  |  |  |
| order | OrderInvoice | order | 🔗 ManyToOne |  |  |  |
| amount | Integer | amount |  | ✓ |  |  |
| credittedDate | LocalDate | creditted_date |  | ✓ |  |  |

#### Voucher

**Purpose**: Discount vouchers and promotional codes

**Database Table**: `voucher`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| voucherName | String | voucher_name |  | ✓ |  |  |
| voucherDescription | String | voucher_description |  | ✓ |  |  |
| voucherCode | String | voucher_code |  | ✓ | ✓ |  |
| discountType | DiscountType | discount_type |  | ✓ |  | Enum |
| discountAmount | Integer | discount_amount |  | ✓ |  |  |
| effectiveFrom | LocalDateTime | effective_from |  | ✓ |  |  |
| effectiveTo | LocalDateTime | effective_to |  | ✓ |  |  |
| maxUsage | Integer | max_usage |  | ✓ |  |  |
| usedCount | Integer | used_count |  | ✓ |  |  |
| voucherStatus | VoucherStatus | voucherStatus | 🔗 ManyToOne |  |  |  |

#### VoucherStatus

**Purpose**: Status types for vouchers (active, expired, etc.)

**Database Table**: `voucher_status`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| name | String | name |  | ✓ |  |  |

#### VoucherCondition

**Purpose**: Usage conditions and requirements for vouchers

**Database Table**: `voucher_condition`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| voucher | Voucher | voucher | 🔗 ManyToOne |  |  |  |
| minUserLevel | LoyaltyLevel | minUserLevel | 🔗 ManyToOne |  |  |  |
| minBill | Integer | min_bill |  |  |  |  |
| firstTimeUser | Boolean | first_time_user |  |  |  |  |

#### VoucherRedemption

**Purpose**: Records of voucher usage

**Database Table**: `voucher_redemption`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| voucher | Voucher | voucher | 🔗 ManyToOne |  |  |  |
| userAccount | UserAccount | userAccount | 🔗 ManyToOne |  |  |  |
| redeemedDate | LocalDateTime | redeemed_date |  | ✓ |  |  |

#### Promotion

**Purpose**: Active promotional campaigns

**Database Table**: `promotion`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| promotionName | String | promotion_name |  | ✓ |  |  |
| promotionDescription | String | promotion_description |  |  |  |  |
| discountType | DiscountType | discount_type |  | ✓ |  | Enum |
| discountAmount | Integer | discount_amount |  | ✓ |  |  |
| effectiveFrom | LocalDateTime | effective_from |  | ✓ |  |  |
| effectiveTo | LocalDateTime | effective_to |  | ✓ |  |  |
| promotionStatus | PromotionStatus | promotionStatus | 🔗 ManyToOne |  |  |  |

#### PromotionStatus

**Purpose**: Status types for promotions

**Database Table**: `promotion_status`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| name | String | name |  | ✓ |  |  |


### Financial Management

#### Expense

**Purpose**: Business expenses and costs

**Database Table**: `expense`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| expenseCategory | ExpenseCategory | expenseCategory | 🔗 ManyToOne |  |  |  |
| amount | Integer | amount |  | ✓ |  |  |
| note | String | note |  |  |  |  |
| dateIncurred | LocalDate | date_incurred |  | ✓ |  |  |

#### ExpenseCategory

**Purpose**: Categories for expense types

**Database Table**: `expense_category`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Short | id | 🔑 PK |  |  |  |
| name | String | name |  | ✓ |  |  |

#### FinancialTransaction

**Purpose**: All financial transactions (income and expenses)

**Database Table**: `financial_transaction`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| financialTransactionCategory | FinancialTransactionCategory | financialTransactionCategory | 🔗 ManyToOne |  |  |  |
| amount | Integer | amount |  | ✓ |  |  |
| note | String | note |  |  |  |  |
| timeIncurred | LocalDateTime | time_incurred |  | ✓ |  |  |

#### FinancialTransactionCategory

**Purpose**: Categories for transaction types

**Database Table**: `financial_transaction_category`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Short | id | 🔑 PK |  |  |  |
| name | String | name |  | ✓ |  |  |


### Shift Management

#### ShiftTemplate

**Purpose**: Reusable shift time templates

**Database Table**: `shift_template`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| shiftStart | LocalDateTime | shift_start |  | ✓ |  |  |
| shiftEnd | LocalDateTime | shift_end |  | ✓ |  |  |

#### ShiftInstance

**Purpose**: Actual scheduled shifts

**Database Table**: `shift_instance`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| shiftTemplate | ShiftTemplate | shiftTemplate | 🔗 ManyToOne |  |  |  |
| shiftDate | LocalDate | shift_date |  | ✓ |  |  |

#### ShiftAssignment

**Purpose**: Staff assignments to shifts

**Database Table**: `shift_assignment`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| shiftInstance | ShiftInstance | shiftInstance | 🔗 ManyToOne |  |  |  |
| assignedStaff | Staff | assignedStaff | 🔗 ManyToOne |  |  |  |

#### ShiftAssignmentHistory

**Purpose**: Historical shift assignment records

**Database Table**: `shift_assignment_history`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| shiftInstance | ShiftInstance | shiftInstance | 🔗 ManyToOne |  |  |  |
| assignedStaff | Staff | assignedStaff | 🔗 ManyToOne |  |  |  |

#### ShiftAttendance

**Purpose**: Check-in/check-out records for shifts

**Database Table**: `shift_attendance`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| shiftAssignment | ShiftAssignment | shiftAssignment | 🔗 ManyToOne |  |  |  |
| checkIn | LocalDateTime | check_in |  |  |  |  |
| checkOut | LocalDateTime | check_out |  |  |  |  |
| status | ShiftAttendanceStatus | status |  | ✓ |  | Enum |

#### ShiftCashPaymentRecord

**Purpose**: Cash payment handling during shifts

**Database Table**: `shift_cash_payment_record`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| shiftInstance | ShiftInstance | shiftInstance | 🔗 ManyToOne |  |  |  |
| amount | Integer | amount |  | ✓ |  |  |
| recordedAt | LocalDateTime | recorded_at |  | ✓ |  |  |
| responsibleStaff | Staff | responsibleStaff | 🔗 ManyToOne |  |  |  |
| note | String | note |  |  |  |  |


### Miscellaneous

#### Location

**Purpose**: Salon branch/location information

**Database Table**: `location`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| location | String | location |  | ✓ |  |  |

#### ReminderLog

**Purpose**: Customer reminder and notification history

**Database Table**: `reminder_log`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| userAccount | UserAccount | userAccount | 🔗 ManyToOne |  |  |  |
| reminderReason | ReminderReason | reminderReason | 🔗 ManyToOne |  |  |  |
| reminderType | ReminderType | reminderType | 🔗 ManyToOne |  |  |  |
| remindedDate | LocalDate | reminded_date |  |  |  |  |

#### ReminderReason

**Purpose**: Types of reminders (appointment, promotion, etc.)

**Database Table**: `reminder_reason`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| name | String | name |  | ✓ |  |  |

#### ReminderType

**Purpose**: Delivery methods (SMS, email, etc.)

**Database Table**: `reminder_type`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| name | String | name |  | ✓ |  |  |

#### JobPosting

**Purpose**: Open job positions

**Database Table**: `job_posting`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| jobPostingName | String | job_posting_name |  | ✓ |  |  |
| jobPostingDescription | String | job_posting_description |  | ✓ |  |  |
| maxApplication | Integer | max_application |  | ✓ |  |  |
| effectiveFrom | LocalDate | effective_from |  | ✓ |  |  |
| effectiveTo | LocalDate | effective_to |  |  |  |  |
| status | JobPostingStatus | status |  | ✓ |  | Enum |

#### JobPostingApplication

**Purpose**: Job applications from candidates

**Database Table**: `job_posting_application`

**Attributes**:

| Attribute | Type | Column | Key | Required | Unique | Notes |
|-----------|------|--------|-----|----------|--------|-------|
| id | Integer | id | 🔑 PK |  |  |  |
| jobPosting | JobPosting | jobPosting | 🔗 ManyToOne |  |  |  |
| applicantName | String | applicant_name |  | ✓ |  |  |
| applicantDob | LocalDate | applicant_dob |  | ✓ |  |  |
| applicantPhoneNumber | String | applicant_phone_number |  | ✓ |  |  |
| applicationDate | LocalDateTime | application_date |  | ✓ |  |  |
| status | JobPostingApplicationStatus | status |  | ✓ |  | Enum |


