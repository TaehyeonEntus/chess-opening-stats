"use client"

import { ThemeToggle } from "@/components/theme-toggle"
import { LanguageToggle } from "@/components/language-toggle"
import { Button } from "@/components/ui/button"
import { Skeleton } from "@/components/ui/skeleton"
import { useTranslations } from "next-intl"

interface HeaderBarProps {
  loading: boolean
  nickname: string
  onMyPageOpen: () => void
}

export function HeaderBar({ loading, nickname, onMyPageOpen }: HeaderBarProps) {
  const t = useTranslations("home")
  const tCommon = useTranslations("common")

  return (
    <>
      <div className="absolute top-4 right-4 flex gap-2">
        <ThemeToggle />
        <LanguageToggle />
      </div>

      {loading ? (
        <div className="mb-6 rounded-lg border border-border/70 bg-card/50 px-4 py-3">
          <Skeleton className="h-6 w-48" />
        </div>
      ) : (
        <header className="mb-6 flex items-center justify-between rounded-lg border border-border/70 bg-card/50 px-4 py-3">
          <p className="text-lg font-semibold">
            {nickname || tCommon("loading")}
          </p>
          <Button variant="outline" onClick={onMyPageOpen}>
            {t("myPage")}
          </Button>
        </header>
      )}
    </>
  )
}
