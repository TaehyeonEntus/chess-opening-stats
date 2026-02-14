import type {OpeningResult, OpeningStatView} from "../../types"

export function calcOpeningRates(totalGames: number, wins: number, draws: number, losses: number) {
    return {
        totalGames : totalGames,
        winRate: totalGames > 0 ? Math.round((wins / totalGames) * 1000) / 10 : 0,
        drawRate: totalGames > 0 ? Math.round((draws / totalGames) * 1000) / 10 : 0,
        lossRate: totalGames > 0 ? Math.round((losses / totalGames) * 1000) / 10 : 0,
    }
}

export function adaptOpeningResult(results: OpeningResult[]): OpeningStatView[] {
    return results
        .map((result): OpeningStatView => ({
            ...result,
            ...calcOpeningRates(result.wins + result.draws + result.losses, result.wins, result.draws, result.losses)
        }))
}
