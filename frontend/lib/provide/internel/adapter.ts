import type { OpeningStat, OpeningStatView, Color } from "../../types"
import { calculateRatesFromCounts } from "@/lib/stats"
import type { OpeningDictionary } from "@/lib/openings/csv-parser"

export function calcOpeningRates(totalGames: number, wins: number, draws: number, losses: number) {
    const rates = calculateRatesFromCounts(wins, draws, losses)

    return {
        totalGames : totalGames,
        winRate: rates.winRate,
        drawRate: rates.drawRate,
        lossRate: rates.lossRate,
    }
}

export function adaptColorOpeningStat(
    stats: OpeningStat[], 
    color: Color, 
    dictionary?: OpeningDictionary
): OpeningStatView[] {
    if (!Array.isArray(stats)) {
        return [];
    }
    
    return stats
        .filter(stat => stat && typeof stat.openingId !== 'undefined')
        .map((stat): OpeningStatView => {
            const actualId = stat.openingId;
            const metadata = dictionary ? dictionary[actualId] : null;
            const wins = stat.stat?.win || 0;
            const draws = stat.stat?.draw || 0;
            const losses = stat.stat?.lose || 0;
            const totalGames = wins + draws + losses;
            
            return {
                id: actualId,
                eco: metadata?.eco || "---",
                name: metadata?.name || `Opening #${actualId}`,
                epd: metadata?.epd || "",
                color: color,
                wins,
                draws,
                losses,
                ...calcOpeningRates(totalGames, wins, draws, losses)
            }
        })
}
