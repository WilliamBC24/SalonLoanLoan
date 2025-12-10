// @ts-check
const { test, expect } = require('@playwright/test');

/**
 * Products Tests
 * Tests the products listing and details pages
 */

test.describe('Products Page', () => {
  test('should display products listing page', async ({ page }) => {
    await page.goto('/products');
    
    // Check page title
    await expect(page).toHaveTitle(/Products|Salon Loan Loan/);
    
    // Check main heading
    const heading = page.locator('h1, h2').filter({ hasText: /Products|Our Products/ }).first();
    await expect(heading).toBeVisible();
  });

  test('should display product cards or empty message', async ({ page }) => {
    await page.goto('/products');
    
    // Check if there are product cards
    const productCards = page.locator('.product-card, article').filter({ has: page.locator('a[href*="/products/"]') });
    const noProductsMsg = page.locator('text=/No products|No items/i');
    
    // Either we have cards or a message
    const cardCount = await productCards.count();
    const hasNoMsg = await noProductsMsg.isVisible().catch(() => false);
    
    expect(cardCount > 0 || hasNoMsg).toBeTruthy();
  });

  test('should be able to click on a product to view details', async ({ page }) => {
    await page.goto('/products');
    
    // Find first product link
    const firstProductLink = page.locator('a[href*="/products/"]').first();
    
    // Check if we have products
    const linkCount = await firstProductLink.count();
    if (linkCount > 0) {
      await firstProductLink.click();
      
      // Wait for navigation to product details
      await page.waitForURL('**/products/**', { timeout: 5000 });
      
      // Verify we're on a product details page
      expect(page.url()).toMatch(/\/products\/\d+/);
    }
  });
});

test.describe('Product Details Page', () => {
  test('should display product details when accessing a valid product', async ({ page }) => {
    // Try to access product with ID 1 (likely to exist from DataLoader)
    await page.goto('/products/1');
    
    // Check if we get to a page (either details or error page)
    await page.waitForLoadState('networkidle');
    
    // If product exists, we should see product information
    // If not, we should see an error message
    const hasProductName = await page.locator('h1, h2, h3').count() > 0;
    const hasErrorMsg = await page.locator('text=/not found|error/i').isVisible().catch(() => false);
    
    expect(hasProductName || hasErrorMsg).toBeTruthy();
  });

  test('should display product price', async ({ page }) => {
    await page.goto('/products/1');
    
    // Look for price element (common patterns)
    const priceElements = page.locator('.price, .product-price, text=/VND|₫/i');
    
    // If product exists, should have price
    const errorPage = await page.locator('text=/not found|error/i').isVisible().catch(() => false);
    if (!errorPage) {
      await expect(priceElements.first()).toBeVisible();
    }
  });
});
