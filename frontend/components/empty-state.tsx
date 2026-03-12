"use client"

import { Button } from "@/components/ui/button"
import { useTranslations } from "next-intl"

interface EmptyStateProps {
  onAddPlayer: () => void
  disabled?: boolean
}

export function EmptyState({ onAddPlayer, disabled }: EmptyStateProps) {
  const t = useTranslations("home")

  return (
    <div className="flex flex-col items-center justify-center gap-4 rounded-lg border border-border/70 bg-card/50 px-8 py-16">
      <div className="text-center">
        <h3 className="text-lg font-semibold mb-2">{t("emptyState.title")}</h3>
        <p className="text-sm text-muted-foreground mb-6">
          {t("emptyState.description")}
        </p>
        <Button onClick={onAddPlayer} disabled={disabled} size="lg">
          {t("emptyState.linkAccount")}
        </Button>
      </div>
    </div>
  )
}
