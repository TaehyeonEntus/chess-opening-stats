import type { ColorOpeningStat, OpeningStatView, Color, Stat } from "../../types"
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
    stats: ColorOpeningStat[], 
    color: Color, 
    dictionary?: OpeningDictionary
): OpeningStatView[] {
    if (!Array.isArray(stats)) {
        return [];
    }
    
    return stats
        .filter(stat => stat && (typeof stat.id !== 'undefined' || typeof stat.openingId !== 'undefined'))
        .map((stat): OpeningStatView => {
            const actualId = stat.id ?? stat.openingId!;
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

export function adaptStat(
    stats: ColorOpeningStat[], 
    color: Color, 
    dictionary?: OpeningDictionary
): Stat[] {
    if (!Array.isArray(stats)) {
        return [];
    }
    
    return stats
        .filter(stat => stat && (typeof stat.id !== 'undefined' || typeof stat.openingId !== 'undefined'))
        .map((stat): Stat => {
            const actualId = stat.id ?? stat.openingId!;
            const metadata = dictionary ? dictionary[actualId] : null;
            
            return {
                eco: metadata?.eco || "---",
                name: metadata?.name || `Opening #${actualId}`,
                epd: metadata?.epd || "",
                color: color,
                wins: stat.stat?.win || 0,
                draws: stat.stat?.draw || 0,
                losses: stat.stat?.lose || 0,
            }
        })
}
