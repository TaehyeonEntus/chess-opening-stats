// 백엔드 API에서 제공하는 원시 결과 데이터 (EPD 기반 매칭)
export interface OpeningResult {
    eco: string
    epd: string
    name: string
    color: Color
    wins: number
    draws: number
    losses: number
}

export interface OpeningRate {
    winRate: number
    drawRate: number
    lossRate: number
}

export interface OpeningStatView {
    eco: string
    epd: string
    name: string
    color: Color
    totalGames: number
    wins: number
    draws: number
    losses: number
    winRate: number
    drawRate: number
    lossRate: number
}

export type Color = "white" | "black"
export type ColorFilter = "all" | "white" | "black"
export type SortBy = "winRate" | "totalGames" | "name"

// Summary API types
export interface WinRate {
    color: Color
    wins: number
    draws: number
    losses: number
}

export interface Stat {
    eco: string
    epd: string
    name: string
    color: Color
    wins: number
    draws: number
    losses: number
}

export interface SummaryResponse {
    bestWinRateOpenings: Stat[]
    mostPlayedOpenings: Stat[]
    winRates: WinRate[]
}

// Opening Stats API types
export interface OpeningStatsResponse {
    openingStats: OpeningResult[]
}

// Pre-calculated summary for display
export interface DisplaySummary {
    totalWins: number;
    totalDraws: number;
    totalLosses: number;
    totalGames: number;
    winRate: number;
    drawRate: number;
    lossRate: number;
    bestWinRateOpenings: Stat[];
    mostPlayedOpenings: Stat[];
}
