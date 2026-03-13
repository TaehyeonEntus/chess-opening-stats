"use client"

import { useState } from "react"
import { Loader2 } from "lucide-react"
import { useTranslations } from "next-intl"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import type { Platform } from "@/lib/types"

interface AddPlayerDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  addingPlayer: boolean
  addPlayerError: string | null
  onAdd: (username: string, platform: Platform, onSuccess: () => void) => Promise<void>
  onClearError: () => void
}

export function AddPlayerDialog({
  open,
  onOpenChange,
  addingPlayer,
  addPlayerError,
  onAdd,
  onClearError,
}: AddPlayerDialogProps) {
  const tPlayer = useTranslations("player")
  const tCommon = useTranslations("common")

  const [username, setUsername] = useState("")
  const [platform, setPlatform] = useState<Platform>("chess_com")

  function resetForm() {
    setUsername("")
    setPlatform("chess_com")
    onClearError()
  }

  function handleOpenChange(nextOpen: boolean) {
    if (!nextOpen) resetForm()
    onOpenChange(nextOpen)
  }

  async function handleSubmit() {
    await onAdd(username, platform, () => {
      resetForm()
      onOpenChange(false)
    })
  }

  return (
    <Dialog open={open} onOpenChange={handleOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{tPlayer("addPlayerTitle")}</DialogTitle>
          <DialogDescription>{tPlayer("addPlayerDescription")}</DialogDescription>
        </DialogHeader>

        <div className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="add-player-username">{tPlayer("playerUsername")}</Label>
            <Input
              id="add-player-username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder={tPlayer("usernamePlaceholder")}
              disabled={addingPlayer}
              onKeyDown={(e) => e.key === "Enter" && handleSubmit()}
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="add-player-platform">{tPlayer("platform")}</Label>
            <Select value={platform} onValueChange={(v) => setPlatform(v as Platform)}>
              <SelectTrigger id="add-player-platform">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="chess_com">CHESS.COM</SelectItem>
                <SelectItem value="lichess">LICHESS</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>

        {addPlayerError && (
          <p className="text-sm text-destructive">{addPlayerError}</p>
        )}

        <DialogFooter>
          <Button variant="outline" onClick={() => onOpenChange(false)} disabled={addingPlayer}>
            {tCommon("cancel")}
          </Button>
          <Button onClick={handleSubmit} disabled={addingPlayer}>
            {addingPlayer ? (
              <><Loader2 className="h-4 w-4 animate-spin mr-2" />{tPlayer("adding")}</>
            ) : tPlayer("add")}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}
