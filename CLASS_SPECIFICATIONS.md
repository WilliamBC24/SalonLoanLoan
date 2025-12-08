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

**Attributes:**

| No | Name | Description |
|----|------|-------------|
| 01 | id | **Visibility:** private<br>**Type:** Integer<br>**Purpose:** Primary key identifier for the user account |
| 02 | username | **Visibility:** private<br>**Type:** String<br>**Purpose:** Unique username for login authentication (max 100 characters) |
| 03 | password | **Visibility:** private<br>**Type:** String<br>**Purpose:** Encrypted password for account security (max 100 characters) |
| 04 | gender | **Visibility:** private<br>**Type:** Gender (enum)<br>**Purpose:** User's gender (MALE, FEMALE) |
| 05 | birthDate | **Visibility:** private<br>**Type:** LocalDate<br>**Purpose:** User's date of birth for age verification and promotions |
| 06 | phoneNumber | **Visibility:** private<br>**Type:** String<br>**Purpose:** Contact phone number for notifications (max 20 characters) |
| 07 | email | **Visibility:** private<br>**Type:** String<br>**Purpose:** Email address for communication (max 100 characters) |
| 08 | phoneVerified | **Visibility:** private<br>**Type:** Boolean<br>**Purpose:** Indicates if phone number has been verified (default: false) |
| 09 | accountStatus | **Visibility:** private<br>**Type:** AccountStatus (enum)<br>**Purpose:** Current account status (ACTIVE, DEACTIVATED, BANNED) (default: ACTIVE) |

**Methods/Operations:**

| No | Name | Description |
|----|------|-------------|
| 01 | getId | **Visibility:** public<br>**Return:** Integer<br>**Purpose:** Retrieve the unique identifier of the user account<br>**Parameters:** None |
| 02 | setId | **Visibility:** public<br>**Return:** void<br>**Purpose:** Set the unique identifier for the user account<br>**Parameters:** `id:Integer` - the primary key value to set |
| 03 | getUsername | **Visibility:** public<br>**Return:** String<br>**Purpose:** Retrieve the username for authentication<br>**Parameters:** None |
| 04 | setUsername | **Visibility:** public<br>**Return:** void<br>**Purpose:** Set the username for the account<br>**Parameters:** `username:String` - unique username value |
| 05 | getPassword | **Visibility:** public<br>**Return:** String<br>**Purpose:** Retrieve encrypted password<br>**Parameters:** None |
| 06 | setPassword | **Visibility:** public<br>**Return:** void<br>**Purpose:** Set encrypted password for authentication<br>**Parameters:** `password:String` - encrypted password value |
| 07 | getGender | **Visibility:** public<br>**Return:** Gender<br>**Purpose:** Retrieve user's gender<br>**Parameters:** None |
| 08 | setGender | **Visibility:** public<br>**Return:** void<br>**Purpose:** Set user's gender<br>**Parameters:** `gender:Gender` - enum value (MALE, FEMALE) |
| 09 | getBirthDate | **Visibility:** public<br>**Return:** LocalDate<br>**Purpose:** Retrieve user's date of birth<br>**Parameters:** None |
| 10 | setBirthDate | **Visibility:** public<br>**Return:** void<br>**Purpose:** Set user's date of birth<br>**Parameters:** `birthDate:LocalDate` - date of birth value |
| 11 | getPhoneNumber | **Visibility:** public<br>**Return:** String<br>**Purpose:** Retrieve user's contact phone number<br>**Parameters:** None |
| 12 | setPhoneNumber | **Visibility:** public<br>**Return:** void<br>**Purpose:** Set user's contact phone number<br>**Parameters:** `phoneNumber:String` - phone number value |
| 13 | getEmail | **Visibility:** public<br>**Return:** String<br>**Purpose:** Retrieve user's email address<br>**Parameters:** None |
| 14 | setEmail | **Visibility:** public<br>**Return:** void<br>**Purpose:** Set user's email address<br>**Parameters:** `email:String` - email address value |
| 15 | getPhoneVerified | **Visibility:** public<br>**Return:** Boolean<br>**Purpose:** Check if phone number is verified<br>**Parameters:** None |
| 16 | setPhoneVerified | **Visibility:** public<br>**Return:** void<br>**Purpose:** Set phone verification status<br>**Parameters:** `phoneVerified:Boolean` - verification status |
| 17 | getAccountStatus | **Visibility:** public<br>**Return:** AccountStatus<br>**Purpose:** Retrieve current account status<br>**Parameters:** None |
| 18 | setAccountStatus | **Visibility:** public<br>**Return:** void<br>**Purpose:** Set account status (ACTIVE, DEACTIVATED, BANNED)<br>**Parameters:** `accountStatus:AccountStatus` - enum value for account status |

#### 3.1.2 Appointment

Manages customer appointment bookings, scheduling, and tracking appointment lifecycle from registration to completion.

**Attributes:**

| No | Name | Description |
|----|------|-------------|
| 01 | id | **Visibility:** private<br>**Type:** Integer<br>**Purpose:** Primary key identifier for the appointment |
| 02 | registeredAt | **Visibility:** private<br>**Type:** LocalDateTime<br>**Purpose:** Timestamp when appointment was registered (default: current time) |
| 03 | scheduledAt | **Visibility:** private<br>**Type:** LocalDateTime<br>**Purpose:** Scheduled date and time for the appointment |
| 04 | name | **Visibility:** private<br>**Type:** String<br>**Purpose:** Name of the customer making the appointment |
| 05 | phoneNumber | **Visibility:** private<br>**Type:** String<br>**Purpose:** Contact phone number for appointment confirmation (max 20 characters) |
| 06 | status | **Visibility:** private<br>**Type:** AppointmentStatus (enum)<br>**Purpose:** Current appointment status (PENDING, REGISTERED, STARTED, COMPLETED, RESCHEDULED, CANCELLED) (default: PENDING) |
| 07 | preferredStaffId | **Visibility:** private<br>**Type:** Staff (ManyToOne relationship)<br>**Purpose:** Customer's preferred staff member for the appointment |
| 08 | responsibleStaffId | **Visibility:** private<br>**Type:** Staff (ManyToOne relationship)<br>**Purpose:** Assigned staff member responsible for this appointment |

**Methods/Operations:**

| No | Name | Description |
|----|------|-------------|
| 01 | getId | **Visibility:** public<br>**Return:** Integer<br>**Purpose:** Retrieve appointment identifier<br>**Parameters:** None |
| 02 | getRegisteredAt | **Visibility:** public<br>**Return:** LocalDateTime<br>**Purpose:** Retrieve registration timestamp<br>**Parameters:** None |
| 03 | getScheduledAt | **Visibility:** public<br>**Return:** LocalDateTime<br>**Purpose:** Retrieve scheduled appointment time<br>**Parameters:** None |
| 04 | getStatus | **Visibility:** public<br>**Return:** AppointmentStatus<br>**Purpose:** Retrieve current appointment status<br>**Parameters:** None |
| 05 | setStatus | **Visibility:** public<br>**Return:** void<br>**Purpose:** Update appointment status (e.g., from PENDING to REGISTERED)<br>**Parameters:** `status:AppointmentStatus` - new status value |

*Standard getters and setters for all attributes*

#### 3.1.3 Staff

Stores staff member personal and employment information for payroll, scheduling, and service assignment.

**Attributes:**

| No | Name | Description |
|----|------|-------------|
| 01 | id | **Visibility:** private<br>**Type:** Integer<br>**Purpose:** Primary key identifier for the staff member |
| 02 | dateHired | **Visibility:** private<br>**Type:** LocalDate<br>**Purpose:** Date when staff member was hired |
| 03 | endOfContractDate | **Visibility:** private<br>**Type:** LocalDate<br>**Purpose:** Contract end date (nullable for permanent staff) |
| 04 | socialSecurityNum | **Visibility:** private<br>**Type:** String<br>**Purpose:** Encrypted social security number for legal compliance |
| 05 | name | **Visibility:** private<br>**Type:** String<br>**Purpose:** Full name of the staff member |
| 06 | birthDate | **Visibility:** private<br>**Type:** LocalDate<br>**Purpose:** Date of birth for age verification |
| 07 | email | **Visibility:** private<br>**Type:** String<br>**Purpose:** Email address for staff communication |
| 08 | staffStatus | **Visibility:** private<br>**Type:** StaffStatus (enum)<br>**Purpose:** Employment status (ACTIVE, ON_LEAVE, TERMINATED) |

*Standard getters and setters for all attributes*

#### 3.1.4 Product

Represents physical products available for purchase, including pricing and inventory tracking.

**Attributes:**

| No | Name | Description |
|----|------|-------------|
| 01 | id | **Visibility:** private<br>**Type:** Integer<br>**Purpose:** Primary key identifier for the product |
| 02 | productName | **Visibility:** private<br>**Type:** String<br>**Purpose:** Name of the product for display and identification |
| 03 | currentPrice | **Visibility:** private<br>**Type:** Integer<br>**Purpose:** Current selling price in smallest currency unit (e.g., cents) |
| 04 | productDescription | **Visibility:** private<br>**Type:** String<br>**Purpose:** Detailed description of product features and benefits |
| 05 | activeStatus | **Visibility:** private<br>**Type:** Boolean<br>**Purpose:** Indicates if product is currently available for sale |

*Standard getters and setters for all attributes*

#### 3.1.5 Service

Defines salon services available for booking, including pricing, duration, and categorization.

**Attributes:**

| No | Name | Description |
|----|------|-------------|
| 01 | id | **Visibility:** private<br>**Type:** Integer<br>**Purpose:** Primary key identifier for the service |
| 02 | serviceName | **Visibility:** private<br>**Type:** String<br>**Purpose:** Name of the service (e.g., "Haircut", "Manicure") |
| 03 | serviceCategory | **Visibility:** private<br>**Type:** ServiceCategory (ManyToOne relationship)<br>**Purpose:** Category classification for organizational purposes |
| 04 | serviceType | **Visibility:** private<br>**Type:** ServiceType (enum)<br>**Purpose:** Type indicator (SINGLE service or COMBO package) |
| 05 | servicePrice | **Visibility:** private<br>**Type:** Integer<br>**Purpose:** Price in smallest currency unit |
| 06 | durationMinutes | **Visibility:** private<br>**Type:** Short<br>**Purpose:** Expected service duration in minutes for scheduling |
| 07 | serviceDescription | **Visibility:** private<br>**Type:** String<br>**Purpose:** Detailed description of service and what it includes |
| 08 | activeStatus | **Visibility:** private<br>**Type:** Boolean<br>**Purpose:** Indicates if service is currently offered |

*Standard getters and setters for all attributes*

#### 3.1.6 Voucher

Manages discount vouchers and promotional codes with usage tracking and conditions.

**Attributes:**

| No | Name | Description |
|----|------|-------------|
| 01 | id | **Visibility:** private<br>**Type:** Integer<br>**Purpose:** Primary key identifier for the voucher |
| 02 | voucherName | **Visibility:** private<br>**Type:** String<br>**Purpose:** Display name of the voucher |
| 03 | voucherDescription | **Visibility:** private<br>**Type:** String<br>**Purpose:** Detailed description of voucher benefits |
| 04 | voucherCode | **Visibility:** private<br>**Type:** String<br>**Purpose:** Unique redemption code for the voucher |
| 05 | discountType | **Visibility:** private<br>**Type:** DiscountType (enum)<br>**Purpose:** Type of discount (AMOUNT for fixed amount, PERCENTAGE for percentage off) |
| 06 | discountAmount | **Visibility:** private<br>**Type:** Integer<br>**Purpose:** Discount value (amount in cents or percentage as integer) |
| 07 | effectiveFrom | **Visibility:** private<br>**Type:** LocalDateTime<br>**Purpose:** Start date/time when voucher becomes valid |
| 08 | effectiveTo | **Visibility:** private<br>**Type:** LocalDateTime<br>**Purpose:** End date/time when voucher expires |
| 09 | maxUsage | **Visibility:** private<br>**Type:** Integer<br>**Purpose:** Maximum number of times voucher can be redeemed |
| 10 | usedCount | **Visibility:** private<br>**Type:** Integer<br>**Purpose:** Current count of redemptions |
| 11 | voucherStatus | **Visibility:** private<br>**Type:** VoucherStatus (ManyToOne relationship)<br>**Purpose:** Current status (active, expired, depleted, etc.) |

*Standard getters and setters for all attributes*

### 3.2 service.sllbackend.enumerator

This package contains enumeration classes defining fixed sets of values used throughout the application.

#### 3.2.1 AppointmentStatus

Defines possible states of an appointment throughout its lifecycle.

**Enum Values:**

| Value | Description |
|-------|-------------|
| PENDING | Appointment request submitted, awaiting confirmation |
| REGISTERED | Appointment confirmed and scheduled |
| STARTED | Service has begun |
| COMPLETED | Service finished successfully |
| RESCHEDULED | Appointment moved to different time |
| CANCELLED | Appointment cancelled by customer or salon |

#### 3.2.2 AccountStatus

Defines possible states of user and staff accounts.

**Enum Values:**

| Value | Description |
|-------|-------------|
| ACTIVE | Account is active and can be used |
| DEACTIVATED | Account temporarily disabled |
| BANNED | Account permanently banned due to policy violation |

#### 3.2.3 Gender

Defines gender options for users and staff.

**Enum Values:**

| Value | Description |
|-------|-------------|
| MALE | Male gender |
| FEMALE | Female gender |

#### 3.2.4 StaffStatus

Defines employment status for staff members.

**Enum Values:**

| Value | Description |
|-------|-------------|
| ACTIVE | Staff member is currently employed and working |
| ON_LEAVE | Staff member on temporary leave (vacation, medical, etc.) |
| TERMINATED | Employment ended (quit, fired, contract expired) |

#### 3.2.5 DiscountType

Defines types of discounts for promotions and vouchers.

**Enum Values:**

| Value | Description |
|-------|-------------|
| AMOUNT | Fixed amount discount (e.g., $10 off) |
| PERCENTAGE | Percentage-based discount (e.g., 15% off) |

### 3.3 service.sllbackend.repository

This package contains Spring Data JPA repository interfaces for database operations.

#### 3.3.1 AppointmentRepo

Repository interface for Appointment entity database operations.

**Methods:**

| No | Name | Description |
|----|------|-------------|
| 01 | findByUserIdIgnoreCaseContainingAndStatusIn | **Visibility:** public<br>**Return:** List&lt;Appointment&gt;<br>**Purpose:** Find appointments by user ID and filter by statuses<br>**Parameters:** `userAccountId:int` - the user account identifier, `statuses:List<AppointmentStatus>` - list of statuses to filter |
| 02 | findByNameIgnoreCaseContainingAndStatusIn | **Visibility:** public<br>**Return:** List&lt;Appointment&gt;<br>**Purpose:** Search appointments by customer name (case-insensitive) and status<br>**Parameters:** `name:String` - customer name search term, `statuses:List<AppointmentStatus>` - list of statuses to filter |
| 03 | findByPhoneNumberAndStatusIn | **Visibility:** public<br>**Return:** List&lt;Appointment&gt;<br>**Purpose:** Find appointments by phone number and status<br>**Parameters:** `phoneNumber:String` - phone number to search, `statuses:List<AppointmentStatus>` - list of statuses to filter |
| 04 | findByScheduledAtBetween | **Visibility:** public<br>**Return:** List&lt;Appointment&gt;<br>**Purpose:** Find appointments scheduled within a date range<br>**Parameters:** `start:LocalDateTime` - start of date range, `end:LocalDateTime` - end of date range |
| 05 | save | **Visibility:** public<br>**Return:** Appointment<br>**Purpose:** Save or update an appointment<br>**Parameters:** `appointment:Appointment` - the appointment entity to save<br>*Inherited from JpaRepository* |
| 06 | findById | **Visibility:** public<br>**Return:** Optional&lt;Appointment&gt;<br>**Purpose:** Find appointment by ID<br>**Parameters:** `id:Long` - primary key of the appointment<br>*Inherited from JpaRepository* |
| 07 | deleteById | **Visibility:** public<br>**Return:** void<br>**Purpose:** Delete appointment by ID<br>**Parameters:** `id:Long` - primary key of the appointment to delete<br>*Inherited from JpaRepository* |

#### 3.3.2 UserAccountRepo

Repository interface for UserAccount entity database operations.

**Methods:**

| No | Name | Description |
|----|------|-------------|
| 01 | existsByUsername | **Visibility:** public<br>**Return:** Boolean<br>**Purpose:** Check if username already exists<br>**Parameters:** `username:String` - username to check |
| 02 | existsByEmail | **Visibility:** public<br>**Return:** Boolean<br>**Purpose:** Check if email already exists<br>**Parameters:** `email:String` - email to check |
| 03 | existsByPhoneNumber | **Visibility:** public<br>**Return:** Boolean<br>**Purpose:** Check if phone number already exists<br>**Parameters:** `phoneNumber:String` - phone number to check |
| 04 | findConflicts | **Visibility:** public<br>**Return:** List&lt;UserAccount&gt;<br>**Purpose:** Find accounts with conflicting username, email, or phone (excluding current)<br>**Parameters:** `username:String` - username to check, `email:String` - email to check, `phoneNumber:String` - phone number to check, `currentUserId:Long` - current user ID to exclude from search |

### 3.4 service.sllbackend.service

This package contains business logic service interfaces defining application operations.

#### 3.4.1 AppointmentService

Service interface for appointment-related business operations.

**Methods:**

| No | Name | Description |
|----|------|-------------|
| 01 | getByIdAndStatus | **Visibility:** public<br>**Return:** List&lt;Appointment&gt;<br>**Purpose:** Retrieve appointments by user ID filtered by status list<br>**Parameters:** `id:int` - user account identifier, `status:List<AppointmentStatus>` - list of statuses to filter (null for all) |
| 02 | getByNameAndStatus | **Visibility:** public<br>**Return:** List&lt;Appointment&gt;<br>**Purpose:** Search appointments by customer name with status filtering<br>**Parameters:** `name:String` - customer name search term, `status:List<AppointmentStatus>` - list of statuses to filter (null for all) |
| 03 | getByPhoneNumberAndStatus | **Visibility:** public<br>**Return:** List&lt;Appointment&gt;<br>**Purpose:** Find appointments by phone number with status filtering<br>**Parameters:** `phoneNumber:String` - phone number to search, `status:List<AppointmentStatus>` - list of statuses to filter (null for all) |
| 04 | findById | **Visibility:** public<br>**Return:** Appointment<br>**Purpose:** Find appointment by ID, throws exception if not found<br>**Parameters:** `id:Long` - appointment identifier |
| 05 | getRequestedServices | **Visibility:** public<br>**Return:** List&lt;RequestedService&gt;<br>**Purpose:** Retrieve list of services requested for an appointment<br>**Parameters:** `appointmentId:Integer` - appointment identifier |
| 06 | getDetailsByAppointmentId | **Visibility:** public<br>**Return:** AppointmentDetails<br>**Purpose:** Retrieve detailed information for an appointment<br>**Parameters:** `appointmentId:Integer` - appointment identifier |
| 07 | save | **Visibility:** public<br>**Return:** void<br>**Purpose:** Save or update appointment entity<br>**Parameters:** `appointment:Appointment` - appointment entity to save |
| 08 | register | **Visibility:** public<br>**Return:** void<br>**Purpose:** Register a new appointment with services and scheduling<br>**Parameters:** `appointmentRegisterDTO:AppointmentRegisterDTO` - data transfer object with appointment details |
| 09 | findAvailableStaff | **Visibility:** public<br>**Return:** List&lt;AvailableStaffDTO&gt;<br>**Purpose:** Find staff members available for given date, time, and service duration<br>**Parameters:** `date:LocalDate` - appointment date, `startTime:LocalTime` - appointment start time, `durationMinutes:int` - total service duration in minutes |

### 3.5 service.sllbackend.web.dto

This package contains Data Transfer Objects for API request/response handling.

#### 3.5.1 AppointmentRegisterDTO

DTO for capturing appointment registration information from client.

**Attributes:**

| No | Name | Description |
|----|------|-------------|
| 01 | selectedServices | **Visibility:** private<br>**Type:** String<br>**Purpose:** Comma-separated list of service IDs selected for appointment<br>**Validation:** @NotBlank |
| 02 | appointmentDate | **Visibility:** private<br>**Type:** LocalDate<br>**Purpose:** Desired date for appointment<br>**Validation:** @NotNull, @Future (must be in future) |
| 03 | appointmentTime | **Visibility:** private<br>**Type:** LocalTime<br>**Purpose:** Desired start time for appointment<br>**Validation:** @NotNull |
| 04 | endTime | **Visibility:** private<br>**Type:** LocalTime<br>**Purpose:** Expected end time for appointment<br>**Validation:** @NotNull |
| 05 | name | **Visibility:** private<br>**Type:** String<br>**Purpose:** Customer name for the appointment<br>**Validation:** @NotBlank |
| 06 | phoneNumber | **Visibility:** private<br>**Type:** String<br>**Purpose:** Contact phone number<br>**Validation:** @NotBlank, @Pattern (7-15 digits with optional + prefix) |
| 07 | staffId | **Visibility:** private<br>**Type:** Integer<br>**Purpose:** Optional preferred staff member ID |
| 08 | totalDuration | **Visibility:** private<br>**Type:** Integer<br>**Purpose:** Total duration of all services in minutes |
| 09 | totalPrice | **Visibility:** private<br>**Type:** Long<br>**Purpose:** Total price for all services |

*Standard getters and setters for all attributes*

#### 3.5.2 UserProfileDTO

DTO for user profile information display and updates.

**Attributes:**

| No | Name | Description |
|----|------|-------------|
| 01 | username | **Visibility:** private<br>**Type:** String<br>**Purpose:** User's username |
| 02 | email | **Visibility:** private<br>**Type:** String<br>**Purpose:** User's email address |
| 03 | phoneNumber | **Visibility:** private<br>**Type:** String<br>**Purpose:** User's phone number |
| 04 | gender | **Visibility:** private<br>**Type:** Gender<br>**Purpose:** User's gender |
| 05 | birthDate | **Visibility:** private<br>**Type:** LocalDate<br>**Purpose:** User's birth date |

*Standard getters and setters for all attributes*

### 3.6 service.sllbackend.web.mvc

This package contains Spring MVC controller classes handling HTTP requests.

#### 3.6.1 AuthController

Controller for user authentication and registration.

**Attributes:**

| No | Name | Description |
|----|------|-------------|
| 01 | registerService | **Visibility:** private (final)<br>**Type:** RegisterService<br>**Purpose:** Service dependency for user registration logic |

**Methods:**

| No | Name | Description |
|----|------|-------------|
| 01 | showLoginForm | **Visibility:** public<br>**Return:** String<br>**Purpose:** Display login page<br>**Mapping:** GET /login<br>**Parameters:** None |
| 02 | showRegisterForm | **Visibility:** public<br>**Return:** String<br>**Purpose:** Display user registration form<br>**Mapping:** GET /register<br>**Parameters:** `model:Model` - Spring MVC model for view data |
| 03 | register | **Visibility:** public<br>**Return:** String<br>**Purpose:** Process user registration form submission<br>**Mapping:** POST /register<br>**Parameters:** `userRegisterDTO:UserRegisterDTO` - validated registration data, `bindingResult:BindingResult` - validation results, `model:Model` - Spring MVC model for view data |

#### 3.6.2 HomeController

Controller for home page and main navigation.

**Attributes:**

| No | Name | Description |
|----|------|-------------|
| 01 | servicesService | **Visibility:** private (final)<br>**Type:** ServicesService<br>**Purpose:** Service for retrieving service catalog |
| 02 | productsService | **Visibility:** private (final)<br>**Type:** ProductsService<br>**Purpose:** Service for retrieving product catalog |

**Methods:**

| No | Name | Description |
|----|------|-------------|
| 01 | home | **Visibility:** public<br>**Return:** String<br>**Purpose:** Display home page with featured services and products<br>**Mapping:** GET /<br>**Parameters:** `model:Model` - Spring MVC model for view data, `principal:Principal` - current authenticated user (nullable) |

### 3.7 service.sllbackend.utils

This package contains utility classes providing helper functions.

#### 3.7.1 ValidationUtils

Utility class for validating user and staff data to prevent duplicates and ensure data integrity.

**Attributes:**

| No | Name | Description |
|----|------|-------------|
| 01 | userAccountRepo | **Visibility:** private (final)<br>**Type:** UserAccountRepo<br>**Purpose:** Repository for user account database operations |
| 02 | staffRepo | **Visibility:** private (final)<br>**Type:** StaffRepo<br>**Purpose:** Repository for staff database operations |

**Methods:**

| No | Name | Description |
|----|------|-------------|
| 01 | validateNewUser | **Visibility:** public<br>**Return:** void<br>**Purpose:** Validate new user registration data for duplicates<br>**Parameters:** `username:String` - proposed username, `email:String` - proposed email, `phoneNumber:String` - proposed phone number<br>**Throws:** IllegalArgumentException if duplicate found |
| 02 | validateUserProfile | **Visibility:** public<br>**Return:** void<br>**Purpose:** Validate user profile updates for conflicts with other users<br>**Parameters:** `currentUserId:Long` - ID of user being updated, `username:String` - proposed username, `email:String` - proposed email, `phoneNumber:String` - proposed phone number, `birthDate:LocalDate` - proposed birth date<br>**Throws:** IllegalArgumentException if conflict or invalid data found |
| 03 | validateStaffProfile | **Visibility:** public<br>**Return:** void<br>**Purpose:** Validate staff profile update for name conflicts<br>**Parameters:** `currentStaffId:Long` - ID of staff being updated, `username:String` - proposed staff name<br>**Throws:** IllegalArgumentException if name conflict found |

#### 3.7.2 BadWordFilter

Utility class for filtering inappropriate content from user input.

**Attributes:**

| No | Name | Description |
|----|------|-------------|
| 01 | badWords | **Visibility:** private (final)<br>**Type:** Set&lt;String&gt;<br>**Purpose:** Set of inappropriate words loaded from configuration file |

**Methods:**

| No | Name | Description |
|----|------|-------------|
| 01 | init | **Visibility:** public<br>**Return:** void<br>**Purpose:** Initialize filter by loading bad words from badwords.txt file<br>**Annotation:** @PostConstruct (runs after bean construction)<br>**Parameters:** None |
| 02 | containsBadWord | **Visibility:** public<br>**Return:** boolean<br>**Purpose:** Check if given text contains any inappropriate words<br>**Parameters:** `text:String` - text to check |
| 03 | findBadWords | **Visibility:** public<br>**Return:** Set&lt;String&gt;<br>**Purpose:** Return set of all inappropriate words found in text<br>**Parameters:** `text:String` - text to analyze |
| 04 | loadBadWords | **Visibility:** private<br>**Return:** void<br>**Purpose:** Load bad words from classpath resource file<br>**Parameters:** None |

#### 3.7.3 EncryptSSN

Utility class for encrypting and decrypting Social Security Numbers.

**Methods:**

| No | Name | Description |
|----|------|-------------|
| 01 | encrypt | **Visibility:** public static<br>**Return:** String<br>**Purpose:** Encrypt SSN for secure storage<br>**Parameters:** `ssn:String` - plain text SSN<br>**Throws:** Exception if encryption fails |
| 02 | decrypt | **Visibility:** public static<br>**Return:** String<br>**Purpose:** Decrypt SSN for display or processing<br>**Parameters:** `encryptedSSN:String` - encrypted SSN<br>**Throws:** Exception if decryption fails |

---

## 4. Additional Entity Classes Summary

Due to the comprehensive nature of this system (66 entity classes total), here is a summary of additional key entities:

### Appointment Management Entities
- **AppointmentDetails**: Tracks actual start/end times and user assignment
- **AppointmentInvoice**: Billing information for completed appointments
- **AppointmentFeedback**: Customer satisfaction ratings and comments
- **RequestedService**: Services selected for each appointment

### Staff Management Entities
- **StaffAccount**: Login credentials for staff members
- **StaffPosition**: Job position definitions (stylist, receptionist, etc.)
- **StaffPayroll**: Monthly payroll calculations
- **StaffCommission**: Commission rates by position and type

### Inventory Entities
- **InventoryInvoice**: Purchase orders from suppliers
- **InventoryLot**: Current stock levels by batch
- **InventoryTransaction**: Stock movement tracking
- **InventoryConsignment**: Received shipments

### Order Management Entities
- **OrderInvoice**: Customer product orders
- **Cart**: Shopping cart items
- **OrderInvoiceDetails**: Line items in orders

### Loyalty & Promotions
- **Loyalty**: Customer loyalty accounts
- **LoyaltyLevel**: Tier definitions (Bronze, Silver, Gold, Platinum)
- **Promotion**: Active promotional campaigns

### Scheduling Entities
- **ShiftTemplate**: Reusable shift time templates
- **ShiftInstance**: Actual scheduled shifts
- **ShiftAssignment**: Staff assignments to shifts
- **ShiftAttendance**: Check-in/check-out records

---

## 5. Notes

1. All entity classes use Lombok annotations (@Data, @Builder, @NoArgsConstructor, @AllArgsConstructor) to automatically generate getters, setters, builders, and constructors.

2. Repository interfaces extend JpaRepository&lt;Entity, ID&gt; which provides standard CRUD operations automatically.

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
