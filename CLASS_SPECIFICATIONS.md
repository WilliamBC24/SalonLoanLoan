# Class Specifications - SalonLoanLoan Backend System
**Software Requirements Documentation**

## Table of Contents
1. [Introduction](#1-introduction)
2. [Package Overview](#2-package-overview)
3. [Class Specifications](#3-class-specifications)

---

## 1. Introduction

This document provides comprehensive class specifications for the SalonLoanLoan backend system - a web-based salon management application built with Spring Boot. The system manages appointments, staff, inventory, orders, loyalty programs, and other salon operations.

### Technology Stack
- **Framework**: Spring Boot 3.x (Java)
- **Database**: PostgreSQL
- **ORM**: Hibernate/JPA
- **Build Tool**: Maven
- **Architecture**: MVC (Model-View-Controller)

### Documentation Format
Each class specification includes:
- Brief description of the class purpose
- Complete list of attributes with visibility, type, and purpose
- Complete list of methods/operations with visibility, return type, parameters, and purpose

---

## 2. Package Overview

| Package | Purpose | Class Count |
|---------|---------|-------------|
| service.sllbackend.entity | JPA entity classes representing database tables | 66 |
| service.sllbackend.enumerator | Enumeration types for fixed value sets | 16 |
| service.sllbackend.repository | Spring Data JPA repository interfaces | 39 |
| service.sllbackend.service | Business logic service interfaces | 23 |
| service.sllbackend.service.impl | Service implementation classes | 23 |
| service.sllbackend.web.dto | Data Transfer Objects for API communication | 45 |
| service.sllbackend.web.mvc | Spring MVC controller classes | 22 |
| service.sllbackend.utils | Utility and helper classes | 8 |
| service.sllbackend.config | Configuration classes | 3 |

---

## 3. Class Specifications

### 3.1 service.sllbackend.entity

This package contains JPA entity classes that map to database tables. Each entity represents a business domain object.


#### 3.1.1 UserAccount

Stores customer account credentials and profile information for system authentication and user management.

+----+------------------+---------------------------------------------------------------------------------------+
| No | Name             | Description                                                                           |
+====+==================+=======================================================================================+
| Attributes                                                                                                    |
+----+------------------+---------------------------------------------------------------------------------------+
| 01 | id               | Visibility: private                                                                   |
|    |                  | Type: Integer                                                                         |
|    |                  | Purpose: Primary key identifier for the user account                                  |
+----+------------------+---------------------------------------------------------------------------------------+
| 02 | username         | Visibility: private                                                                   |
|    |                  | Type: String                                                                          |
|    |                  | Purpose: Unique username for login authentication (max 100 characters)                |
+----+------------------+---------------------------------------------------------------------------------------+
| 03 | password         | Visibility: private                                                                   |
|    |                  | Type: String                                                                          |
|    |                  | Purpose: Encrypted password for account security (max 100 characters)                 |
+----+------------------+---------------------------------------------------------------------------------------+
| 04 | gender           | Visibility: private                                                                   |
|    |                  | Type: Gender (enum)                                                                   |
|    |                  | Purpose: User's gender (MALE, FEMALE)                                                 |
+----+------------------+---------------------------------------------------------------------------------------+
| 05 | birthDate        | Visibility: private                                                                   |
|    |                  | Type: LocalDate                                                                       |
|    |                  | Purpose: User's date of birth for age verification and promotions                     |
+----+------------------+---------------------------------------------------------------------------------------+
| 06 | phoneNumber      | Visibility: private                                                                   |
|    |                  | Type: String                                                                          |
|    |                  | Purpose: Contact phone number for notifications (max 20 characters)                   |
+----+------------------+---------------------------------------------------------------------------------------+
| 07 | email            | Visibility: private                                                                   |
|    |                  | Type: String                                                                          |
|    |                  | Purpose: Email address for communication (max 100 characters)                         |
+----+------------------+---------------------------------------------------------------------------------------+
| 08 | phoneVerified    | Visibility: private                                                                   |
|    |                  | Type: Boolean                                                                         |
|    |                  | Purpose: Indicates if phone number has been verified (default: false)                 |
+----+------------------+---------------------------------------------------------------------------------------+
| 09 | accountStatus    | Visibility: private                                                                   |
|    |                  | Type: AccountStatus (enum)                                                            |
|    |                  | Purpose: Current account status (ACTIVE, DEACTIVATED, BANNED) (default: ACTIVE)       |
+====+==================+=======================================================================================+
| Methods/Operations                                                                                            |
+----+------------------+---------------------------------------------------------------------------------------+
| 01 | getId            | Visibility: public                                                                    |
|    |                  | Return: Integer                                                                       |
|    |                  | Purpose: Retrieve the unique identifier of the user account                           |
|    |                  | Parameters: None                                                                      |
+----+------------------+---------------------------------------------------------------------------------------+
| 02 | setId            | Visibility: public                                                                    |
|    |                  | Return: void                                                                          |
|    |                  | Purpose: Set the unique identifier for the user account                               |
|    |                  | Parameters:                                                                           |
|    |                  | - id:Integer, the primary key value to set                                            |
+----+------------------+---------------------------------------------------------------------------------------+
| 03 | getUsername      | Visibility: public                                                                    |
|    |                  | Return: String                                                                        |
|    |                  | Purpose: Retrieve the username for authentication                                     |
|    |                  | Parameters: None                                                                      |
+----+------------------+---------------------------------------------------------------------------------------+
| 04 | setUsername      | Visibility: public                                                                    |
|    |                  | Return: void                                                                          |
|    |                  | Purpose: Set the username for the account                                             |
|    |                  | Parameters:                                                                           |
|    |                  | - username:String, unique username value                                              |
+----+------------------+---------------------------------------------------------------------------------------+
| 05 | getPassword      | Visibility: public                                                                    |
|    |                  | Return: String                                                                        |
|    |                  | Purpose: Retrieve encrypted password                                                  |
|    |                  | Parameters: None                                                                      |
+----+------------------+---------------------------------------------------------------------------------------+
| 06 | setPassword      | Visibility: public                                                                    |
|    |                  | Return: void                                                                          |
|    |                  | Purpose: Set encrypted password for authentication                                    |
|    |                  | Parameters:                                                                           |
|    |                  | - password:String, encrypted password value                                           |
+----+------------------+---------------------------------------------------------------------------------------+
| 07 | getGender        | Visibility: public                                                                    |
|    |                  | Return: Gender                                                                        |
|    |                  | Purpose: Retrieve user's gender                                                       |
|    |                  | Parameters: None                                                                      |
+----+------------------+---------------------------------------------------------------------------------------+
| 08 | setGender        | Visibility: public                                                                    |
|    |                  | Return: void                                                                          |
|    |                  | Purpose: Set user's gender                                                            |
|    |                  | Parameters:                                                                           |
|    |                  | - gender:Gender, enum value (MALE, FEMALE)                                            |
+----+------------------+---------------------------------------------------------------------------------------+
| 09 | getBirthDate     | Visibility: public                                                                    |
|    |                  | Return: LocalDate                                                                     |
|    |                  | Purpose: Retrieve user's date of birth                                                |
|    |                  | Parameters: None                                                                      |
+----+------------------+---------------------------------------------------------------------------------------+
| 10 | setBirthDate     | Visibility: public                                                                    |
|    |                  | Return: void                                                                          |
|    |                  | Purpose: Set user's date of birth                                                     |
|    |                  | Parameters:                                                                           |
|    |                  | - birthDate:LocalDate, date of birth value                                            |
+----+------------------+---------------------------------------------------------------------------------------+
| 11 | getPhoneNumber   | Visibility: public                                                                    |
|    |                  | Return: String                                                                        |
|    |                  | Purpose: Retrieve user's contact phone number                                         |
|    |                  | Parameters: None                                                                      |
+----+------------------+---------------------------------------------------------------------------------------+
| 12 | setPhoneNumber   | Visibility: public                                                                    |
|    |                  | Return: void                                                                          |
|    |                  | Purpose: Set user's contact phone number                                              |
|    |                  | Parameters:                                                                           |
|    |                  | - phoneNumber:String, phone number value                                              |
+----+------------------+---------------------------------------------------------------------------------------+
| 13 | getEmail         | Visibility: public                                                                    |
|    |                  | Return: String                                                                        |
|    |                  | Purpose: Retrieve user's email address                                                |
|    |                  | Parameters: None                                                                      |
+----+------------------+---------------------------------------------------------------------------------------+
| 14 | setEmail         | Visibility: public                                                                    |
|    |                  | Return: void                                                                          |
|    |                  | Purpose: Set user's email address                                                     |
|    |                  | Parameters:                                                                           |
|    |                  | - email:String, email address value                                                   |
+----+------------------+---------------------------------------------------------------------------------------+
| 15 | getPhoneVerified | Visibility: public                                                                    |
|    |                  | Return: Boolean                                                                       |
|    |                  | Purpose: Check if phone number is verified                                            |
|    |                  | Parameters: None                                                                      |
+----+------------------+---------------------------------------------------------------------------------------+
| 16 | setPhoneVerified | Visibility: public                                                                    |
|    |                  | Return: void                                                                          |
|    |                  | Purpose: Set phone verification status                                                |
|    |                  | Parameters:                                                                           |
|    |                  | - phoneVerified:Boolean, verification status                                          |
+----+------------------+---------------------------------------------------------------------------------------+
| 17 | getAccountStatus | Visibility: public                                                                    |
|    |                  | Return: AccountStatus                                                                 |
|    |                  | Purpose: Retrieve current account status                                              |
|    |                  | Parameters: None                                                                      |
+----+------------------+---------------------------------------------------------------------------------------+
| 18 | setAccountStatus | Visibility: public                                                                    |
|    |                  | Return: void                                                                          |
|    |                  | Purpose: Set account status (ACTIVE, DEACTIVATED, BANNED)                             |
|    |                  | Parameters:                                                                           |
|    |                  | - accountStatus:AccountStatus, enum value for account status                          |
+----+------------------+---------------------------------------------------------------------------------------+

#### 3.1.2 Appointment

Manages customer appointment bookings, scheduling, and tracking appointment lifecycle from registration to completion.

+----+------------------+---------------------------------------------------------------------------------------+
| No | Name             | Description                                                                           |
+====+==================+=======================================================================================+
| Attributes                                                                                                    |
+----+------------------+---------------------------------------------------------------------------------------+
| 01 | id               | Visibility: private                                                                   |
|    |                  | Type: Integer                                                                         |
|    |                  | Purpose: Primary key identifier for the appointment                                   |
+----+------------------+---------------------------------------------------------------------------------------+
| 02 | registeredAt     | Visibility: private                                                                   |
|    |                  | Type: LocalDateTime                                                                   |
|    |                  | Purpose: Timestamp when appointment was registered (default: current time)            |
+----+------------------+---------------------------------------------------------------------------------------+
| 03 | scheduledAt      | Visibility: private                                                                   |
|    |                  | Type: LocalDateTime                                                                   |
|    |                  | Purpose: Scheduled date and time for the appointment                                  |
+----+------------------+---------------------------------------------------------------------------------------+
| 04 | name             | Visibility: private                                                                   |
|    |                  | Type: String                                                                          |
|    |                  | Purpose: Name of the customer making the appointment                                  |
+----+------------------+---------------------------------------------------------------------------------------+
| 05 | phoneNumber      | Visibility: private                                                                   |
|    |                  | Type: String                                                                          |
|    |                  | Purpose: Contact phone number for appointment confirmation (max 20 characters)        |
+----+------------------+---------------------------------------------------------------------------------------+
| 06 | status           | Visibility: private                                                                   |
|    |                  | Type: AppointmentStatus (enum)                                                        |
|    |                  | Purpose: Current appointment status (PENDING, REGISTERED, STARTED, COMPLETED,         |
|    |                  | RESCHEDULED, CANCELLED) (default: PENDING)                                            |
+----+------------------+---------------------------------------------------------------------------------------+
| 07 | preferredStaffId | Visibility: private                                                                   |
|    |                  | Type: Staff (ManyToOne relationship)                                                  |
|    |                  | Purpose: Customer's preferred staff member for the appointment                        |
+----+------------------+---------------------------------------------------------------------------------------+
| 08 | responsibleStaffId| Visibility: private                                                                  |
|    |                  | Type: Staff (ManyToOne relationship)                                                  |
|    |                  | Purpose: Assigned staff member responsible for this appointment                       |
+====+==================+=======================================================================================+
| Methods/Operations (Standard getters and setters for all attributes)                                         |
+----+------------------+---------------------------------------------------------------------------------------+
| 01 | getId            | Visibility: public                                                                    |
|    |                  | Return: Integer                                                                       |
|    |                  | Purpose: Retrieve appointment identifier                                              |
|    |                  | Parameters: None                                                                      |
+----+------------------+---------------------------------------------------------------------------------------+
| 02 | getRegisteredAt  | Visibility: public                                                                    |
|    |                  | Return: LocalDateTime                                                                 |
|    |                  | Purpose: Retrieve registration timestamp                                              |
|    |                  | Parameters: None                                                                      |
+----+------------------+---------------------------------------------------------------------------------------+
| 03 | getScheduledAt   | Visibility: public                                                                    |
|    |                  | Return: LocalDateTime                                                                 |
|    |                  | Purpose: Retrieve scheduled appointment time                                          |
|    |                  | Parameters: None                                                                      |
+----+------------------+---------------------------------------------------------------------------------------+
| 04 | getStatus        | Visibility: public                                                                    |
|    |                  | Return: AppointmentStatus                                                             |
|    |                  | Purpose: Retrieve current appointment status                                          |
|    |                  | Parameters: None                                                                      |
+----+------------------+---------------------------------------------------------------------------------------+
| 05 | setStatus        | Visibility: public                                                                    |
|    |                  | Return: void                                                                          |
|    |                  | Purpose: Update appointment status (e.g., from PENDING to REGISTERED)                 |
|    |                  | Parameters:                                                                           |
|    |                  | - status:AppointmentStatus, new status value                                          |
+----+------------------+---------------------------------------------------------------------------------------+

#### 3.1.3 Staff

Stores staff member personal and employment information for payroll, scheduling, and service assignment.

+----+------------------+---------------------------------------------------------------------------------------+
| No | Name             | Description                                                                           |
+====+==================+=======================================================================================+
| Attributes                                                                                                    |
+----+------------------+---------------------------------------------------------------------------------------+
| 01 | id               | Visibility: private                                                                   |
|    |                  | Type: Integer                                                                         |
|    |                  | Purpose: Primary key identifier for the staff member                                  |
+----+------------------+---------------------------------------------------------------------------------------+
| 02 | dateHired        | Visibility: private                                                                   |
|    |                  | Type: LocalDate                                                                       |
|    |                  | Purpose: Date when staff member was hired                                             |
+----+------------------+---------------------------------------------------------------------------------------+
| 03 | endOfContractDate| Visibility: private                                                                   |
|    |                  | Type: LocalDate                                                                       |
|    |                  | Purpose: Contract end date (nullable for permanent staff)                             |
+----+------------------+---------------------------------------------------------------------------------------+
| 04 | socialSecurityNum| Visibility: private                                                                   |
|    |                  | Type: String                                                                          |
|    |                  | Purpose: Encrypted social security number for legal compliance                        |
+----+------------------+---------------------------------------------------------------------------------------+
| 05 | name             | Visibility: private                                                                   |
|    |                  | Type: String                                                                          |
|    |                  | Purpose: Full name of the staff member                                                |
+----+------------------+---------------------------------------------------------------------------------------+
| 06 | birthDate        | Visibility: private                                                                   |
|    |                  | Type: LocalDate                                                                       |
|    |                  | Purpose: Date of birth for age verification                                           |
+----+------------------+---------------------------------------------------------------------------------------+
| 07 | email            | Visibility: private                                                                   |
|    |                  | Type: String                                                                          |
|    |                  | Purpose: Email address for staff communication                                        |
+----+------------------+---------------------------------------------------------------------------------------+
| 08 | staffStatus      | Visibility: private                                                                   |
|    |                  | Type: StaffStatus (enum)                                                              |
|    |                  | Purpose: Employment status (ACTIVE, ON_LEAVE, TERMINATED)                             |
+====+==================+=======================================================================================+
| Methods/Operations (Standard getters and setters for all attributes)                                         |
+----+------------------+---------------------------------------------------------------------------------------+

#### 3.1.4 Product

Represents physical products available for purchase, including pricing and inventory tracking.

+----+------------------+---------------------------------------------------------------------------------------+
| No | Name             | Description                                                                           |
+====+==================+=======================================================================================+
| Attributes                                                                                                    |
+----+------------------+---------------------------------------------------------------------------------------+
| 01 | id               | Visibility: private                                                                   |
|    |                  | Type: Integer                                                                         |
|    |                  | Purpose: Primary key identifier for the product                                       |
+----+------------------+---------------------------------------------------------------------------------------+
| 02 | productName      | Visibility: private                                                                   |
|    |                  | Type: String                                                                          |
|    |                  | Purpose: Name of the product for display and identification                           |
+----+------------------+---------------------------------------------------------------------------------------+
| 03 | currentPrice     | Visibility: private                                                                   |
|    |                  | Type: Integer                                                                         |
|    |                  | Purpose: Current selling price in smallest currency unit (e.g., cents)                |
+----+------------------+---------------------------------------------------------------------------------------+
| 04 | productDescription| Visibility: private                                                                  |
|    |                  | Type: String                                                                          |
|    |                  | Purpose: Detailed description of product features and benefits                        |
+----+------------------+---------------------------------------------------------------------------------------+
| 05 | activeStatus     | Visibility: private                                                                   |
|    |                  | Type: Boolean                                                                         |
|    |                  | Purpose: Indicates if product is currently available for sale                         |
+====+==================+=======================================================================================+
| Methods/Operations (Standard getters and setters for all attributes)                                         |
+----+------------------+---------------------------------------------------------------------------------------+

#### 3.1.5 Service

Defines salon services available for booking, including pricing, duration, and categorization.

+----+------------------+---------------------------------------------------------------------------------------+
| No | Name             | Description                                                                           |
+====+==================+=======================================================================================+
| Attributes                                                                                                    |
+----+------------------+---------------------------------------------------------------------------------------+
| 01 | id               | Visibility: private                                                                   |
|    |                  | Type: Integer                                                                         |
|    |                  | Purpose: Primary key identifier for the service                                       |
+----+------------------+---------------------------------------------------------------------------------------+
| 02 | serviceName      | Visibility: private                                                                   |
|    |                  | Type: String                                                                          |
|    |                  | Purpose: Name of the service (e.g., "Haircut", "Manicure")                            |
+----+------------------+---------------------------------------------------------------------------------------+
| 03 | serviceCategory  | Visibility: private                                                                   |
|    |                  | Type: ServiceCategory (ManyToOne relationship)                                        |
|    |                  | Purpose: Category classification for organizational purposes                          |
+----+------------------+---------------------------------------------------------------------------------------+
| 04 | serviceType      | Visibility: private                                                                   |
|    |                  | Type: ServiceType (enum)                                                              |
|    |                  | Purpose: Type indicator (SINGLE service or COMBO package)                             |
+----+------------------+---------------------------------------------------------------------------------------+
| 05 | servicePrice     | Visibility: private                                                                   |
|    |                  | Type: Integer                                                                         |
|    |                  | Purpose: Price in smallest currency unit                                              |
+----+------------------+---------------------------------------------------------------------------------------+
| 06 | durationMinutes  | Visibility: private                                                                   |
|    |                  | Type: Short                                                                           |
|    |                  | Purpose: Expected service duration in minutes for scheduling                          |
+----+------------------+---------------------------------------------------------------------------------------+
| 07 | serviceDescription| Visibility: private                                                                  |
|    |                  | Type: String                                                                          |
|    |                  | Purpose: Detailed description of service and what it includes                         |
+----+------------------+---------------------------------------------------------------------------------------+
| 08 | activeStatus     | Visibility: private                                                                   |
|    |                  | Type: Boolean                                                                         |
|    |                  | Purpose: Indicates if service is currently offered                                    |
+====+==================+=======================================================================================+
| Methods/Operations (Standard getters and setters for all attributes)                                         |
+----+------------------+---------------------------------------------------------------------------------------+

#### 3.1.6 Voucher

Manages discount vouchers and promotional codes with usage tracking and conditions.

+----+------------------+---------------------------------------------------------------------------------------+
| No | Name             | Description                                                                           |
+====+==================+=======================================================================================+
| Attributes                                                                                                    |
+----+------------------+---------------------------------------------------------------------------------------+
| 01 | id               | Visibility: private                                                                   |
|    |                  | Type: Integer                                                                         |
|    |                  | Purpose: Primary key identifier for the voucher                                       |
+----+------------------+---------------------------------------------------------------------------------------+
| 02 | voucherName      | Visibility: private                                                                   |
|    |                  | Type: String                                                                          |
|    |                  | Purpose: Display name of the voucher                                                  |
+----+------------------+---------------------------------------------------------------------------------------+
| 03 | voucherDescription| Visibility: private                                                                  |
|    |                  | Type: String                                                                          |
|    |                  | Purpose: Detailed description of voucher benefits                                     |
+----+------------------+---------------------------------------------------------------------------------------+
| 04 | voucherCode      | Visibility: private                                                                   |
|    |                  | Type: String                                                                          |
|    |                  | Purpose: Unique redemption code for the voucher                                       |
+----+------------------+---------------------------------------------------------------------------------------+
| 05 | discountType     | Visibility: private                                                                   |
|    |                  | Type: DiscountType (enum)                                                             |
|    |                  | Purpose: Type of discount (AMOUNT for fixed amount, PERCENTAGE for percentage off)    |
+----+------------------+---------------------------------------------------------------------------------------+
| 06 | discountAmount   | Visibility: private                                                                   |
|    |                  | Type: Integer                                                                         |
|    |                  | Purpose: Discount value (amount in cents or percentage as integer)                    |
+----+------------------+---------------------------------------------------------------------------------------+
| 07 | effectiveFrom    | Visibility: private                                                                   |
|    |                  | Type: LocalDateTime                                                                   |
|    |                  | Purpose: Start date/time when voucher becomes valid                                   |
+----+------------------+---------------------------------------------------------------------------------------+
| 08 | effectiveTo      | Visibility: private                                                                   |
|    |                  | Type: LocalDateTime                                                                   |
|    |                  | Purpose: End date/time when voucher expires                                           |
+----+------------------+---------------------------------------------------------------------------------------+
| 09 | maxUsage         | Visibility: private                                                                   |
|    |                  | Type: Integer                                                                         |
|    |                  | Purpose: Maximum number of times voucher can be redeemed                              |
+----+------------------+---------------------------------------------------------------------------------------+
| 10 | usedCount        | Visibility: private                                                                   |
|    |                  | Type: Integer                                                                         |
|    |                  | Purpose: Current count of redemptions                                                 |
+----+------------------+---------------------------------------------------------------------------------------+
| 11 | voucherStatus    | Visibility: private                                                                   |
|    |                  | Type: VoucherStatus (ManyToOne relationship)                                          |
|    |                  | Purpose: Current status (active, expired, depleted, etc.)                             |
+====+==================+=======================================================================================+
| Methods/Operations (Standard getters and setters for all attributes)                                         |
+----+------------------+---------------------------------------------------------------------------------------+

### 3.2 service.sllbackend.enumerator

This package contains enumeration classes defining fixed sets of values used throughout the application.

#### 3.2.1 AppointmentStatus

Defines possible states of an appointment throughout its lifecycle.

+----+------------------+---------------------------------------------------------------------------------------+
| No | Name             | Description                                                                           |
+====+==================+=======================================================================================+
| Enum Values                                                                                                   |
+----+------------------+---------------------------------------------------------------------------------------+
| 01 | PENDING          | Purpose: Appointment request submitted, awaiting confirmation                         |
+----+------------------+---------------------------------------------------------------------------------------+
| 02 | REGISTERED       | Purpose: Appointment confirmed and scheduled                                          |
+----+------------------+---------------------------------------------------------------------------------------+
| 03 | STARTED          | Purpose: Service has begun                                                            |
+----+------------------+---------------------------------------------------------------------------------------+
| 04 | COMPLETED        | Purpose: Service finished successfully                                                |
+----+------------------+---------------------------------------------------------------------------------------+
| 05 | RESCHEDULED      | Purpose: Appointment moved to different time                                          |
+----+------------------+---------------------------------------------------------------------------------------+
| 06 | CANCELLED        | Purpose: Appointment cancelled by customer or salon                                   |
+----+------------------+---------------------------------------------------------------------------------------+

#### 3.2.2 AccountStatus

Defines possible states of user and staff accounts.

+----+------------------+---------------------------------------------------------------------------------------+
| No | Name             | Description                                                                           |
+====+==================+=======================================================================================+
| Enum Values                                                                                                   |
+----+------------------+---------------------------------------------------------------------------------------+
| 01 | ACTIVE           | Purpose: Account is active and can be used                                            |
+----+------------------+---------------------------------------------------------------------------------------+
| 02 | DEACTIVATED      | Purpose: Account temporarily disabled                                                 |
+----+------------------+---------------------------------------------------------------------------------------+
| 03 | BANNED           | Purpose: Account permanently banned due to policy violation                           |
+----+------------------+---------------------------------------------------------------------------------------+

#### 3.2.3 Gender

Defines gender options for users and staff.

+----+------------------+---------------------------------------------------------------------------------------+
| No | Name             | Description                                                                           |
+====+==================+=======================================================================================+
| Enum Values                                                                                                   |
+----+------------------+---------------------------------------------------------------------------------------+
| 01 | MALE             | Purpose: Male gender                                                                  |
+----+------------------+---------------------------------------------------------------------------------------+
| 02 | FEMALE           | Purpose: Female gender                                                                |
+----+------------------+---------------------------------------------------------------------------------------+

#### 3.2.4 StaffStatus

Defines employment status for staff members.

+----+------------------+---------------------------------------------------------------------------------------+
| No | Name             | Description                                                                           |
+====+==================+=======================================================================================+
| Enum Values                                                                                                   |
+----+------------------+---------------------------------------------------------------------------------------+
| 01 | ACTIVE           | Purpose: Staff member is currently employed and working                               |
+----+------------------+---------------------------------------------------------------------------------------+
| 02 | ON_LEAVE         | Purpose: Staff member on temporary leave (vacation, medical, etc.)                    |
+----+------------------+---------------------------------------------------------------------------------------+
| 03 | TERMINATED       | Purpose: Employment ended (quit, fired, contract expired)                             |
+----+------------------+---------------------------------------------------------------------------------------+

#### 3.2.5 DiscountType

Defines types of discounts for promotions and vouchers.

+----+------------------+---------------------------------------------------------------------------------------+
| No | Name             | Description                                                                           |
+====+==================+=======================================================================================+
| Enum Values                                                                                                   |
+----+------------------+---------------------------------------------------------------------------------------+
| 01 | AMOUNT           | Purpose: Fixed amount discount (e.g., $10 off)                                        |
+----+------------------+---------------------------------------------------------------------------------------+
| 02 | PERCENTAGE       | Purpose: Percentage-based discount (e.g., 15% off)                                    |
+----+------------------+---------------------------------------------------------------------------------------+

### 3.3 service.sllbackend.repository

This package contains Spring Data JPA repository interfaces for database operations.

#### 3.3.1 AppointmentRepo

Repository interface for Appointment entity database operations.

+----+------------------+---------------------------------------------------------------------------------------+
| No | Name             | Description                                                                           |
+====+==================+=======================================================================================+
| Methods/Operations                                                                                            |
+----+------------------+---------------------------------------------------------------------------------------+
| 01 | findByUserIdIgnoreCaseContainingAndStatusIn | Visibility: public (interface method)                  |
|    |                  | Return: List<Appointment>                                                             |
|    |                  | Purpose: Find appointments by user ID and filter by statuses                          |
|    |                  | Parameters:                                                                           |
|    |                  | - userAccountId:int, the user account identifier                                      |
|    |                  | - statuses:List<AppointmentStatus>, list of statuses to filter                        |
+----+------------------+---------------------------------------------------------------------------------------+
| 02 | findByNameIgnoreCaseContainingAndStatusIn | Visibility: public (interface method)                    |
|    |                  | Return: List<Appointment>                                                             |
|    |                  | Purpose: Search appointments by customer name (case-insensitive) and status           |
|    |                  | Parameters:                                                                           |
|    |                  | - name:String, customer name search term                                              |
|    |                  | - statuses:List<AppointmentStatus>, list of statuses to filter                        |
+----+------------------+---------------------------------------------------------------------------------------+
| 03 | findByPhoneNumberAndStatusIn | Visibility: public (interface method)                             |
|    |                  | Return: List<Appointment>                                                             |
|    |                  | Purpose: Find appointments by phone number and status                                 |
|    |                  | Parameters:                                                                           |
|    |                  | - phoneNumber:String, phone number to search                                          |
|    |                  | - statuses:List<AppointmentStatus>, list of statuses to filter                        |
+----+------------------+---------------------------------------------------------------------------------------+
| 04 | findByScheduledAtBetween | Visibility: public (interface method)                                     |
|    |                  | Return: List<Appointment>                                                             |
|    |                  | Purpose: Find appointments scheduled within a date range                              |
|    |                  | Parameters:                                                                           |
|    |                  | - start:LocalDateTime, start of date range                                            |
|    |                  | - end:LocalDateTime, end of date range                                                |
+----+------------------+---------------------------------------------------------------------------------------+
| 05 | save             | Visibility: public (inherited from JpaRepository)                                     |
|    |                  | Return: Appointment                                                                   |
|    |                  | Purpose: Save or update an appointment                                                |
|    |                  | Parameters:                                                                           |
|    |                  | - appointment:Appointment, the appointment entity to save                             |
+----+------------------+---------------------------------------------------------------------------------------+
| 06 | findById         | Visibility: public (inherited from JpaRepository)                                     |
|    |                  | Return: Optional<Appointment>                                                         |
|    |                  | Purpose: Find appointment by ID                                                       |
|    |                  | Parameters:                                                                           |
|    |                  | - id:Long, primary key of the appointment                                             |
+----+------------------+---------------------------------------------------------------------------------------+
| 07 | deleteById       | Visibility: public (inherited from JpaRepository)                                     |
|    |                  | Return: void                                                                          |
|    |                  | Purpose: Delete appointment by ID                                                     |
|    |                  | Parameters:                                                                           |
|    |                  | - id:Long, primary key of the appointment to delete                                   |
+----+------------------+---------------------------------------------------------------------------------------+

#### 3.3.2 UserAccountRepo

Repository interface for UserAccount entity database operations.

+----+------------------+---------------------------------------------------------------------------------------+
| No | Name             | Description                                                                           |
+====+==================+=======================================================================================+
| Methods/Operations                                                                                            |
+----+------------------+---------------------------------------------------------------------------------------+
| 01 | existsByUsername | Visibility: public (interface method)                                                 |
|    |                  | Return: Boolean                                                                       |
|    |                  | Purpose: Check if username already exists                                             |
|    |                  | Parameters:                                                                           |
|    |                  | - username:String, username to check                                                  |
+----+------------------+---------------------------------------------------------------------------------------+
| 02 | existsByEmail    | Visibility: public (interface method)                                                 |
|    |                  | Return: Boolean                                                                       |
|    |                  | Purpose: Check if email already exists                                                |
|    |                  | Parameters:                                                                           |
|    |                  | - email:String, email to check                                                        |
+----+------------------+---------------------------------------------------------------------------------------+
| 03 | existsByPhoneNumber | Visibility: public (interface method)                                              |
|    |                  | Return: Boolean                                                                       |
|    |                  | Purpose: Check if phone number already exists                                         |
|    |                  | Parameters:                                                                           |
|    |                  | - phoneNumber:String, phone number to check                                           |
+----+------------------+---------------------------------------------------------------------------------------+
| 04 | findConflicts    | Visibility: public (interface method)                                                 |
|    |                  | Return: List<UserAccount>                                                             |
|    |                  | Purpose: Find accounts with conflicting username, email, or phone (excluding current) |
|    |                  | Parameters:                                                                           |
|    |                  | - username:String, username to check                                                  |
|    |                  | - email:String, email to check                                                        |
|    |                  | - phoneNumber:String, phone number to check                                           |
|    |                  | - currentUserId:Long, current user ID to exclude from search                          |
+----+------------------+---------------------------------------------------------------------------------------+

### 3.4 service.sllbackend.service

This package contains business logic service interfaces defining application operations.

#### 3.4.1 AppointmentService

Service interface for appointment-related business operations.

+----+------------------+---------------------------------------------------------------------------------------+
| No | Name             | Description                                                                           |
+====+==================+=======================================================================================+
| Methods/Operations                                                                                            |
+----+------------------+---------------------------------------------------------------------------------------+
| 01 | getByIdAndStatus | Visibility: public (interface method)                                                 |
|    |                  | Return: List<Appointment>                                                             |
|    |                  | Purpose: Retrieve appointments by user ID filtered by status list                     |
|    |                  | Parameters:                                                                           |
|    |                  | - id:int, user account identifier                                                     |
|    |                  | - status:List<AppointmentStatus>, list of statuses to filter (null for all)           |
+----+------------------+---------------------------------------------------------------------------------------+
| 02 | getByNameAndStatus | Visibility: public (interface method)                                               |
|    |                  | Return: List<Appointment>                                                             |
|    |                  | Purpose: Search appointments by customer name with status filtering                   |
|    |                  | Parameters:                                                                           |
|    |                  | - name:String, customer name search term                                              |
|    |                  | - status:List<AppointmentStatus>, list of statuses to filter (null for all)           |
+----+------------------+---------------------------------------------------------------------------------------+
| 03 | getByPhoneNumberAndStatus | Visibility: public (interface method)                                        |
|    |                  | Return: List<Appointment>                                                             |
|    |                  | Purpose: Find appointments by phone number with status filtering                      |
|    |                  | Parameters:                                                                           |
|    |                  | - phoneNumber:String, phone number to search                                          |
|    |                  | - status:List<AppointmentStatus>, list of statuses to filter (null for all)           |
+----+------------------+---------------------------------------------------------------------------------------+
| 04 | findById         | Visibility: public (interface method)                                                 |
|    |                  | Return: Appointment                                                                   |
|    |                  | Purpose: Find appointment by ID, throws exception if not found                        |
|    |                  | Parameters:                                                                           |
|    |                  | - id:Long, appointment identifier                                                     |
+----+------------------+---------------------------------------------------------------------------------------+
| 05 | getRequestedServices | Visibility: public (interface method)                                             |
|    |                  | Return: List<RequestedService>                                                        |
|    |                  | Purpose: Retrieve list of services requested for an appointment                       |
|    |                  | Parameters:                                                                           |
|    |                  | - appointmentId:Integer, appointment identifier                                       |
+----+------------------+---------------------------------------------------------------------------------------+
| 06 | getDetailsByAppointmentId | Visibility: public (interface method)                                        |
|    |                  | Return: AppointmentDetails                                                            |
|    |                  | Purpose: Retrieve detailed information for an appointment                             |
|    |                  | Parameters:                                                                           |
|    |                  | - appointmentId:Integer, appointment identifier                                       |
+----+------------------+---------------------------------------------------------------------------------------+
| 07 | save             | Visibility: public (interface method)                                                 |
|    |                  | Return: void                                                                          |
|    |                  | Purpose: Save or update appointment entity                                            |
|    |                  | Parameters:                                                                           |
|    |                  | - appointment:Appointment, appointment entity to save                                 |
+----+------------------+---------------------------------------------------------------------------------------+
| 08 | register         | Visibility: public (interface method)                                                 |
|    |                  | Return: void                                                                          |
|    |                  | Purpose: Register a new appointment with services and scheduling                      |
|    |                  | Parameters:                                                                           |
|    |                  | - appointmentRegisterDTO:AppointmentRegisterDTO, data transfer object with            |
|    |                  |   appointment details                                                                 |
+----+------------------+---------------------------------------------------------------------------------------+
| 09 | findAvailableStaff | Visibility: public (interface method)                                               |
|    |                  | Return: List<AvailableStaffDTO>                                                       |
|    |                  | Purpose: Find staff members available for given date, time, and service duration      |
|    |                  | Parameters:                                                                           |
|    |                  | - date:LocalDate, appointment date                                                    |
|    |                  | - startTime:LocalTime, appointment start time                                         |
|    |                  | - durationMinutes:int, total service duration in minutes                              |
+----+------------------+---------------------------------------------------------------------------------------+

### 3.5 service.sllbackend.web.dto

This package contains Data Transfer Objects for API request/response handling.

#### 3.5.1 AppointmentRegisterDTO

DTO for capturing appointment registration information from client.

+----+------------------+---------------------------------------------------------------------------------------+
| No | Name             | Description                                                                           |
+====+==================+=======================================================================================+
| Attributes                                                                                                    |
+----+------------------+---------------------------------------------------------------------------------------+
| 01 | selectedServices | Visibility: private                                                                   |
|    |                  | Type: String                                                                          |
|    |                  | Purpose: Comma-separated list of service IDs selected for appointment                 |
|    |                  | Validation: @NotBlank                                                                 |
+----+------------------+---------------------------------------------------------------------------------------+
| 02 | appointmentDate  | Visibility: private                                                                   |
|    |                  | Type: LocalDate                                                                       |
|    |                  | Purpose: Desired date for appointment                                                 |
|    |                  | Validation: @NotNull, @Future (must be in future)                                     |
+----+------------------+---------------------------------------------------------------------------------------+
| 03 | appointmentTime  | Visibility: private                                                                   |
|    |                  | Type: LocalTime                                                                       |
|    |                  | Purpose: Desired start time for appointment                                           |
|    |                  | Validation: @NotNull                                                                  |
+----+------------------+---------------------------------------------------------------------------------------+
| 04 | endTime          | Visibility: private                                                                   |
|    |                  | Type: LocalTime                                                                       |
|    |                  | Purpose: Expected end time for appointment                                            |
|    |                  | Validation: @NotNull                                                                  |
+----+------------------+---------------------------------------------------------------------------------------+
| 05 | name             | Visibility: private                                                                   |
|    |                  | Type: String                                                                          |
|    |                  | Purpose: Customer name for the appointment                                            |
|    |                  | Validation: @NotBlank                                                                 |
+----+------------------+---------------------------------------------------------------------------------------+
| 06 | phoneNumber      | Visibility: private                                                                   |
|    |                  | Type: String                                                                          |
|    |                  | Purpose: Contact phone number                                                         |
|    |                  | Validation: @NotBlank, @Pattern (7-15 digits with optional + prefix)                  |
+----+------------------+---------------------------------------------------------------------------------------+
| 07 | staffId          | Visibility: private                                                                   |
|    |                  | Type: Integer                                                                         |
|    |                  | Purpose: Optional preferred staff member ID                                           |
+----+------------------+---------------------------------------------------------------------------------------+
| 08 | totalDuration    | Visibility: private                                                                   |
|    |                  | Type: Integer                                                                         |
|    |                  | Purpose: Total duration of all services in minutes                                    |
+----+------------------+---------------------------------------------------------------------------------------+
| 09 | totalPrice       | Visibility: private                                                                   |
|    |                  | Type: Long                                                                            |
|    |                  | Purpose: Total price for all services                                                 |
+====+==================+=======================================================================================+
| Methods/Operations (Standard getters and setters for all attributes)                                         |
+----+------------------+---------------------------------------------------------------------------------------+

#### 3.5.2 UserProfileDTO

DTO for user profile information display and updates.

+----+------------------+---------------------------------------------------------------------------------------+
| No | Name             | Description                                                                           |
+====+==================+=======================================================================================+
| Attributes                                                                                                    |
+----+------------------+---------------------------------------------------------------------------------------+
| 01 | username         | Visibility: private                                                                   |
|    |                  | Type: String                                                                          |
|    |                  | Purpose: User's username                                                              |
+----+------------------+---------------------------------------------------------------------------------------+
| 02 | email            | Visibility: private                                                                   |
|    |                  | Type: String                                                                          |
|    |                  | Purpose: User's email address                                                         |
+----+------------------+---------------------------------------------------------------------------------------+
| 03 | phoneNumber      | Visibility: private                                                                   |
|    |                  | Type: String                                                                          |
|    |                  | Purpose: User's phone number                                                          |
+----+------------------+---------------------------------------------------------------------------------------+
| 04 | gender           | Visibility: private                                                                   |
|    |                  | Type: Gender                                                                          |
|    |                  | Purpose: User's gender                                                                |
+----+------------------+---------------------------------------------------------------------------------------+
| 05 | birthDate        | Visibility: private                                                                   |
|    |                  | Type: LocalDate                                                                       |
|    |                  | Purpose: User's birth date                                                            |
+====+==================+=======================================================================================+
| Methods/Operations (Standard getters and setters for all attributes)                                         |
+----+------------------+---------------------------------------------------------------------------------------+

### 3.6 service.sllbackend.web.mvc

This package contains Spring MVC controller classes handling HTTP requests.

#### 3.6.1 AuthController

Controller for user authentication and registration.

+----+------------------+---------------------------------------------------------------------------------------+
| No | Name             | Description                                                                           |
+====+==================+=======================================================================================+
| Attributes                                                                                                    |
+----+------------------+---------------------------------------------------------------------------------------+
| 01 | registerService  | Visibility: private (final, injected via constructor)                                 |
|    |                  | Type: RegisterService                                                                 |
|    |                  | Purpose: Service dependency for user registration logic                               |
+====+==================+=======================================================================================+
| Methods/Operations                                                                                            |
+----+------------------+---------------------------------------------------------------------------------------+
| 01 | showLoginForm    | Visibility: public                                                                    |
|    |                  | Return: String                                                                        |
|    |                  | Purpose: Display login page                                                           |
|    |                  | Mapping: GET /login                                                                   |
|    |                  | Parameters: None                                                                      |
+----+------------------+---------------------------------------------------------------------------------------+
| 02 | showRegisterForm | Visibility: public                                                                    |
|    |                  | Return: String                                                                        |
|    |                  | Purpose: Display user registration form                                               |
|    |                  | Mapping: GET /register                                                                |
|    |                  | Parameters:                                                                           |
|    |                  | - model:Model, Spring MVC model for view data                                         |
+----+------------------+---------------------------------------------------------------------------------------+
| 03 | register         | Visibility: public                                                                    |
|    |                  | Return: String                                                                        |
|    |                  | Purpose: Process user registration form submission                                    |
|    |                  | Mapping: POST /register                                                               |
|    |                  | Parameters:                                                                           |
|    |                  | - userRegisterDTO:UserRegisterDTO, validated registration data                        |
|    |                  | - bindingResult:BindingResult, validation results                                     |
|    |                  | - model:Model, Spring MVC model for view data                                         |
+----+------------------+---------------------------------------------------------------------------------------+

#### 3.6.2 HomeController

Controller for home page and main navigation.

+----+------------------+---------------------------------------------------------------------------------------+
| No | Name             | Description                                                                           |
+====+==================+=======================================================================================+
| Attributes                                                                                                    |
+----+------------------+---------------------------------------------------------------------------------------+
| 01 | servicesService  | Visibility: private (final, injected via constructor)                                 |
|    |                  | Type: ServicesService                                                                 |
|    |                  | Purpose: Service for retrieving service catalog                                       |
+----+------------------+---------------------------------------------------------------------------------------+
| 02 | productsService  | Visibility: private (final, injected via constructor)                                 |
|    |                  | Type: ProductsService                                                                 |
|    |                  | Purpose: Service for retrieving product catalog                                       |
+====+==================+=======================================================================================+
| Methods/Operations                                                                                            |
+----+------------------+---------------------------------------------------------------------------------------+
| 01 | home             | Visibility: public                                                                    |
|    |                  | Return: String                                                                        |
|    |                  | Purpose: Display home page with featured services and products                        |
|    |                  | Mapping: GET /                                                                        |
|    |                  | Parameters:                                                                           |
|    |                  | - model:Model, Spring MVC model for view data                                         |
|    |                  | - principal:Principal, current authenticated user (nullable)                          |
+----+------------------+---------------------------------------------------------------------------------------+

### 3.7 service.sllbackend.utils

This package contains utility classes providing helper functions.

#### 3.7.1 ValidationUtils

Utility class for validating user and staff data to prevent duplicates and ensure data integrity.

+----+------------------+---------------------------------------------------------------------------------------+
| No | Name             | Description                                                                           |
+====+==================+=======================================================================================+
| Attributes                                                                                                    |
+----+------------------+---------------------------------------------------------------------------------------+
| 01 | userAccountRepo  | Visibility: private (final, injected via constructor)                                 |
|    |                  | Type: UserAccountRepo                                                                 |
|    |                  | Purpose: Repository for user account database operations                              |
+----+------------------+---------------------------------------------------------------------------------------+
| 02 | staffRepo        | Visibility: private (final, injected via constructor)                                 |
|    |                  | Type: StaffRepo                                                                       |
|    |                  | Purpose: Repository for staff database operations                                     |
+====+==================+=======================================================================================+
| Methods/Operations                                                                                            |
+----+------------------+---------------------------------------------------------------------------------------+
| 01 | validateNewUser  | Visibility: public                                                                    |
|    |                  | Return: void                                                                          |
|    |                  | Purpose: Validate new user registration data for duplicates                           |
|    |                  | Parameters:                                                                           |
|    |                  | - username:String, proposed username                                                  |
|    |                  | - email:String, proposed email                                                        |
|    |                  | - phoneNumber:String, proposed phone number                                           |
|    |                  | Throws: IllegalArgumentException if duplicate found                                   |
+----+------------------+---------------------------------------------------------------------------------------+
| 02 | validateUserProfile | Visibility: public                                                                 |
|    |                  | Return: void                                                                          |
|    |                  | Purpose: Validate user profile updates for conflicts with other users                 |
|    |                  | Parameters:                                                                           |
|    |                  | - currentUserId:Long, ID of user being updated                                        |
|    |                  | - username:String, proposed username                                                  |
|    |                  | - email:String, proposed email                                                        |
|    |                  | - phoneNumber:String, proposed phone number                                           |
|    |                  | - birthDate:LocalDate, proposed birth date                                            |
|    |                  | Throws: IllegalArgumentException if conflict or invalid data found                    |
+----+------------------+---------------------------------------------------------------------------------------+
| 03 | validateStaffProfile | Visibility: public                                                                |
|    |                  | Return: void                                                                          |
|    |                  | Purpose: Validate staff profile update for name conflicts                             |
|    |                  | Parameters:                                                                           |
|    |                  | - currentStaffId:Long, ID of staff being updated                                      |
|    |                  | - username:String, proposed staff name                                                |
|    |                  | Throws: IllegalArgumentException if name conflict found                               |
+----+------------------+---------------------------------------------------------------------------------------+

#### 3.7.2 BadWordFilter

Utility class for filtering inappropriate content from user input.

+----+------------------+---------------------------------------------------------------------------------------+
| No | Name             | Description                                                                           |
+====+==================+=======================================================================================+
| Attributes                                                                                                    |
+----+------------------+---------------------------------------------------------------------------------------+
| 01 | badWords         | Visibility: private (final)                                                           |
|    |                  | Type: Set<String>                                                                     |
|    |                  | Purpose: Set of inappropriate words loaded from configuration file                    |
+====+==================+=======================================================================================+
| Methods/Operations                                                                                            |
+----+------------------+---------------------------------------------------------------------------------------+
| 01 | init             | Visibility: public                                                                    |
|    |                  | Return: void                                                                          |
|    |                  | Purpose: Initialize filter by loading bad words from badwords.txt file                |
|    |                  | Annotation: @PostConstruct (runs after bean construction)                             |
|    |                  | Parameters: None                                                                      |
+----+------------------+---------------------------------------------------------------------------------------+
| 02 | containsBadWord  | Visibility: public                                                                    |
|    |                  | Return: boolean                                                                       |
|    |                  | Purpose: Check if given text contains any inappropriate words                         |
|    |                  | Parameters:                                                                           |
|    |                  | - text:String, text to check                                                          |
+----+------------------+---------------------------------------------------------------------------------------+
| 03 | findBadWords     | Visibility: public                                                                    |
|    |                  | Return: Set<String>                                                                   |
|    |                  | Purpose: Return set of all inappropriate words found in text                          |
|    |                  | Parameters:                                                                           |
|    |                  | - text:String, text to analyze                                                        |
+----+------------------+---------------------------------------------------------------------------------------+
| 04 | loadBadWords     | Visibility: private                                                                   |
|    |                  | Return: void                                                                          |
|    |                  | Purpose: Load bad words from classpath resource file                                  |
|    |                  | Parameters: None                                                                      |
+----+------------------+---------------------------------------------------------------------------------------+

#### 3.7.3 EncryptSSN

Utility class for encrypting and decrypting Social Security Numbers.

+----+------------------+---------------------------------------------------------------------------------------+
| No | Name             | Description                                                                           |
+====+==================+=======================================================================================+
| Methods/Operations (Static utility methods)                                                                   |
+----+------------------+---------------------------------------------------------------------------------------+
| 01 | encrypt          | Visibility: public static                                                             |
|    |                  | Return: String                                                                        |
|    |                  | Purpose: Encrypt SSN for secure storage                                               |
|    |                  | Parameters:                                                                           |
|    |                  | - ssn:String, plain text SSN                                                          |
|    |                  | Throws: Exception if encryption fails                                                 |
+----+------------------+---------------------------------------------------------------------------------------+
| 02 | decrypt          | Visibility: public static                                                             |
|    |                  | Return: String                                                                        |
|    |                  | Purpose: Decrypt SSN for display or processing                                        |
|    |                  | Parameters:                                                                           |
|    |                  | - encryptedSSN:String, encrypted SSN                                                  |
|    |                  | Throws: Exception if decryption fails                                                 |
+----+------------------+---------------------------------------------------------------------------------------+

---

## 4. Additional Entity Classes Summary

Due to the comprehensive nature of this system (66 entity classes total), here is a summary of additional key entities:

### Appointment Management Entities:
- **AppointmentDetails**: Tracks actual start/end times and user assignment
- **AppointmentInvoice**: Billing information for completed appointments
- **AppointmentFeedback**: Customer satisfaction ratings and comments
- **RequestedService**: Services selected for each appointment

### Staff Management Entities:
- **StaffAccount**: Login credentials for staff members
- **StaffPosition**: Job position definitions (stylist, receptionist, etc.)
- **StaffPayroll**: Monthly payroll calculations
- **StaffCommission**: Commission rates by position and type

### Inventory Entities:
- **InventoryInvoice**: Purchase orders from suppliers
- **InventoryLot**: Current stock levels by batch
- **InventoryTransaction**: Stock movement tracking
- **InventoryConsignment**: Received shipments

### Order Management Entities:
- **OrderInvoice**: Customer product orders
- **Cart**: Shopping cart items
- **OrderInvoiceDetails**: Line items in orders

### Loyalty & Promotions:
- **Loyalty**: Customer loyalty accounts
- **LoyaltyLevel**: Tier definitions (Bronze, Silver, Gold, Platinum)
- **Promotion**: Active promotional campaigns

### Scheduling Entities:
- **ShiftTemplate**: Reusable shift time templates
- **ShiftInstance**: Actual scheduled shifts
- **ShiftAssignment**: Staff assignments to shifts
- **ShiftAttendance**: Check-in/check-out records

---

## 5. Notes

1. All entity classes use Lombok annotations (@Data, @Builder, @NoArgsConstructor, @AllArgsConstructor) to automatically generate getters, setters, builders, and constructors.

2. Repository interfaces extend JpaRepository<Entity, ID> which provides standard CRUD operations automatically.

3. Service interfaces define business logic contracts, with implementations in the service.impl package.

4. DTOs use Jakarta validation annotations (@NotNull, @NotBlank, @Pattern, @Future, etc.) for input validation.

5. Controllers use Spring MVC annotations (@Controller, @RequestMapping, @GetMapping, @PostMapping) for routing.

6. The system follows a layered architecture:
   - **Presentation Layer**: Controllers (web.mvc)
   - **Business Logic Layer**: Services (service, service.impl)
   - **Data Access Layer**: Repositories (repository)
   - **Domain Layer**: Entities (entity)

---

**Document Version**: 1.0  
**Last Updated**: 2025-12-08  
**System**: SalonLoanLoan Backend  
**Framework**: Spring Boot 3.x
