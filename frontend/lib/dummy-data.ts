import type { Opening, OpeningResult } from "./types"

export const OPENINGS_MASTER: Opening[] = [
  { eco: "A00", name: "Starting Position", epd: "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -" },
  { eco: "B00", name: "King's Pawn Opening", epd: "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq -" },
  { eco: "A40", name: "Queen's Pawn Opening", epd: "rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR b KQkq -" },
  { eco: "A10", name: "English Opening", epd: "rnbqkbnr/pppppppp/8/8/2P5/8/PP1PPPPP/RNBQKBNR b KQkq -" },
  { eco: "A04", name: "Reti Opening", epd: "rnbqkbnr/pppppppp/8/8/8/5N2/PPPPPPPP/RNBQKB1R b KQkq -" },
  { eco: "B20", name: "Sicilian Defense", epd: "rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq -" },
  { eco: "C20", name: "King's Pawn: e5 Response", epd: "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq -" },
  { eco: "C00", name: "French Defense", epd: "rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq -" },
  { eco: "B10", name: "Caro-Kann Defense", epd: "rnbqkbnr/pp1ppppp/2p5/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq -" },
  { eco: "B06", name: "Pirc Defense", epd: "rnbqkbnr/ppp1pppp/3p4/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq -" },
  { eco: "B01", name: "Scandinavian Defense", epd: "rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR w KQkq -" },
  { eco: "B02", name: "Alekhine's Defense", epd: "rnbqkb1r/pppppppp/5n2/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq -" },
  { eco: "C50", name: "Italian Game", epd: "r1bqkbnr/pppp1ppp/2n5/4p3/2B1P3/5N2/PPPP1PPP/RNBQK2R b KQkq -" },
  { eco: "C60", name: "Ruy Lopez", epd: "r1bqkbnr/pppp1ppp/2n5/1B2p3/4P3/5N2/PPPP1PPP/RNBQK2R b KQkq -" },
  { eco: "C45", name: "Scotch Game", epd: "r1bqkbnr/pppp1ppp/2n5/4p3/3PP3/5N2/PPP2PPP/RNBQKB1R b KQkq -" },
  { eco: "C46", name: "Four Knights Game", epd: "r1bqkb1r/pppp1ppp/2n2n2/4p3/4P3/2N2N2/PPPP1PPP/R1BQKB1R w KQkq -" },
  { eco: "C55", name: "Italian Game: Two Knights", epd: "r1bqkb1r/pppp1ppp/2n2n2/4p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq -" },
  { eco: "C41", name: "Philidor Defense", epd: "rnbqkbnr/ppp2ppp/3p4/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq -" },
  { eco: "D06", name: "Queen's Gambit", epd: "rnbqkbnr/ppp1pppp/8/3p4/2PP4/8/PP2PPPP/RNBQKBNR b KQkq -" },
  { eco: "D30", name: "Queen's Gambit Declined", epd: "rnbqkbnr/ppp2ppp/4p3/3p4/2PP4/8/PP2PPPP/RNBQKBNR w KQkq -" },
  { eco: "D20", name: "Queen's Gambit Accepted", epd: "rnbqkbnr/ppp1pppp/8/8/2pP4/8/PP2PPPP/RNBQKBNR w KQkq -" },
  { eco: "E60", name: "King's Indian Defense", epd: "rnbqkb1r/pppppp1p/5np1/8/2PP4/8/PP2PPPP/RNBQKBNR w KQkq -" },
  { eco: "E70", name: "King's Indian: Normal Variation", epd: "rnbqk2r/ppp1ppbp/3p1np1/8/2PPP3/2N5/PP3PPP/R1BQKBNR b KQkq -" },
  { eco: "E00", name: "Catalan Opening", epd: "rnbqkb1r/pppp1ppp/4pn2/8/2PP4/6P1/PP2PP1P/RNBQKBNR b KQkq -" },
  { eco: "A45", name: "Trompowsky Attack", epd: "rnbqkb1r/pppppppp/5n2/6B1/3P4/8/PPP1PPPP/RN1QKBNR b KQkq -" },
  { eco: "A46", name: "London System", epd: "rnbqkb1r/pppppppp/5n2/8/3P1B2/8/PPP1PPPP/RN1QKBNR b KQkq -" },
  { eco: "A00", name: "Hungarian Opening", epd: "rnbqkbnr/pppppppp/8/8/8/5NP1/PPPPPP1P/RNBQKB1R b KQkq -" },
  { eco: "D35", name: "Queen's Gambit Declined: Exchange", epd: "rnbqkb1r/ppp2ppp/4pn2/3P4/3P4/8/PP2PPPP/RNBQKBNR b KQkq -" },
  { eco: "B07", name: "Lion Defense", epd: "rnbqkb1r/ppp1pppp/3p1n2/8/3PP3/8/PPP2PPP/RNBQKBNR w KQkq -" },
]

// Seed-based pseudo-random for consistent dummy data
function seededResults(seed: number, scale: number): { wins: number; draws: number; losses: number } {
  const base = seed * 7 + 13
  return {
    wins: Math.max(1, ((base * 3 + 7) % (80 * scale)) + Math.floor(scale * 5)),
    draws: Math.max(1, ((base * 5 + 11) % (40 * scale)) + Math.floor(scale * 2)),
    losses: Math.max(1, ((base * 2 + 3) % (60 * scale)) + Math.floor(scale * 3)),
  }
}

// 전체 플레이어 통합 데이터 (비로그인 - 큰 수, EPD 기반)
export const GLOBAL_RESULTS: OpeningResult[] = OPENINGS_MASTER.flatMap((opening, i) => [
  {
    epd: opening.epd,
    color: "white" as const,
    ...seededResults(i * 2, 50),
  },
  {
    epd: opening.epd,
    color: "black" as const,
    ...seededResults(i * 2 + 1, 50),
  },
])

// 개인 데이터 (로그인 - 작은 수)
export const PERSONAL_RESULTS: OpeningResult[] = OPENINGS_MASTER.slice(0, 18).flatMap((opening, i) => [
  {
    epd: opening.epd,
    color: "white" as const,
    ...seededResults(i * 3 + 100, 3),
  },
  {
    epd: opening.epd,
    color: "black" as const,
    ...seededResults(i * 3 + 101, 3),
  },
])
