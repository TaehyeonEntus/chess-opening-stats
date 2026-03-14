export type Color = "white" | "black"
export type Platform = "chess_com" | "lichess"
export type ColorFilter = "all" | "white" | "black"
export type SortBy = "winRate" | "totalGames" | "name"

export interface ApiResponseDto<T> {
  code: string;
  message: string;
  data: T;
}

// ----------------------------------------------------------------------
// 1. Account / Player API Types
// ----------------------------------------------------------------------

export interface PlayerInfo {
    id: number
    username: string
    platform: Platform
    lastPlayedAt: string // ISO 8601 datetime string
}

export interface AccountSummary {
    id: number
    nickname: string
    lastSyncedAt: string // ISO 8601 datetime string
}

// Previously AccountInfoResponse
export interface AccountInfoResponse {
    nickname: string
    lastSyncedAt: string 
    players: PlayerInfo[]
}

export type SyncGameResponse = Record<string, number>

export interface SyncGameStatus {
  status: boolean
}

// ----------------------------------------------------------------------
// 2. Statistics API Types (Dashboard / Records)
// ----------------------------------------------------------------------

export interface ColorRecord {
  color: Color | "unknown"
  win: number
  draw: number
  lose: number
}

export interface ColorOpeningStat {
  color: Color
  id?: number
  openingId?: number // Backend might use openingId instead of id
  win: number
  draw: number
  lose: number
}

export interface ColorDashboard {
  record: ColorRecord | null
  mostPlayedOpenings: ColorOpeningStat[]
  highestWinRateOpenings: ColorOpeningStat[]
  openings: ColorOpeningStat[] | null
}

export interface HomeView {
  account: AccountSummary
  players: PlayerInfo[]
  white: ColorDashboard
  black: ColorDashboard
}

export interface WinRate {
  color: Color
  wins: number
  draws: number
  losses: number
}

// ----------------------------------------------------------------------
// 3. Frontend Internal Display Types
// ----------------------------------------------------------------------

export interface OpeningRate {
    winRate: number
    drawRate: number
    lossRate: number
}

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

export interface Stat {
    eco: string
    name: string
    epd: string
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
    bestWinRateOpenings: Stat[];
    mostPlayedOpenings: Stat[];
}
