// @ts-check
const { test, expect } = require('@playwright/test');

/**
 * Home Page Tests
 * Tests the home page displays services and products correctly
 */

test.describe('Home Page', () => {
  test('should display home page with hero section', async ({ page }) => {
    await page.goto('/');
    
    // Check page title
    await expect(page).toHaveTitle(/Home/);
    
    // Check hero section
    await expect(page.locator('.hero')).toBeVisible();
    await expect(page.locator('h1')).toContainText('Welcome to');
    
    // Check "View Our Services" button
    const servicesButton = page.locator('a[href="/services"]').first();
    await expect(servicesButton).toBeVisible();
  });

  test('should display services section on home page', async ({ page }) => {
    await page.goto('/');
    
    // Check if services section exists
    const servicesSection = page.locator('#services');
    await expect(servicesSection).toBeVisible();
    
    // Check section heading
    await expect(servicesSection.locator('h2')).toContainText('Our Services');
    
    // Check if there are service cards or "no services" message
    const serviceCards = page.locator('.product-card');
    const noServicesMsg = page.locator('text=No services available');
    
    // At least one should be visible
    const hasServices = await serviceCards.count() > 0;
    const hasNoServicesMsg = await noServicesMsg.isVisible().catch(() => false);
    
    expect(hasServices || hasNoServicesMsg).toBeTruthy();
  });

  test('should display products section on home page', async ({ page }) => {
    await page.goto('/');
    
    // Check if products section exists
    const productsSection = page.locator('#products');
    await expect(productsSection).toBeVisible();
    
    // Check section heading
    await expect(productsSection.locator('h2')).toContainText('Our Products');
    
    // Check if there are product cards or "no products" message
    const productCards = page.locator('.product-card');
    const noProductsMsg = page.locator('text=No products available');
    
    // At least one should be visible
    const hasProducts = await productCards.count() > 0;
    const hasNoProductsMsg = await noProductsMsg.isVisible().catch(() => false);
    
    expect(hasProducts || hasNoProductsMsg).toBeTruthy();
  });

  test('should have working navigation to services page', async ({ page }) => {
    await page.goto('/');
    
    // Click on "View Our Services" button
    await page.click('a[href="/services"]');
    
    // Wait for navigation
    await page.waitForURL('**/services', { timeout: 5000 });
    
    // Verify we're on services page
    expect(page.url()).toContain('/services');
  });
});
