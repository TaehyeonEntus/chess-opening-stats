"use client"

import { useState } from "react"
import { Eye, EyeOff, Loader2 } from "lucide-react"
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

interface ChangePasswordDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  changingPassword: boolean
  passwordError: string | null
  onSubmit: (oldPassword: string, newPassword: string, newPasswordConfirm: string, onSuccess: () => void) => Promise<void>
  onClearError: () => void
}

export function ChangePasswordDialog({
  open,
  onOpenChange,
  changingPassword,
  passwordError,
  onSubmit,
  onClearError,
}: ChangePasswordDialogProps) {
  const tAuth = useTranslations("auth")
  const tCommon = useTranslations("common")

  const [oldPassword, setOldPassword] = useState("")
  const [newPassword, setNewPassword] = useState("")
  const [newPasswordConfirm, setNewPasswordConfirm] = useState("")
  const [showOld, setShowOld] = useState(false)
  const [showNew, setShowNew] = useState(false)
  const [showConfirm, setShowConfirm] = useState(false)

  function resetForm() {
    setOldPassword("")
    setNewPassword("")
    setNewPasswordConfirm("")
    setShowOld(false)
    setShowNew(false)
    setShowConfirm(false)
    onClearError()
  }

  function handleOpenChange(nextOpen: boolean) {
    if (!nextOpen) resetForm()
    onOpenChange(nextOpen)
  }

  async function handleSubmit() {
    await onSubmit(oldPassword, newPassword, newPasswordConfirm, () => {
      resetForm()
      onOpenChange(false)
    })
  }

  return (
    <Dialog open={open} onOpenChange={handleOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{tAuth("changePasswordTitle")}</DialogTitle>
          <DialogDescription>{tAuth("changePasswordDescription")}</DialogDescription>
        </DialogHeader>

        <div className="space-y-4">
          {[
            { id: "old-password", label: tAuth("oldPassword"), value: oldPassword, setValue: setOldPassword, show: showOld, setShow: setShowOld, autoComplete: "current-password" },
            { id: "new-password", label: tAuth("newPassword"), value: newPassword, setValue: setNewPassword, show: showNew, setShow: setShowNew, autoComplete: "new-password" },
            { id: "new-password-confirm", label: tAuth("newPasswordConfirm"), value: newPasswordConfirm, setValue: setNewPasswordConfirm, show: showConfirm, setShow: setShowConfirm, autoComplete: "new-password" },
          ].map(({ id, label, value, setValue, show, setShow, autoComplete }) => (
            <div key={id} className="space-y-2">
              <Label htmlFor={id}>{label}</Label>
              <div className="relative">
                <Input
                  id={id}
                  type={show ? "text" : "password"}
                  value={value}
                  onChange={(e) => setValue(e.target.value)}
                  disabled={changingPassword}
                  autoComplete={autoComplete}
                  className="pr-10"
                />
                <button
                  type="button"
                  onClick={() => setShow((prev) => !prev)}
                  aria-label={show ? tAuth("hidePassword") : tAuth("showPassword")}
                  className="absolute right-2 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground transition-colors"
                >
                  {show ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                </button>
              </div>
            </div>
          ))}

          {passwordError && (
            <p className="text-sm text-destructive">{passwordError}</p>
          )}
        </div>

        <DialogFooter>
          <Button variant="outline" onClick={() => onOpenChange(false)} disabled={changingPassword}>
            {tCommon("cancel")}
          </Button>
          <Button onClick={handleSubmit} disabled={changingPassword}>
            {changingPassword ? (
              <><Loader2 className="h-4 w-4 animate-spin mr-2" />{tAuth("changing")}</>
            ) : tCommon("confirm")}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}
