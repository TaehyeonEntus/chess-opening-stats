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
    
    return stats.map((stat): OpeningStatView => {
        const metadata = dictionary ? dictionary[stat.id] : null;
        
        return {
            id: stat.id,
            eco: metadata?.eco || "---",
            name: metadata?.name || `Opening #${stat.id}`,
            epd: metadata?.epd || "",
            color: color,
            wins: stat.win,
            draws: stat.draw,
            losses: stat.lose,
            ...calcOpeningRates(stat.win + stat.draw + stat.lose, stat.win, stat.draw, stat.lose)
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
    
    return stats.map((stat): Stat => {
        const metadata = dictionary ? dictionary[stat.id] : null;
        
        return {
            eco: metadata?.eco || "---",
            name: metadata?.name || `Opening #${stat.id}`,
            epd: metadata?.epd || "",
            color: color,
            wins: stat.win,
            draws: stat.draw,
            losses: stat.lose,
        }
    })
}
