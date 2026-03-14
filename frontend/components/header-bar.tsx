"use client"

import { ThemeToggle } from "@/components/theme-toggle"
import { LanguageToggle } from "@/components/language-toggle"
import { useTranslations } from "next-intl"

export function HeaderBar() {
  const t = useTranslations("home")

  return (
    <div className="mb-8 flex items-center justify-between">
      <h1 className="text-2xl font-bold tracking-tight">{t("title")}</h1>
      <div className="flex items-center gap-2">
        <ThemeToggle />
        <LanguageToggle />
      </div>
    </div>
  )
}
