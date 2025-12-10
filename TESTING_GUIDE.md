# Testing Guide - Playwright End-to-End Tests

This guide shows how to test both the frontend and backend of the Salon Loan Loan application using Playwright.

## Quick Start

### 1. Start the Application with Docker

```bash
# Copy environment variables
cp .env.example .env

# Start database and backend
docker-compose up --build
```

Wait until you see `Started SllBackendApplication` in the logs.

### 2. Install and Run Playwright Tests

```bash
# Navigate to test directory
cd playwright-tests

# Install dependencies
npm install

# Install Playwright browser
npx playwright install chromium

# Run tests
npm test
```

## What Gets Tested

### Frontend Pages
- ✅ **Home Page** (`/`): Hero section, services, and products display
- ✅ **User Login** (`/user/login`): Login form with DataLoader credentials (alice/alice)
- ✅ **Services** (`/services`): Service listing and details
- ✅ **Products** (`/products`): Product listing and details

### Backend Integration
- ✅ Backend serves HTML pages correctly
- ✅ DataLoader populates test data (services, products, user accounts)
- ✅ User authentication works with test credentials
- ✅ User session management
- ✅ Static resources (CSS) are served
- ✅ API endpoints are accessible

### Test Credentials (from DataLoader)
- **Username**: `alice`
- **Password**: `alice`
- **Email**: `alice@wonderland.com`
- **Phone**: `0999999999`

## Test Suite Overview

| Test File | Tests | Description |
|-----------|-------|-------------|
| `auth.spec.js` | 3 | User login and authentication |
| `home.spec.js` | 4 | Home page sections and navigation |
| `services.spec.js` | 4 | Services listing and details |
| `products.spec.js` | 5 | Products listing and details |
| `integration.spec.js` | 8 | Full frontend-backend integration |
| **Total** | **24** | **Complete E2E coverage** |

## Running Tests

### All Tests
```bash
npm test
```

### Interactive Mode (UI)
```bash
npm run test:ui
```

### Watch Browser (Headed Mode)
```bash
npm run test:headed
```

### Debug Mode
```bash
npm run test:debug
```

### Specific Test File
```bash
npx playwright test tests/auth.spec.js
npx playwright test tests/home.spec.js
npx playwright test tests/services.spec.js
npx playwright test tests/products.spec.js
npx playwright test tests/integration.spec.js
```

## View Test Results

After running tests:

```bash
npx playwright show-report
```

This opens an HTML report with:
- ✅ Test execution timeline
- 📸 Screenshots of failures
- 🎥 Video recordings of failures
- 🌐 Network activity logs

## Example Test Run

```bash
$ npm test

Running 24 tests using 1 worker

✓ auth.spec.js:9:3 › should display login page correctly (1.2s)
✓ auth.spec.js:18:3 › should successfully login with valid credentials (2.1s)
✓ auth.spec.js:37:3 › should show error with invalid credentials (1.5s)
✓ home.spec.js:9:3 › should display home page with hero section (0.8s)
✓ home.spec.js:23:3 › should display services section (1.0s)
✓ home.spec.js:43:3 › should display products section (0.9s)
✓ home.spec.js:63:3 › should have working navigation (1.2s)
... (17 more tests)

24 passed (25s)
```

## Troubleshooting

### Application Not Running
```bash
# Check if backend is up
curl http://localhost:8080

# Check docker containers
docker ps | grep sll
```

### Tests Failing
1. **Ensure application is fully started** - Wait for "Started SllBackendApplication"
2. **Check DataLoader ran** - Look for "User registered: alice" in logs
3. **Verify port 8080 is available** - No other service using it

### View Application Logs
```bash
# Docker logs
docker logs sll-backend

# Check for DataLoader output
docker logs sll-backend | grep "Loading initial data"
```

## CI/CD Integration

To run in CI/CD pipeline:

```bash
# Install dependencies
cd playwright-tests
npm ci

# Install browser with dependencies
npx playwright install --with-deps chromium

# Run tests
npm test
```

## Next Steps

- Add more test coverage for user workflows (registration, profile editing)
- Test staff login functionality
- Test appointment booking flow
- Test cart and checkout process
- Add visual regression testing

## Documentation

For detailed information, see:
- [playwright-tests/README.md](playwright-tests/README.md) - Comprehensive test documentation
- [Playwright Documentation](https://playwright.dev) - Official Playwright docs

## Support

If you encounter issues:
1. Check application logs: `docker logs sll-backend`
2. Verify database is running: `docker ps | grep sll-database`
3. Ensure DataLoader credentials are correct: alice/alice
4. Review test output: `npx playwright show-report`
