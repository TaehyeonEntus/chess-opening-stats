import { test, expect } from '@playwright/test';

test.describe('Mobile Responsiveness', () => {
  test.use({ viewport: { width: 375, height: 667 } }); // iPhone SE size

  test('should show mobile friendly layout on home page', async ({ page }) => {
    await page.goto('/');
    
    // Check if search inputs stack vertically on mobile
    const searchContainer = page.locator('section[aria-labelledby="explore-title"] > .flex-col');
    await expect(searchContainer).toBeVisible();
    await expect(searchContainer).toHaveClass(/flex-col/);
    
    // Check if header is responsive
    const header = page.locator('header');
    await expect(header).toBeVisible();
    
    // Check search button hit target (min 44px)
    const searchButton = page.getByRole('button', { name: /search/i });
    const box = await searchButton.boundingBox();
    if (box) {
      expect(box.height).toBeGreaterThanOrEqual(44);
      expect(box.width).toBeGreaterThanOrEqual(44);
    }
  });

  test('should have accessible skip link', async ({ page }) => {
    await page.goto('/');
    await page.keyboard.press('Tab');
    const skipLink = page.locator('a:has-text("Skip to content")');
    await expect(skipLink).toBeFocused();
    await expect(skipLink).toBeVisible();
  });
});
