import type { DashboardResponse } from "@/lib/types"

export const MOCK_DASHBOARD_DATA: DashboardResponse = {
  white: {
    stat: { win: 520, draw: 120, lose: 360 }, // total 1000
    mostPlayedOpenings: [
      { openingId: 1, stat: { win: 50, draw: 10, lose: 40 } }, // Ruy Lopez
      { openingId: 2, stat: { win: 30, draw: 20, lose: 10 } }, // Italian Game
      { openingId: 3, stat: { win: 25, draw: 5, lose: 20 } }, // Sicilian Defense
      { openingId: 4, stat: { win: 30, draw: 15, lose: 35 } }, // Queen's Gambit (37.5% - Red)
      { openingId: 5, stat: { win: 17, draw: 10, lose: 13 } }, // French Defense (42.5% - Orange)
    ],
    highestWinRateOpenings: [
      { openingId: 6, stat: { win: 15, draw: 2, lose: 3 } }, // King's Gambit (High Win Rate)
      { openingId: 7, stat: { win: 12, draw: 1, lose: 2 } }, // Vienna Game
      { openingId: 8, stat: { win: 10, draw: 2, lose: 1 } }, // Scotch Game
      { openingId: 2, stat: { win: 30, draw: 20, lose: 10 } }, // Italian Game (Also high win rate)
      { openingId: 1, stat: { win: 50, draw: 10, lose: 40 } }, // Ruy Lopez
    ],
    openings: [
      { openingId: 1, stat: { win: 50, draw: 10, lose: 40 } },
      { openingId: 2, stat: { win: 30, draw: 20, lose: 10 } },
      { openingId: 3, stat: { win: 25, draw: 5, lose: 20 } },
      { openingId: 4, stat: { win: 40, draw: 15, lose: 25 } },
      { openingId: 5, stat: { win: 20, draw: 10, lose: 10 } },
      { openingId: 6, stat: { win: 15, draw: 2, lose: 3 } },
      { openingId: 7, stat: { win: 12, draw: 1, lose: 2 } },
      { openingId: 8, stat: { win: 10, draw: 2, lose: 1 } },
    ]
  },
  black: {
    stat: { win: 450, draw: 150, lose: 400 }, // total 1000
    mostPlayedOpenings: [
      { openingId: 9, stat: { win: 35, draw: 15, lose: 30 } }, // Caro-Kann Defense
      { openingId: 10, stat: { win: 28, draw: 12, lose: 20 } }, // Slav Defense
      { openingId: 11, stat: { win: 22, draw: 8, lose: 18 } }, // Pirc Defense
      { openingId: 12, stat: { win: 45, draw: 20, lose: 35 } }, // King's Indian Defense
      { openingId: 13, stat: { win: 18, draw: 5, lose: 12 } }, // Modern Defense
    ],
    highestWinRateOpenings: [
      { openingId: 14, stat: { win: 14, draw: 3, lose: 3 } }, // Alekhine's Defense
      { openingId: 15, stat: { win: 11, draw: 2, lose: 2 } }, // Scandinavian Defense
      { openingId: 12, stat: { win: 45, draw: 20, lose: 35 } }, // King's Indian Defense
      { openingId: 9, stat: { win: 35, draw: 15, lose: 30 } }, // Caro-Kann Defense
      { openingId: 10, stat: { win: 28, draw: 12, lose: 20 } }, // Slav Defense
    ],
    openings: [
      { openingId: 9, stat: { win: 35, draw: 15, lose: 30 } },
      { openingId: 10, stat: { win: 28, draw: 12, lose: 20 } },
      { openingId: 11, stat: { win: 22, draw: 8, lose: 18 } },
      { openingId: 12, stat: { win: 45, draw: 20, lose: 35 } },
      { openingId: 13, stat: { win: 18, draw: 5, lose: 12 } },
      { openingId: 14, stat: { win: 14, draw: 3, lose: 3 } },
      { openingId: 15, stat: { win: 11, draw: 2, lose: 2 } },
    ]
  }
}
