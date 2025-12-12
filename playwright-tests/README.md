# Playwright End-to-End Tests

This directory contains Playwright tests for the Salon Loan Loan application, testing both frontend and backend integration.

## Overview

The test suite includes:
- **Authentication Tests** (`auth.spec.js`): User login with DataLoader credentials
- **Home Page Tests** (`home.spec.js`): Hero section, services and products display
- **Services Tests** (`services.spec.js`): Services listing and details pages
- **Products Tests** (`products.spec.js`): Products listing and details pages

## Prerequisites

1. **Backend Application**: The Spring Boot backend must be running on `http://localhost:8080`
2. **Database**: PostgreSQL database must be running with test data loaded by DataLoader
3. **Node.js**: Version 14 or higher
4. **npm**: Comes with Node.js

## Setup

### 1. Install Dependencies

```bash
cd playwright-tests
npm install
```

### 2. Install Playwright Browsers

```bash
npx playwright install chromium
```

## Running the Application

Before running tests, start the application:

### Option 1: Using Docker Compose (Recommended)

```bash
# From the project root
cp .env.example .env
docker-compose up --build
```

Wait for the application to fully start (you should see "Started SllBackendApplication" in the logs).

### Option 2: Manual Setup

1. Start PostgreSQL database:
```bash
cd database
docker build -t sll-db .
docker run -d --network host --env-file ../.env --name sll-database sll-db
```

2. Start the backend:
```bash
cd backend/SLLBackend
./gradlew bootRun
```

## Running Tests

### Run All Tests

```bash
npm test
```

### Run Tests with UI Mode (Interactive)

```bash
npm run test:ui
```

### Run Tests in Headed Mode (See Browser)

```bash
npm run test:headed
```

### Run Tests in Debug Mode

```bash
npm run test:debug
```

### Run Specific Test File

```bash
npx playwright test tests/auth.spec.js
npx playwright test tests/home.spec.js
npx playwright test tests/services.spec.js
npx playwright test tests/products.spec.js
```

### Run Tests with Specific Browser

```bash
npx playwright test --project=chromium
```

## Test Credentials

The tests use credentials from the `DataLoader` class:

- **Username**: `alice`
- **Password**: `alice`
- **Email**: `alice@wonderland.com`
- **Phone**: `0999999999`

These credentials are automatically loaded when the application starts.

## Test Structure

### Authentication Tests (`auth.spec.js`)
- ✅ Display login page correctly
- ✅ Login with valid credentials (alice/alice)
- ✅ Handle invalid credentials

### Home Page Tests (`home.spec.js`)
- ✅ Display hero section
- ✅ Display services section
- ✅ Display products section
- ✅ Navigate to services page

### Services Tests (`services.spec.js`)
- ✅ Display services listing
- ✅ Display service cards or empty message
- ✅ Click service to view details
- ✅ Display service details page

### Products Tests (`products.spec.js`)
- ✅ Display products listing
- ✅ Display product cards or empty message
- ✅ Click product to view details
- ✅ Display product details with price

## Viewing Test Results

After running tests, view the HTML report:

```bash
npx playwright show-report
```

This will open a browser with detailed test results, including:
- Test execution timeline
- Screenshots on failure
- Video recordings on failure
- Network activity logs

## Configuration

The test configuration is in `playwright.config.js`:

- **Base URL**: `http://localhost:8080`
- **Browser**: Chromium (Chrome)
- **Screenshots**: Captured on failure
- **Videos**: Recorded on failure
- **Traces**: Captured on first retry

## Troubleshooting

### Application Not Running

**Error**: `Error: connect ECONNREFUSED ::1:8080`

**Solution**: Make sure the Spring Boot application is running on port 8080.

```bash
# Check if application is running
curl http://localhost:8080

# Or check docker containers
docker ps
```

### Database Not Initialized

**Error**: Tests fail because no data is displayed

**Solution**: Ensure the DataLoader has run. Check application logs:

```bash
docker logs sll-backend | grep "DataLoader"
```

You should see logs like:
```
Loading initial data
User registered: alice with password: alice
Staff registered: admin with role: admin
```

### Port Already in Use

**Error**: Port 8080 already in use

**Solution**: Stop the existing process or use a different port:

```bash
# Find process using port 8080
lsof -ti:8080 | xargs kill -9

# Or change port in application.yml and playwright.config.js
```

### Browser Not Installed

**Error**: `browserType.launch: Executable doesn't exist`

**Solution**: Install Playwright browsers:

```bash
npx playwright install chromium
```

## CI/CD Integration

To run tests in CI/CD:

```bash
# Install dependencies
npm ci

# Install browsers
npx playwright install --with-deps chromium

# Run tests
npm test
```

## Writing New Tests

To add new tests:

1. Create a new file in `tests/` directory: `tests/my-feature.spec.js`
2. Use the test template:

```javascript
const { test, expect } = require('@playwright/test');

test.describe('My Feature', () => {
  test('should do something', async ({ page }) => {
    await page.goto('/my-page');
    await expect(page.locator('h1')).toBeVisible();
  });
});
```

3. Run your test:

```bash
npx playwright test tests/my-feature.spec.js
```

## Best Practices

1. **Use DataLoader credentials**: Always use the alice/alice credentials for authentication tests
2. **Wait for elements**: Use `await expect().toBeVisible()` instead of `waitForTimeout`
3. **Handle dynamic content**: Check for both success and empty states
4. **Clean up**: Tests should not depend on each other
5. **Screenshot failures**: Failures automatically capture screenshots for debugging

## Resources

- [Playwright Documentation](https://playwright.dev)
- [Playwright Best Practices](https://playwright.dev/docs/best-practices)
- [Playwright API Reference](https://playwright.dev/docs/api/class-playwright)
