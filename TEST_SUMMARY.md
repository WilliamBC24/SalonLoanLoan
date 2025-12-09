# Unit Test Summary

This document provides a comprehensive overview of the unit tests added to the SalonLoanLoan project.

## Overview

A total of **45 unit tests** have been created across **5 test classes**, covering notable methods and functions in the codebase. All tests use mocked dependencies and can run without requiring a database connection.

## Test Classes and Coverage

### 1. ValidationUtilsTest (9 tests)
**Location:** `backend/SLLBackend/src/test/java/service/sllbackend/utils/ValidationUtilsTest.java`

Tests the `ValidationUtils` class which handles user and staff profile validation.

**Tests:**
- ✅ `testValidateNewUser_WithDuplicateUsername_ShouldThrowException`
- ✅ `testValidateNewUser_WithDuplicateEmail_ShouldThrowException`
- ✅ `testValidateNewUser_WithDuplicatePhoneNumber_ShouldThrowException`
- ✅ `testValidateNewUser_WithUniqueCredentials_ShouldNotThrowException`
- ✅ `testValidateUserProfile_WithInvalidAge_ShouldThrowException`
- ✅ `testValidateUserProfile_WithAgeOver150_ShouldThrowException`
- ✅ `testValidateUserProfile_WithConflictingUsername_ShouldThrowException`
- ✅ `testValidateUserProfile_WithValidData_ShouldNotThrowException`
- ✅ `testValidateStaffProfile_WithDuplicateName_ShouldThrowException`

**Coverage:**
- Username, email, and phone number duplicate detection
- Birth date validation (age must be 0-150)
- Staff name conflict detection
- Profile update validation

### 2. EncryptSSNTest (6 tests)
**Location:** `backend/SLLBackend/src/test/java/service/sllbackend/utils/EncryptSSNTest.java`

Tests the `EncryptSSN` class which handles Social Security Number encryption and decryption using AES/GCM.

**Tests:**
- ✅ `testEncrypt_ShouldReturnNonNullEncryptedString`
- ✅ `testEncryptDecrypt_ShouldReturnOriginalValue`
- ✅ `testEncrypt_SameValueTwice_ShouldProduceDifferentEncryption`
- ✅ `testEncryptDecrypt_WithEmptyString_ShouldWork`
- ✅ `testEncryptDecrypt_WithSpecialCharacters_ShouldWork`
- ✅ `testDecrypt_WithInvalidData_ShouldThrowException`

**Coverage:**
- Encryption produces non-null output
- Decrypt(Encrypt(x)) = x (round-trip)
- Different ciphertext for same plaintext (random IV)
- Edge cases (empty string, special characters)
- Error handling for invalid encrypted data

### 3. TimeInRageValidatorTest (7 tests)
**Location:** `backend/SLLBackend/src/test/java/service/sllbackend/utils/TimeInRageValidatorTest.java`

Tests the `TimeInRageValidator` class which validates that times fall within a specified range.

**Tests:**
- ✅ `testIsValid_WithTimeInRange_ShouldReturnTrue`
- ✅ `testIsValid_WithTimeAtStartBoundary_ShouldReturnTrue`
- ✅ `testIsValid_WithTimeAtEndBoundary_ShouldReturnTrue`
- ✅ `testIsValid_WithTimeBeforeRange_ShouldReturnFalse`
- ✅ `testIsValid_WithTimeAfterRange_ShouldReturnFalse`
- ✅ `testIsValid_WithNullTime_ShouldReturnFalse`
- ✅ `testIsValid_WithMidnightRange_ShouldWork`

**Coverage:**
- Time within range validation
- Boundary conditions (start and end times)
- Times outside range
- Null handling
- Edge cases (midnight range)

### 4. VoucherServiceTest (12 tests)
**Location:** `backend/SLLBackend/src/test/java/service/sllbackend/service/VoucherServiceTest.java`

Tests the `VoucherServiceImpl` class which handles voucher management operations.

**Tests:**
- ✅ `testGetVouchers_WithNoFilters_ShouldReturnAllVouchers`
- ✅ `testGetVouchers_WithCodeFilter_ShouldReturnFilteredVouchers`
- ✅ `testGetVouchers_WithNameFilter_ShouldReturnFilteredVouchers`
- ✅ `testGetVouchers_WithDiscountTypeFilter_ShouldReturnFilteredVouchers`
- ✅ `testGetVouchers_WithStatusFilter_ShouldReturnFilteredVouchers`
- ✅ `testGetVouchers_WithMultipleFilters_ShouldReturnFilteredVouchers`
- ✅ `testGetVoucherById_WithValidId_ShouldReturnVoucher`
- ✅ `testGetVoucherById_WithInvalidId_ShouldReturnNull`
- ✅ `testCreateVoucher_ShouldSaveAndReturnVoucher`
- ✅ `testUpdateVoucher_WithValidId_ShouldUpdateAndReturnVoucher`
- ✅ `testUpdateVoucher_WithInvalidId_ShouldThrowException`
- ✅ `testGetAllVoucherStatuses_ShouldReturnAllStatuses`

**Coverage:**
- Voucher listing with various filters (code, name, discount type, status)
- Multiple filter combinations
- Voucher retrieval by ID
- Voucher creation
- Voucher updates
- Error handling for non-existent vouchers
- Voucher status retrieval

### 5. OrderServiceTest (11 tests)
**Location:** `backend/SLLBackend/src/test/java/service/sllbackend/service/OrderServiceTest.java`

Tests the `OrderServiceImpl` class which handles order placement and management.

**Tests:**
- ✅ `testPlaceOrder_WithEmptyCart_ShouldThrowException`
- ✅ `testPlaceOrder_WithInsufficientStock_ShouldThrowException`
- ✅ `testPlaceOrder_DeliveryWithoutAddress_ShouldThrowException`
- ✅ `testPlaceOrder_DeliveryWithoutCity_ShouldThrowException`
- ✅ `testPlaceOrder_InStorePickupWithInvalidPayment_ShouldThrowException`
- ✅ `testCancelOrder_WithUnauthorizedUser_ShouldThrowException`
- ✅ `testCancelOrder_WithInvalidStatus_ShouldThrowException`
- ✅ `testCancelOrder_WithValidRequest_ShouldSucceed`
- ✅ `testGetOrderDetails_WithValidId_ShouldReturnOrder`
- ✅ `testUpdateOrderStatus_ShouldUpdateStatus`
- ✅ `testCountByUser_ShouldReturnCount`

**Coverage:**
- Order placement validation (cart, stock, address)
- Payment method validation for different fulfillment types
- Order cancellation (authorization and status checks)
- Order retrieval and status updates
- Order counting by user

## Running the Tests

### Run All Tests
```bash
cd backend/SLLBackend
./gradlew test
```

### Run Specific Test Class
```bash
./gradlew test --tests "service.sllbackend.utils.ValidationUtilsTest"
./gradlew test --tests "service.sllbackend.utils.EncryptSSNTest"
./gradlew test --tests "service.sllbackend.utils.TimeInRageValidatorTest"
./gradlew test --tests "service.sllbackend.service.VoucherServiceTest"
./gradlew test --tests "service.sllbackend.service.OrderServiceTest"
```

### Run All New Tests
```bash
./gradlew test --tests "service.sllbackend.utils.*Test" --tests "service.sllbackend.service.VoucherServiceTest" --tests "service.sllbackend.service.OrderServiceTest"
```

## Test Results

All 45 tests pass successfully:
- **ValidationUtilsTest**: 9/9 tests passing ✅
- **EncryptSSNTest**: 6/6 tests passing ✅
- **TimeInRageValidatorTest**: 7/7 tests passing ✅
- **VoucherServiceTest**: 12/12 tests passing ✅
- **OrderServiceTest**: 11/11 tests passing ✅

## Testing Framework

The tests use the following frameworks and tools:
- **JUnit 5**: Test framework
- **Mockito**: Mocking framework for dependencies
- **Spring Boot Test**: Integration with Spring Boot
- **AssertJ**: Fluent assertions (provided by Spring Boot Test)

## Test Credentials

The project includes a `DataLoader` class that loads test data on application startup. The following credentials are available for testing:

- **User Account:**
  - Username: `alice`
  - Password: `alice`
  - Email: `alice@wonderland.com`
  - Phone: `0999999999`

- **Staff Account:**
  - Name: `admin`
  - Role: `admin`

## Docker Testing

For integration testing with a real database, see the [DOCKER_TESTING.md](DOCKER_TESTING.md) guide which includes:
- Docker Compose setup for database and backend
- Instructions for building and running containers
- Database credentials configuration
- Troubleshooting tips

## Notes

- All unit tests use mocked dependencies and don't require a database
- The existing `SllBackendApplicationTests` is an integration test that requires a database connection
- Tests follow the Arrange-Act-Assert pattern
- Test names clearly describe what they test and expected behavior
- Each test is independent and can run in isolation
