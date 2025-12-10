// @ts-check
const { test, expect } = require('@playwright/test');

/**
 * Authentication Tests
 * Tests user login functionality using DataLoader credentials (alice/alice)
 */

test.describe('User Authentication', () => {
  test('should display login page correctly', async ({ page }) => {
    await page.goto('/user/login');
    
    // Check page title
    await expect(page).toHaveTitle(/User Login/);
    
    // Check login form elements
    await expect(page.locator('input[name="username"]')).toBeVisible();
    await expect(page.locator('input[name="password"]')).toBeVisible();
    await expect(page.locator('button[type="submit"]')).toBeVisible();
  });

  test('should successfully login with valid credentials from DataLoader', async ({ page }) => {
    await page.goto('/user/login');
    
    // Fill in credentials from DataLoader class
    // Username: alice, Password: alice
    await page.fill('input[name="username"]', 'alice');
    await page.fill('input[name="password"]', 'alice');
    
    // Submit login form
    await page.click('button[type="submit"]');
    
    // Wait for navigation after successful login
    await page.waitForURL(/\/(home|user\/profile|services)/, { timeout: 10000 });
    
    // Verify successful login by checking if we're redirected away from login page
    expect(page.url()).not.toContain('/user/login');
  });

  test('should show error with invalid credentials', async ({ page }) => {
    await page.goto('/user/login');
    
    // Try invalid credentials
    await page.fill('input[name="username"]', 'invaliduser');
    await page.fill('input[name="password"]', 'wrongpassword');
    
    await page.click('button[type="submit"]');
    
    // Should show error message or stay on login page
    // Wait a bit for potential error message
    await page.waitForTimeout(1000);
    
    // Verify we're still on login page or see error
    const currentUrl = page.url();
    expect(currentUrl.includes('/user/login') || currentUrl.includes('error')).toBeTruthy();
  });
});
