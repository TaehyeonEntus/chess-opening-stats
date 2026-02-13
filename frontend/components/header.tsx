"use client"

import { Moon, Sun, LogIn, LogOut, Crown } from "lucide-react"
import { useTheme } from "next-themes"
import { Button } from "@/components/ui/button"

interface HeaderProps {
  isLoggedIn: boolean
  playerName?: string
  onToggleLogin: () => void
}

export function Header({ isLoggedIn, playerName, onToggleLogin }: HeaderProps) {
  const { theme, setTheme } = useTheme()

  return (
    <header className="border-b border-border bg-card">
      <div className="mx-auto flex max-w-7xl items-center justify-between px-4 py-3 lg:px-6">
        <div className="flex items-center gap-2.5">
          <Crown className="h-6 w-6 text-foreground" />
          <h1 className="text-lg font-bold tracking-tight text-foreground">
            Chess Opening Stats
          </h1>
        </div>
        <div className="flex items-center gap-2">
          {isLoggedIn && playerName && (
            <span className="hidden text-sm text-muted-foreground sm:inline">
              {playerName}
            </span>
          )}
          <Button
            variant="ghost"
            size="icon"
            onClick={() => setTheme(theme === "dark" ? "light" : "dark")}
            aria-label="Toggle theme"
          >
            <Sun className="h-4 w-4 rotate-0 scale-100 transition-all dark:-rotate-90 dark:scale-0" />
            <Moon className="absolute h-4 w-4 rotate-90 scale-0 transition-all dark:rotate-0 dark:scale-100" />
          </Button>
          <Button variant="outline" size="sm" onClick={onToggleLogin} className="gap-1.5">
            {isLoggedIn ? (
              <>
                <LogOut className="h-3.5 w-3.5" />
                <span className="hidden sm:inline">Logout</span>
              </>
            ) : (
              <>
                <LogIn className="h-3.5 w-3.5" />
                <span className="hidden sm:inline">Login</span>
              </>
            )}
          </Button>
        </div>
      </div>
    </header>
  )
}
