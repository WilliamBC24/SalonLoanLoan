# End-to-End Tests for Image Upload Functionality

This directory contains Playwright tests for the product and service image upload functionality in SalonLoanLoan.

## Prerequisites

1. **Backend Server**: The backend server must be running on `http://localhost:8080`
2. **Database**: PostgreSQL database must be running and initialized with test data
3. **Node.js**: Node.js 18+ must be installed

## Setup

```bash
cd e2e-tests
npm install
npx playwright install chromium
```

## Running Tests

### Run all tests (headless mode)
```bash
npm test
```

### Run tests in headed mode (see the browser)
```bash
npm run test:headed
```

### Debug tests (step through test execution)
```bash
npm run test:debug
```

### Run tests with UI mode
```bash
npm run test:ui
```

## Test Files

- **product-image-upload.spec.ts**: Tests for product image upload functionality
  - Creating a product and uploading images
  - Deleting uploaded images
  - Validating invalid file types

- **service-image-upload.spec.ts**: Tests for service image upload functionality
  - Creating a service and uploading images
  - Deleting uploaded images
  - Viewing images on service detail pages

## Test Data

The tests use the following credentials (from DataLoader):
- Username: `alice`
- Password: `alice`

## How It Works

The implementation follows a minimal-change approach:

1. When a product/service is created via the create form, the backend redirects to the edit page instead of the list page
2. The edit page already has full image upload functionality implemented
3. A success message indicates that images can now be added
4. Users can immediately upload images on the edit page

This approach reuses all existing image upload code and requires minimal modifications to achieve the goal.

## Known Issues

- **DataLoader Bug**: There is a pre-existing bug in the DataLoader with shift_template constraint violations that prevents the application from starting. This is unrelated to the image upload functionality.
  - Error: `new row for relation "shift_template" violates check constraint "shift_template_check"`
  - This needs to be fixed before the tests can run successfully

## Test Assets

- `test-assets/test-image.jpg`: A valid JPEG image used for upload testing
- `test-assets/test-file.txt`: A text file used to test invalid file type validation
