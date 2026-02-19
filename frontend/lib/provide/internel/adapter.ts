import type {OpeningResult, OpeningStatView} from "../../types"
import { calculateRatesFromCounts } from "@/lib/stats"

export function calcOpeningRates(totalGames: number, wins: number, draws: number, losses: number) {
    const rates = calculateRatesFromCounts(wins, draws, losses)

    return {
        totalGames : totalGames,
        winRate: rates.winRate,
        drawRate: rates.drawRate,
        lossRate: rates.lossRate,
    }
}

export function adaptOpeningResult(results: OpeningResult[]): OpeningStatView[] {
    if (!Array.isArray(results)) {
        console.error("adaptOpeningResult: results is not an array", results);
        return [];
    }
    return results
        .map((result): OpeningStatView => ({
            ...result,
            ...calcOpeningRates(result.wins + result.draws + result.losses, result.wins, result.draws, result.losses)
        }))
}
