import { type ClassValue, clsx } from "clsx"
import { twMerge } from "tailwind-merge"

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

/**
 * Returns a Tailwind color class based on the win rate percentage.
 * @param winRate The win rate percentage (0-100)
 * @param type 'text' for text color, 'bg' for background color
 */
export function getWinRateColor(winRate: number, type: 'text' | 'bg' = 'text'): string {
  if (winRate === 100) return type === 'text' ? 'animate-rainbow font-black' : 'bg-rainbow'

  if (winRate >= 60) return type === 'text' ? 'text-blue-600 dark:text-blue-400 font-bold' : 'bg-blue-600'
  if (winRate > 45) return type === 'text' ? 'text-emerald-600 dark:text-emerald-400 font-bold' : 'bg-emerald-600'
  if (winRate > 40) return type === 'text' ? 'text-orange-500 dark:text-orange-400 font-semibold' : 'bg-orange-500'

  return type === 'text' ? 'text-rose-500 dark:text-rose-400 font-semibold' : 'bg-rose-500'
}

