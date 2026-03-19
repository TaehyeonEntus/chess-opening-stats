import { test, expect } from '@playwright/test';

test('capture screenshot and check styles', async ({ page }) => {
  await page.goto('http://localhost:3001/en');
  
  // Find search button by text "Search"
  const searchButton = page.getByRole('button', { name: 'Search', exact: true });
  await searchButton.waitFor();
  
  const buttonStyles = await searchButton.evaluate((btn) => {
    const style = window.getComputedStyle(btn);
    return {
      backgroundColor: style.backgroundColor,
      color: style.color,
      border: style.border
    };
  });
  
  console.log('Search button styles:', buttonStyles);
  
  await page.screenshot({ path: 'public/screenshot.png' });
});
