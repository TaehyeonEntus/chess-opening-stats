import { test, expect } from '@playwright/test';

test.describe('Authentication & Sync Flow', () => {
  // Prerequisite: Ensure backend is running at localhost:8080 
  // and frontend at localhost:3001 (as configured in playwright.config.ts)

  test('should redirect to login page when accessing protected route', async ({ page }) => {
    // Attempt to access mypage without login
    await page.goto('/mypage');
    
    // Should be redirected to login
    // Matches /login or /ko/login or /en/login
    await expect(page).toHaveURL(/login/);
  });

  test('should display login form elements', async ({ page }) => {
    await page.goto('/login');
    
    await expect(page.getByLabel(/Username|아이디/i)).toBeVisible();
    await expect(page.getByLabel(/Password|비밀번호/i)).toBeVisible();
    await expect(page.getByRole('button', { name: /Login|로그인/i })).toBeVisible();
  });

  // Note: Actual login test requires a running backend with a valid user.
  // The following test is a template for verifying the full flow.
  /*
  test('full login and sync flow', async ({ page }) => {
    // 1. Login
    await page.goto('/login');
    await page.fill('input[name="username"]', 'testuser');
    await page.fill('input[name="password"]', 'password123!');
    await page.click('button[type="submit"]');
    
    await expect(page).toHaveURL('/');

    // 2. Go to My Page
    await page.goto('/mypage');
    
    // 3. Trigger Sync
    const syncButton = page.getByRole('button', { name: /Sync Now|지금 갱신하기/i });
    await syncButton.click();

    // 4. Verify Success Toast
    await expect(page.getByText(/Synced|동기화했습니다/i)).toBeVisible();
  });
  */
});
