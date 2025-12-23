import { test, expect } from '@playwright/test';
import * as path from 'path';

/**
 * End-to-end test for Service Image Upload functionality
 * 
 * Prerequisites:
 * - Backend server running on http://localhost:8080
 * - Database initialized with test data (DataLoader should run on startup)
 * 
 * Test Flow:
 * 1. Login as alice/alice
 * 2. Navigate to service create page
 * 3. Create a new service
 * 4. Verify redirect to edit page after creation
 * 5. Upload an image
 * 6. Verify image appears in gallery
 * 7. Delete the image
 * 8. Verify image is removed
 */

test.describe('Service Image Upload', () => {
  test.beforeEach(async ({ page }) => {
    // Login
    await page.goto('/login');
    await page.fill('input[name="username"]', 'alice');
    await page.fill('input[name="password"]', 'alice');
    await page.click('button[type="submit"]');
    
    // Wait for successful login
    await page.waitForURL(/\/(dashboard|home|manager)/, { timeout: 10000 });
  });

  test('should create service and upload image', async ({ page }) => {
    // Navigate to service create page
    await page.goto('/manager/service/create');
    
    // Fill in service details
    await page.fill('#serviceName', `Test Service ${Date.now()}`);
    
    // Select service category (assuming first option after placeholder)
    await page.selectOption('#serviceCategoryId', { index: 1 });
    
    // Select service type
    await page.selectOption('#serviceType', 'SINGLE');
    
    // Fill in price and duration
    await page.fill('#servicePrice', '100000');
    await page.fill('#durationMinutes', '60');
    await page.fill('#serviceDescription', 'This is a test service for image upload testing');
    await page.check('#activeStatus');
    
    // Submit form
    await page.click('button[type="submit"]');
    
    // Verify redirect to edit page with success message
    await page.waitForURL(/\/manager\/service\/edit\/\d+/, { timeout: 10000 });
    
    // Check for success message
    const successMessage = await page.textContent('.alert, .success, [class*="success"]');
    expect(successMessage).toContain('created successfully');
    expect(successMessage).toContain('add images');
    
    // Create a test image file
    const testImagePath = path.join(__dirname, '../test-assets/test-image.jpg');
    
    // Upload image
    const fileInput = await page.locator('#imageFile');
    await fileInput.setInputFiles(testImagePath);
    
    // Click upload button
    await page.click('#uploadBtn, button[onclick*="uploadServiceImage"]');
    
    // Wait for upload to complete
    await page.waitForSelector('#imageGallery .image-card', { timeout: 10000 });
    
    // Verify image appears in gallery
    const imageCards = await page.locator('#imageGallery .image-card').count();
    expect(imageCards).toBeGreaterThan(0);
    
    // Verify image is displayed
    const uploadedImage = await page.locator('#imageGallery .image-card img').first();
    await expect(uploadedImage).toBeVisible();
    
    // Test image deletion
    const deleteButton = await page.locator('#imageGallery .btn-delete-image').first();
    
    // Click delete and confirm
    page.on('dialog', dialog => dialog.accept());
    await deleteButton.click();
    
    // Wait for image to be removed
    await page.waitForTimeout(1000);
    
    // Verify image is deleted
    const imageCardsAfterDelete = await page.locator('#imageGallery .image-card').count();
    expect(imageCardsAfterDelete).toBe(imageCards - 1);
  });

  test('should display images on service detail page', async ({ page }) => {
    // First, create a service with an image (reusing logic from previous test)
    await page.goto('/manager/service/create');
    
    const serviceName = `Test Service View ${Date.now()}`;
    await page.fill('#serviceName', serviceName);
    await page.selectOption('#serviceCategoryId', { index: 1 });
    await page.selectOption('#serviceType', 'SINGLE');
    await page.fill('#servicePrice', '80000');
    await page.fill('#durationMinutes', '45');
    await page.fill('#serviceDescription', 'Test service for viewing images');
    await page.check('#activeStatus');
    await page.click('button[type="submit"]');
    
    // Wait for redirect to edit page
    await page.waitForURL(/\/manager\/service\/edit\/(\d+)/, { timeout: 10000 });
    
    // Extract service ID from URL
    const url = page.url();
    const serviceId = url.match(/\/edit\/(\d+)/)?.[1];
    
    // Upload an image
    const testImagePath = path.join(__dirname, '../test-assets/test-image.jpg');
    const fileInput = await page.locator('#imageFile');
    await fileInput.setInputFiles(testImagePath);
    await page.click('#uploadBtn, button[onclick*="uploadServiceImage"]');
    
    // Wait for upload
    await page.waitForSelector('#imageGallery .image-card', { timeout: 10000 });
    
    // Navigate to service detail page (public view)
    await page.goto(`/services/${serviceId}`);
    
    // Verify image is displayed on detail page
    const serviceImage = await page.locator('.service-image-wrapper img, img[alt*="service"]').first();
    await expect(serviceImage).toBeVisible();
  });
});
