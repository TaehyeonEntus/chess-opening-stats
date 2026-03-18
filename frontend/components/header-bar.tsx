"use client"

import { useTranslations } from "next-intl"
import { ThemeToggle } from "./theme-toggle"
import { LanguageToggle } from "./language-toggle"
import { cn } from "@/lib/utils"

export function HeaderBar() {
  const t = useTranslations("common")

  return (
    <header className="flex h-16 items-center justify-between border-b bg-background px-4 md:px-6 sticky top-0 z-50">
      <div className="flex items-center gap-2">
        <h1 className="text-xl font-bold tracking-tight md:text-2xl">
          <span className="text-primary">Chess</span>OpeningStats
        </h1>
      </div>

      <div className="flex items-center gap-2 md:gap-4">
        <ThemeToggle />
        <LanguageToggle />
      </div>
    </header>
  )
}
