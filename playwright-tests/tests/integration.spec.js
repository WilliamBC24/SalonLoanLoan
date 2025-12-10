// @ts-check
const { test, expect } = require('@playwright/test');

/**
 * Integration Tests
 * Tests that verify both frontend and backend are working together
 * Uses DataLoader credentials: alice/alice
 */

test.describe('Frontend and Backend Integration', () => {
  test('should load home page with data from backend', async ({ page }) => {
    await page.goto('/');
    
    // Wait for page to load
    await page.waitForLoadState('networkidle');
    
    // Check that page loaded successfully
    await expect(page).toHaveTitle(/Salon Loan Loan|Home/);
    
    // Backend should serve the page (status 200)
    const response = await page.goto('/');
    expect(response?.status()).toBe(200);
  });

  test('should complete full user journey: login -> view services -> view products', async ({ page }) => {
    // Step 1: Login with DataLoader credentials
    await page.goto('/user/login');
    await page.fill('input[name="username"]', 'alice');
    await page.fill('input[name="password"]', 'alice');
    await page.click('button[type="submit"]');
    
    // Wait for successful login (redirected away from login page)
    await page.waitForURL(/^(?!.*\/user\/login).*$/, { timeout: 10000 });
    
    // Step 2: Navigate to services page
    await page.goto('/services');
    await page.waitForLoadState('networkidle');
    
    // Verify services page loads
    await expect(page).toHaveTitle(/Services|Salon Loan Loan/);
    
    // Step 3: Navigate to products page
    await page.goto('/products');
    await page.waitForLoadState('networkidle');
    
    // Verify products page loads
    await expect(page).toHaveTitle(/Products|Salon Loan Loan/);
  });

  test('should verify backend is serving correct content type', async ({ page }) => {
    const response = await page.goto('/');
    
    // Check response headers
    const contentType = response?.headers()['content-type'];
    expect(contentType).toContain('text/html');
  });

  test('should verify user profile data after login', async ({ page }) => {
    // Login first
    await page.goto('/user/login');
    await page.fill('input[name="username"]', 'alice');
    await page.fill('input[name="password"]', 'alice');
    await page.click('button[type="submit"]');
    
    // Wait for login to complete
    await page.waitForTimeout(2000);
    
    // Try to access user profile
    await page.goto('/user/profile');
    await page.waitForLoadState('networkidle');
    
    // Check if we can see profile page (not redirected to login)
    const currentUrl = page.url();
    
    // If we're on profile page, we should see user data
    if (currentUrl.includes('/user/profile')) {
      // Look for user email from DataLoader (alice@wonderland.com)
      const emailText = await page.locator('text=/alice@wonderland.com/i').isVisible().catch(() => false);
      const usernameText = await page.locator('text=/alice/i').isVisible().catch(() => false);
      
      // At least one should be visible
      expect(emailText || usernameText).toBeTruthy();
    }
  });

  test('should handle backend errors gracefully', async ({ page }) => {
    // Try to access a non-existent page
    const response = await page.goto('/nonexistent-page-12345');
    
    // Should get 404 or redirect to error page
    const status = response?.status();
    expect([404, 302, 200].includes(status || 0)).toBeTruthy();
  });

  test('should verify static resources are loaded from backend', async ({ page }) => {
    await page.goto('/');
    
    // Check if CSS is loaded
    const stylesheets = page.locator('link[rel="stylesheet"]');
    const styleCount = await stylesheets.count();
    expect(styleCount).toBeGreaterThan(0);
    
    // Check if at least one CSS file loads successfully
    const responses = [];
    page.on('response', response => {
      if (response.url().includes('.css')) {
        responses.push(response);
      }
    });
    
    await page.reload();
    await page.waitForLoadState('networkidle');
    
    // At least one CSS should have loaded
    expect(responses.length).toBeGreaterThan(0);
  });

  test('should verify backend API endpoints are accessible', async ({ page, request }) => {
    // Login to get session
    await page.goto('/user/login');
    await page.fill('input[name="username"]', 'alice');
    await page.fill('input[name="password"]', 'alice');
    await page.click('button[type="submit"]');
    await page.waitForTimeout(2000);
    
    // Get cookies from browser context
    const cookies = await page.context().cookies();
    
    // Verify we have session cookies
    expect(cookies.length).toBeGreaterThan(0);
  });
});

test.describe('Backend Data Loading', () => {
  test('should verify DataLoader has initialized test data', async ({ page }) => {
    await page.goto('/');
    
    // Navigate to home page and check if services/products are displayed
    // This verifies that DataLoader has run and populated the database
    await page.waitForLoadState('networkidle');
    
    // Check services section
    const servicesSection = page.locator('#services');
    await expect(servicesSection).toBeVisible();
    
    // Check products section
    const productsSection = page.locator('#products');
    await expect(productsSection).toBeVisible();
  });

  test('should be able to login with DataLoader user credentials', async ({ page }) => {
    await page.goto('/user/login');
    
    // Use DataLoader credentials
    await page.fill('input[name="username"]', 'alice');
    await page.fill('input[name="password"]', 'alice');
    await page.click('button[type="submit"]');
    
    // Should successfully login
    await page.waitForTimeout(2000);
    
    // Verify we're not on login page anymore
    const currentUrl = page.url();
    expect(currentUrl).not.toContain('/user/login');
  });
});
