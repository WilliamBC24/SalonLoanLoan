import { test, expect } from '@playwright/test';
import * as path from 'path';

/**
 * End-to-end test for Product Image Upload functionality
 * 
 * Prerequisites:
 * - Backend server running on http://localhost:8080
 * - Database initialized with test data (DataLoader should run on startup)
 * 
 * Test Flow:
 * 1. Login as alice/alice
 * 2. Navigate to product create page
 * 3. Create a new product
 * 4. Verify redirect to edit page after creation
 * 5. Upload an image
 * 6. Verify image appears in gallery
 * 7. Delete the image
 * 8. Verify image is removed
 */

test.describe('Product Image Upload', () => {
  test.beforeEach(async ({ page }) => {
    // Login
    await page.goto('/login');
    await page.fill('input[name="username"]', 'alice');
    await page.fill('input[name="password"]', 'alice');
    await page.click('button[type="submit"]');
    
    // Wait for successful login - adjust selector based on actual app
    await page.waitForURL(/\/(dashboard|home|manager)/, { timeout: 10000 });
  });

  test('should create product and upload image', async ({ page }) => {
    // Navigate to product create page
    await page.goto('/manager/products/create');
    
    // Fill in product details
    await page.fill('#productName', `Test Product ${Date.now()}`);
    await page.fill('#currentPrice', '50000');
    await page.fill('#productDescription', 'This is a test product for image upload testing');
    await page.check('#activeStatus');
    
    // Submit form
    await page.click('button[type="submit"]');
    
    // Verify redirect to edit page with success message
    await page.waitForURL(/\/manager\/products\/edit\/\d+/, { timeout: 10000 });
    
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
    await page.click('#uploadBtn');
    
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
    
    // Verify image is deleted (count should decrease)
    const imageCardsAfterDelete = await page.locator('#imageGallery .image-card').count();
    expect(imageCardsAfterDelete).toBe(imageCards - 1);
  });

  test('should handle invalid image upload', async ({ page }) => {
    // Navigate to product create page
    await page.goto('/manager/products/create');
    
    // Fill in product details
    await page.fill('#productName', `Test Product Invalid ${Date.now()}`);
    await page.fill('#currentPrice', '30000');
    await page.fill('#productDescription', 'Test product for invalid image upload');
    
    // Submit form
    await page.click('button[type="submit"]');
    
    // Wait for redirect to edit page
    await page.waitForURL(/\/manager\/products\/edit\/\d+/, { timeout: 10000 });
    
    // Try to upload a non-image file (e.g., text file)
    const testFilePath = path.join(__dirname, '../test-assets/test-file.txt');
    
    const fileInput = await page.locator('#imageFile');
    await fileInput.setInputFiles(testFilePath);
    
    // Click upload button
    await page.click('#uploadBtn');
    
    // Wait for error message
    await page.waitForTimeout(2000);
    
    // Verify error message appears
    const errorMessage = await page.textContent('#uploadMessage, .error, [class*="error"]');
    expect(errorMessage).toBeTruthy();
  });
});
