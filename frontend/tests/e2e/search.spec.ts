import { test, expect } from '@playwright/test';

test('search for hikaru and verify response', async ({ page }) => {
  // We'll intercept the API call to see what happens
  await page.route('**/api/player/profile*', async (route) => {
    const response = await route.fetch();
    const json = await response.json();
    console.log('API Response for player:', JSON.stringify(json, null, 2));
    await route.fulfill({ response, json });
  });

  // Intercept dashboard call
  let dashboardCalls = 0;
  await page.route('**/api/dashboard*', async (route) => {
    dashboardCalls++;
    console.log(`Dashboard call #${dashboardCalls}`);
    
    if (dashboardCalls === 1) {
      // Simulate "not ready" state with empty body (which might be the issue)
      console.log('Fulfilling with empty body');
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: ''
      });
    } else {
      // Simulate "ready" state with actual data
      console.log('Fulfilling with data');
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          data: {
            white: { 
              record: { win: 10, draw: 2, lose: 5 }, 
              openings: [{ openingId: 1, win: 5, draw: 1, lose: 2 }] 
            },
            black: { 
              record: { win: 8, draw: 3, lose: 4 }, 
              openings: [{ openingId: 2, win: 4, draw: 1, lose: 2 }] 
            }
          }
        })
      });
    }
  });

  console.log('Navigating to home page...');
  await page.goto('/', { timeout: 60000 });
  await page.getByPlaceholder('Username').fill('hikaru');
  await page.getByRole('button', { name: 'Search' }).click();

  // Confirmation card should appear
  await page.getByRole('button', { name: "Yes, That's Me" }).click();

  // Dashboard should eventually appear
  // Wait for the specific username display in the dashboard
  console.log('Waiting for dashboard...');
  
  // Let's log the visibility of different stages
  const dashboardHeading = page.locator('h2').filter({ hasText: 'hikaru' });
  const syncingState = page.locator('h2', { hasText: 'Synchronizing Games' });
  
  await page.waitForTimeout(5000); // Give it some time
  
  console.log('Syncing state visible:', await syncingState.isVisible());
  console.log('Dashboard heading visible:', await dashboardHeading.isVisible());
  
  const isDataReady = await page.evaluate(() => {
    // This is a bit hacky, but let's see if we can find clues in the DOM
    return !!document.querySelector('.grid'); // Check if a grid exists
  });
  console.log('Grid exists in DOM:', isDataReady);

  await expect(dashboardHeading).toBeVisible({ timeout: 20000 });
  
  console.log(`Final dashboard calls: ${dashboardCalls}`);
});
