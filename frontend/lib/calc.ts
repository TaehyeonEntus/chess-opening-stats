import type { Opening, OpeningResult, OpeningStatView, SummaryStats } from "./types"

export function calcRates(wins: number, draws: number, losses: number) {
  const totalGames = wins + draws + losses
  return {
    totalGames,
    winRate: totalGames > 0 ? Math.round((wins / totalGames) * 1000) / 10 : 0,
    drawRate: totalGames > 0 ? Math.round((draws / totalGames) * 1000) / 10 : 0,
    lossRate: totalGames > 0 ? Math.round((losses / totalGames) * 1000) / 10 : 0,
  }
}

export function mergeOpeningData(
  openings: Opening[],
  results: OpeningResult[],
  colorFilter: "all" | "white" | "black"
): OpeningStatView[] {
  // Map by EPD (unique identifier for position)
  const openingMap = new Map(openings.map((o) => [o.epd, o]))

  if (colorFilter === "all") {
    // Aggregate white + black results per EPD
    const aggregated = new Map<string, { wins: number; draws: number; losses: number }>()
    for (const r of results) {
      const existing = aggregated.get(r.epd) || { wins: 0, draws: 0, losses: 0 }
      existing.wins += r.wins
      existing.draws += r.draws
      existing.losses += r.losses
      aggregated.set(r.epd, existing)
    }

    return Array.from(aggregated.entries())
      .map(([epd, data]) => {
        const opening = openingMap.get(epd)
        if (!opening) return null
        const rates = calcRates(data.wins, data.draws, data.losses)
        return {
          eco: opening.eco,
          name: opening.name,
          epd,
          color: "all" as const,
          wins: data.wins,
          draws: data.draws,
          losses: data.losses,
          ...rates,
        }
      })
      .filter((x): x is OpeningStatView => x !== null)
  }

  // Filter by specific color
  return results
    .filter((r) => r.color === colorFilter)
    .map((r) => {
      const opening = openingMap.get(r.epd)
      if (!opening) return null
      const rates = calcRates(r.wins, r.draws, r.losses)
      return {
        eco: opening.eco,
        name: opening.name,
        epd: r.epd,
        color: colorFilter,
        wins: r.wins,
        draws: r.draws,
        losses: r.losses,
        ...rates,
      }
    })
    .filter((x): x is OpeningStatView => x !== null)
}

export function calcSummary(stats: OpeningStatView[]): SummaryStats {
  const totalWins = stats.reduce((sum, s) => sum + s.wins, 0)
  const totalDraws = stats.reduce((sum, s) => sum + s.draws, 0)
  const totalLosses = stats.reduce((sum, s) => sum + s.losses, 0)
  const rates = calcRates(totalWins, totalDraws, totalLosses)

  const mostPlayed = stats.length > 0
    ? [...stats].sort((a, b) => b.totalGames - a.totalGames)[0].name
    : "-"

  const best = stats.filter((s) => s.totalGames >= 5).length > 0
    ? [...stats].filter((s) => s.totalGames >= 5).sort((a, b) => b.winRate - a.winRate)[0].name
    : stats.length > 0
      ? [...stats].sort((a, b) => b.winRate - a.winRate)[0].name
      : "-"

  return {
    totalGames: rates.totalGames,
    overallWinRate: rates.winRate,
    overallDrawRate: rates.drawRate,
    overallLossRate: rates.lossRate,
    mostPlayedOpening: mostPlayed,
    bestOpening: best,
  }
}
