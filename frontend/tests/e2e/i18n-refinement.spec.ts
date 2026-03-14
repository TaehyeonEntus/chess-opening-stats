import { test, expect } from '@playwright/test';

test.describe('I18n and UI Refinement', () => {
  test('should show translated title and language toggle should persist path', async ({ page }) => {
    // Navigate to Korean home page
    await page.goto('/ko');
    
    // Check main title in Korean
    await expect(page.locator('h1')).toHaveText('체스 오프닝 통계');
    
    // Check explore title
    await expect(page.locator('h2').first()).toHaveText('체스 오프닝 분석하기');

    // Change language to English
    await page.getByRole('button', { name: /change language/i }).click();
    await page.getByRole('menuitem', { name: /english/i }).click();

    // Should stay on the same path (English home)
    await expect(page).toHaveURL(/\/en/);
    await expect(page.locator('h1')).toHaveText('Chess Opening Stats');
  });

  test('should have localized browser title', async ({ page }) => {
    await page.goto('/ko');
    await expect(page).toHaveTitle('체스 오프닝 통계');

    await page.goto('/en');
    await expect(page).toHaveTitle('Chess Opening Stats');
  });
});
