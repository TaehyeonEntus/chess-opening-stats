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
            
            return {
                id: actualId,
                eco: metadata?.eco || "---",
                name: metadata?.name || `Opening #${actualId}`,
                epd: metadata?.epd || "",
                color: color,
                wins: stat.win || 0,
                draws: stat.draw || 0,
                losses: stat.lose || 0,
                ...calcOpeningRates(
                    (stat.win || 0) + (stat.draw || 0) + (stat.lose || 0), 
                    stat.win || 0, 
                    stat.draw || 0, 
                    stat.lose || 0
                )
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
                wins: stat.win || 0,
                draws: stat.draw || 0,
                losses: stat.lose || 0,
            }
        })
}
