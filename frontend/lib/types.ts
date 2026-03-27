export type Color = "white" | "black"
export type Platform = "chess_com" | "lichess"
export type ColorFilter = "all" | "white" | "black"
export type SortBy = "winRate" | "totalGames" | "name"

/**
 * 1. Player API Types (GET /player/profile)
 */
export interface PlayerInfo {
    exists: boolean
    username: string
    image_url?: string
    last_online?: number
}

/**
 * 3. Dashboard API Types (SSE /dashboard)
 */
export interface StatDto {
    win: number
    draw: number
    lose: number
}

export interface OpeningStat {
    openingId: number
    stat: StatDto
}

export interface ColorDashboard {
    stat: StatDto
    mostPlayedOpenings: OpeningStat[]
    highestWinRateOpenings: OpeningStat[]
    openings: OpeningStat[]
}

export interface DashboardResponse {
    white: ColorDashboard
    black: ColorDashboard
}

/**
 * Frontend Internal Display Types
 */
export interface OpeningStatView {
    id: number
    eco: string
    name: string
    epd: string
    color: Color
    totalGames: number
    wins: number
    draws: number
    losses: number
    winRate: number
    drawRate: number
    lossRate: number
}

export interface WinRate {
    color: Color
    wins: number
    draws: number
    losses: number
}

export interface DisplaySummary {
    totalWins: number;
    totalDraws: number;
    totalLosses: number;
    totalGames: number;
    winRate: number;
    drawRate: number;
    lossRate: number;
    bestWinRateOpenings: OpeningStatView[];
    mostPlayedOpenings: OpeningStatView[];
}
