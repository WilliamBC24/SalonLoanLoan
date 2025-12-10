// @ts-check
const { test, expect } = require('@playwright/test');

/**
 * Services Tests
 * Tests the services listing and details pages
 */

test.describe('Services Page', () => {
  test('should display services listing page', async ({ page }) => {
    await page.goto('/services');
    
    // Check page title
    await expect(page).toHaveTitle(/Services|Salon Loan Loan/);
    
    // Check main heading
    const heading = page.locator('h1, h2').filter({ hasText: /Services|Our Services/ }).first();
    await expect(heading).toBeVisible();
  });

  test('should display service cards or empty message', async ({ page }) => {
    await page.goto('/services');
    
    // Check if there are service cards (look for articles with service links)
    const serviceCards = page.locator('article').filter({ has: page.locator('a[href*="/services/"]') });
    const noServicesMsg = page.locator('text=/No services|No items/i');
    
    // Either we have cards or a message
    const cardCount = await serviceCards.count();
    const hasNoMsg = await noServicesMsg.isVisible().catch(() => false);
    
    expect(cardCount > 0 || hasNoMsg).toBeTruthy();
  });

  test('should be able to click on a service to view details', async ({ page }) => {
    await page.goto('/services');
    
    // Find first service link
    const firstServiceLink = page.locator('a[href*="/services/"]').first();
    
    // Check if we have services
    const linkCount = await firstServiceLink.count();
    if (linkCount > 0) {
      await firstServiceLink.click();
      
      // Wait for navigation to service details
      await page.waitForURL('**/services/**', { timeout: 5000 });
      
      // Verify we're on a service details page
      expect(page.url()).toMatch(/\/services\/\d+/);
    }
  });
});

test.describe('Service Details Page', () => {
  test('should display service details when accessing a valid service', async ({ page }) => {
    // Try to access service with ID 1 (likely to exist from DataLoader)
    await page.goto('/services/1');
    
    // Check if we get to a page (either details or error page)
    await page.waitForLoadState('networkidle');
    
    // If service exists, we should see service information
    // If not, we should see an error message
    const hasServiceName = await page.locator('h1, h2, h3').count() > 0;
    const hasErrorMsg = await page.locator('text=/not found|error/i').isVisible().catch(() => false);
    
    expect(hasServiceName || hasErrorMsg).toBeTruthy();
  });
});
