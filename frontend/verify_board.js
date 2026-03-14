const { chromium } = require('playwright');

(async () => {
  const browser = await chromium.launch();
  const page = await browser.newPage();
  try {
    console.log('Navigating to app...');
    await page.goto('http://localhost:3001/ko');
    
    console.log('Searching for hikaru...');
    await page.getByPlaceholder('아이디').fill('hikaru');
    await page.click('button:has-text("검색")');
    
    console.log('Waiting for confirmation...');
    await page.waitForSelector('text=네, 맞습니다', { timeout: 10000 });
    await page.click('button:has-text("네, 맞습니다")');
    
    console.log('Waiting for dashboard...');
    await page.waitForSelector('button[aria-label="체스보드 검색"]', { timeout: 20000 });
    
    console.log('Opening board search...');
    await page.click('button[aria-label="체스보드 검색"]');
    await page.waitForSelector('text=보드로 검색', { timeout: 10000 });
    
    await page.screenshot({ path: 'c:/TaehyeonEntus_GIT/chess-opening-stats/frontend/debug_board_open.png' });
    console.log('Screenshot 1 taken.');

    // We can't easily drag pieces with just select/click, but we can check the layout
    const layout = await page.evaluate(() => {
      const el = document.querySelector('.flex.flex-row.gap-4.flex-1');
      if (!el) return 'Not found';
      const rect = el.getBoundingClientRect();
      const children = Array.from(el.children).map(c => c.getBoundingClientRect());
      if (children.length < 2) return 'Not enough children';
      const isHorizontal = Math.abs(children[0].top - children[1].top) < 5;
      return isHorizontal ? 'Horizontal' : 'Vertical';
    });
    console.log('Layout detected:', layout);

    // Check if Undo is clickable
    const undoBtn = await page.$('button:has-text("되돌리기")');
    console.log('Undo button exists:', !!undoBtn);
    
    // Final screenshot
    await page.screenshot({ path: 'c:/TaehyeonEntus_GIT/chess-opening-stats/frontend/debug_board_final.png' });
    console.log('Final screenshot taken.');

  } catch (err) {
    console.error('Error during verification:', err);
    await page.screenshot({ path: 'c:/TaehyeonEntus_GIT/chess-opening-stats/frontend/debug_error.png' });
  } finally {
    await browser.close();
  }
})();
