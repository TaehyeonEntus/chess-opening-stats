export interface RateSummary {
  totalGames: number
  winRate: number
  drawRate: number
  lossRate: number
}

export function toPercent(value: number, total: number): number {
  if (total <= 0) {
    return 0
  }

  return Math.round((value / total) * 1000) / 10
}

export function calculateRatesFromCounts(
  wins: number,
  draws: number,
  losses: number
): RateSummary {
  const totalGames = wins + draws + losses

  return {
    totalGames,
    winRate: toPercent(wins, totalGames),
    drawRate: toPercent(draws, totalGames),
    lossRate: toPercent(losses, totalGames),
  }
}