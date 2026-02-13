// 백엔드 TSV 기반 오프닝 마스터 데이터
export interface Opening {
  eco: string
  name: string
  epd: string // EPD = unique identifier for position
}

// 백엔드 API에서 제공하는 원시 결과 데이터 (EPD 기반 매칭)
export interface OpeningResult {
  epd: string
  color: "white" | "black"
  wins: number
  draws: number
  losses: number
}

// 프론트에서 비율을 계산한 최종 표시용 데이터
export interface OpeningStatView {
  eco: string
  name: string
  epd: string
  color: "white" | "black" | "all"
  totalGames: number
  wins: number
  draws: number
  losses: number
  winRate: number
  drawRate: number
  lossRate: number
}

export interface SummaryStats {
  totalGames: number
  overallWinRate: number
  overallDrawRate: number
  overallLossRate: number
  mostPlayedOpening: string
  bestOpening: string
}

export type ColorFilter = "all" | "white" | "black"
export type SortBy = "winRate" | "totalGames" | "name"
