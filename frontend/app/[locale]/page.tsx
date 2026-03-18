"use client"

import { useState, useCallback, useMemo, useEffect } from "react"
import { useTranslations } from "next-intl"
import { useRouter, usePathname } from "@/i18n/navigation"
import { useSearchParams } from "next/navigation"
import { OpeningGrid } from "@/components/opening-grid"
import { OpeningSummary } from "@/components/opening-summary"
import { OpeningFilter } from "@/components/opening-filter"
import { HeaderBar } from "@/components/header-bar"
import { useOpeningData } from "@/hooks/use-opening-data"
import { RefreshCw, Search, Check, X, Clock, ExternalLink } from "lucide-react"
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Card, CardContent } from "@/components/ui/card"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { syncGames, type PlayerInfo } from "@/lib/api/api"
import { provideCheckPlayerExists } from "@/lib/provide/provideFacade"
import { toast } from "sonner"
import { cn } from "@/lib/utils"

export default function HomePage() {
  const t = useTranslations("home")
  const tCommon = useTranslations("common")

  const router = useRouter()
  const pathname = usePathname()
  const searchParams = useSearchParams()

  // Format last_online (seconds ago) with localized units
  const formatLastOnline = useCallback((seconds: number) => {
    if (seconds < 60) return t("unitSecond", { count: seconds })
    const mins = Math.floor(seconds / 60)
    if (mins < 60) return t("unitMinute", { count: mins })
    const hours = Math.floor(mins / 60)
    if (hours < 24) return t("unitHour", { count: hours })
    const days = Math.floor(hours / 24)
    return t("unitDay", { count: days })
  }, [t])

  const [platform, setPlatform] = useState<string>(searchParams.get("platform") || "CHESS_COM")
  const [usernameInput, setUsernameInput] = useState<string>(searchParams.get("username") || "")
  const [playerInfo, setPlayerInfo] = useState<PlayerInfo | null>(null)
  
  // States: 'idle' | 'checking' | 'confirming' | 'syncing' | 'ready'
  const [status, setStatus] = useState<'idle' | 'checking' | 'confirming' | 'syncing' | 'ready'>('idle')
  const [isResetting, setIsResetting] = useState(false)

  const {
    allOpenings,
    epdMap,
    filteredAndSortedOpenings,
    currentSummary,
    isPolling,
    error,
    loadData,
    clearData,
    colorFilter, setColorFilter,
    sortBy, setSortBy,
    minGames, setMinGames,
    maxGames, setMaxGames,
    search, setSearch,
  } = useOpeningData()

  const handleConfirm = useCallback(async () => {
    if (!playerInfo?.username) return
    
    setStatus('syncing')
    try {
      const response = await syncGames(platform, playerInfo.username)
      toast.success(t("syncSuccess", { count: response.waiting }))
      loadData(platform, playerInfo.username)
    } catch (err) {
      console.error("Sync failed", err)
      toast.error(t("syncFailed"))
      setStatus('confirming')
    }
  }, [platform, playerInfo, loadData, t])

  const handleCancel = useCallback(() => {
    setIsResetting(true)
    setPlayerInfo(null)
    setStatus('idle')
    setUsernameInput("")
    setPlatform("CHESS_COM")
    clearData()
    router.replace(pathname)
    // Small timeout to allow the URL change to take effect before re-enabling sync
    setTimeout(() => setIsResetting(false), 1000)
  }, [router, pathname, clearData])

  const updateUrl = useCallback((user: string, plat: string) => {
    const params = new URLSearchParams()
    params.set("platform", plat)
    params.set("username", user)
    router.replace(`${pathname}?${params.toString()}`)
  }, [pathname, router])

  const handleSearch = useCallback(async (autoSync = false) => {
    const queryUsername = autoSync ? (searchParams.get("username") || "") : usernameInput
    const queryPlatform = autoSync ? (searchParams.get("platform") || "CHESS_COM") : platform

    if (!queryUsername.trim()) {
      if (!autoSync) toast.error("Please enter a username")
      return
    }

    setStatus('checking')
    try {
      const info = await provideCheckPlayerExists(queryPlatform, queryUsername)
      if (!info.exists) {
        if (!autoSync) toast.error(t("playerNotFound"))
        setStatus('idle')
        return
      }
      setPlayerInfo(info)
      
      if (autoSync) {
        setStatus('syncing')
        try {
          const response = await syncGames(queryPlatform, info.username || "")
          toast.success(t("syncSuccess", { count: response.waiting }))
          loadData(queryPlatform, info.username || "")
        } catch (err) {
          console.error("Auto-sync failed", err)
          setStatus('confirming')
        }
      } else {
        setStatus('confirming')
        updateUrl(queryUsername, queryPlatform)
      }
    } catch (err) {
      console.error("Search failed", err)
      if (!autoSync) toast.error(t("playerNotFound"))
      setStatus('idle')
    }
  }, [platform, usernameInput, loadData, updateUrl, tCommon, searchParams, t])

  useEffect(() => {
    const urlUsername = searchParams.get("username")
    const urlPlatform = searchParams.get("platform")
    if (urlUsername && urlPlatform && status === 'idle' && allOpenings.length === 0 && !isResetting) {
      handleSearch(true)
    }
  }, [searchParams, handleSearch, status, allOpenings.length, isResetting])

  // Determine if data is ready based on useOpeningData's state
  const isDataReady = allOpenings.length > 0 || currentSummary !== null

  useEffect(() => {
    if (isDataReady && (status === 'syncing' || status === 'confirming')) {
      setStatus('ready')
    }
  }, [isDataReady, status])

  return (
    <div className="min-h-screen overflow-x-hidden bg-background">
      <main id="main-content" className="mx-auto max-w-screen-2xl px-4 py-6 lg:px-6">
        <HeaderBar />

        {/* Step 1: Initial Search Input */}
        {status === 'idle' && !isDataReady && (
          <section className="flex flex-col items-center justify-center py-32 space-y-10" aria-labelledby="explore-title">
            <div className="text-center space-y-4 max-w-2xl">
              <h2 id="explore-title" className="text-4xl md:text-5xl font-extrabold tracking-tight">{t("exploreTitle")}</h2>
              <p className="text-xl text-muted-foreground">{t("exploreDescription")}</p>
            </div>
            
            <div className="flex flex-col md:flex-row gap-4 w-full max-w-2xl p-2 bg-card rounded-2xl border shadow-xl">
              <Select value={platform} onValueChange={setPlatform}>
                <SelectTrigger 
                  className="h-14 w-full md:w-[200px] border-none bg-transparent text-lg focus:ring-0"
                  aria-label={tCommon("platform")}
                >
                  <SelectValue placeholder={tCommon("platform")} />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="CHESS_COM">Chess.com</SelectItem>
                  <SelectItem value="LICHESS">Lichess</SelectItem>
                </SelectContent>
              </Select>
              
              <div className="h-14 hidden md:block w-[1px] bg-border my-auto" aria-hidden="true" />
              
              <div className="relative flex-1">
                <Input
                  placeholder={tCommon("username") + "…"}
                  value={usernameInput}
                  onChange={(e) => setUsernameInput(e.target.value)}
                  onKeyDown={(e) => e.key === "Enter" && handleSearch()}
                  className="h-14 border-none bg-transparent text-lg focus-visible:ring-0 px-4"
                  aria-label={tCommon("username")}
                  autoComplete="username"
                  spellCheck={false}
                />
              </div>
              
              <Button 
                onClick={() => handleSearch()} 
                size="lg" 
                className="h-14 px-8 rounded-xl font-bold transition-all hover:scale-105 active:scale-95"
                aria-label={tCommon("search")}
              >
                <Search className="mr-2 h-5 w-5" aria-hidden="true" />
                {tCommon("search")}
              </Button>
            </div>
          </section>
        )}

        {/* Step 2: Confirmation / Checking */}
        {(status === 'checking' || status === 'confirming') && !isDataReady && (
          <section className="flex flex-col items-center justify-center py-32 space-y-8 animate-in fade-in zoom-in duration-300" aria-live="polite">
            {status === 'checking' ? (
              <div className="flex flex-col items-center gap-4">
                <RefreshCw className="h-12 w-12 animate-spin text-primary" />
                <p className="text-lg font-medium">{t("verifyingPlayer")}</p>
              </div>
            ) : (
              <div className="flex flex-col items-center space-y-8 w-full max-w-md">
                <h2 className="text-2xl font-bold">{t("isThisYou")}</h2>
                
                <Card className={cn(
                  "w-full overflow-hidden transition-all duration-500 border-2",
                  "border-green-500 shadow-[0_0_30px_rgba(34,197,94,0.3)] bg-green-50/5 dark:bg-green-900/5"
                )}>
                  <CardContent className="p-8 flex flex-col items-center text-center space-y-6">
                    <div className="relative">
                      <Avatar className="h-32 w-32 border-4 border-background shadow-lg">
                        <AvatarImage src={playerInfo?.image_url} alt={playerInfo?.username} />
                        <AvatarFallback className="text-3xl font-bold bg-primary/10 text-primary">
                          {playerInfo?.username?.[0]?.toUpperCase()}
                        </AvatarFallback>
                      </Avatar>
                      <div className="absolute -bottom-2 -right-2 bg-green-500 text-white p-1.5 rounded-full shadow-lg">
                        <Check className="h-5 w-5" />
                      </div>
                    </div>
                    
                    <div className="space-y-1">
                      <h3 className="text-3xl font-bold tracking-tight">{playerInfo?.username}</h3>
                      <div className="flex items-center justify-center gap-2 text-muted-foreground">
                        <span className="uppercase text-xs font-bold px-2 py-0.5 rounded bg-muted">
                          {platform.replace('_', '.')}
                        </span>
                        {playerInfo?.last_online !== undefined && (
                          <div className="flex items-center text-xs gap-1">
                            <Clock className="h-3 w-3" aria-hidden="true" />
                            <span>{t("activeTime", { time: formatLastOnline(playerInfo.last_online) })}</span>
                          </div>
                        )}

                      </div>
                    </div>
                  </CardContent>
                </Card>

                <div className="flex gap-4 w-full">
                  <Button variant="outline" size="lg" className="flex-1 h-14 rounded-xl" onClick={handleCancel}>
                    <X className="mr-2 h-5 w-5" />
                    {t("goBack")}
                  </Button>
                  <Button size="lg" className="flex-1 h-14 rounded-xl bg-green-600 hover:bg-green-700" onClick={handleConfirm}>
                    <Check className="mr-2 h-5 w-5" />
                    {t("confirmMe")}
                  </Button>
                </div>
              </div>
            )}
          </section>
        )}

        {/* Step 3: Syncing / Polling */}
        {status === 'syncing' && !isDataReady && (
          <section className="flex flex-col items-center justify-center py-32 text-center space-y-8" aria-live="polite">
            <div className="relative">
              <div className="absolute inset-0 animate-ping rounded-full bg-primary/20" />
              <div className="relative flex h-20 w-20 items-center justify-center rounded-full bg-primary/10 text-primary shadow-inner">
                <RefreshCw className="h-10 w-10 animate-spin" />
              </div>
            </div>
            
            <div className="space-y-4">
              <div className="space-y-2">
                <h2 className="text-2xl font-bold tracking-tight">{t("syncTitle")}</h2>
                <p className="text-muted-foreground max-w-md mx-auto">
                  {t("syncDescription", { platform: platform.replace('_', '.') })}
                </p>
              </div>

              <div className="pt-8 grid gap-4 max-w-sm mx-auto">
                <div className="flex items-center gap-3 text-sm text-muted-foreground bg-muted/30 p-3 rounded-lg border border-border/50">
                  <div className="h-2 w-2 rounded-full bg-blue-500 animate-pulse" />
                  <p className="text-left leading-snug">{t("syncLichessLimit")}</p>
                </div>
                <div className="flex items-center gap-3 text-sm text-muted-foreground bg-muted/30 p-3 rounded-lg border border-border/50">
                  <div className="h-2 w-2 rounded-full bg-amber-500 animate-pulse" />
                  <p className="text-left leading-snug">{t("syncHikaruTip")}</p>
                </div>
                <div className="flex items-center gap-3 text-sm text-muted-foreground bg-muted/30 p-3 rounded-lg border border-border/50">
                  <div className="h-2 w-2 rounded-full bg-green-500 animate-pulse" />
                  <p className="text-left leading-snug">{t("syncAnalysisLimit")}</p>
                </div>
              </div>
            </div>
          </section>
        )}

        {/* Error State */}
        {error && (
          <aside className="max-w-md mx-auto mt-8" aria-live="assertive">
             <Alert variant="destructive">
              <AlertTitle>{tCommon("error")}</AlertTitle>
              <AlertDescription>{error}</AlertDescription>
            </Alert>
            <Button className="w-full mt-4" variant="outline" onClick={handleCancel}>
              {tCommon("tryAgain")}
            </Button>
          </aside>
        )}

        {/* Step 4: Dashboard Content */}
        {isDataReady && (
          <div className="flex flex-col gap-6 animate-in fade-in slide-in-from-bottom-4 duration-700">
            <section className="flex items-center justify-between bg-card p-4 rounded-xl border shadow-sm" aria-labelledby="player-profile-title">
              <h2 id="player-profile-title" className="sr-only">{t("profile")}</h2>
              <div className="flex items-center gap-4">
                <Avatar className="h-12 w-12 border shadow-sm">
                  <AvatarImage src={playerInfo?.image_url} />
                  <AvatarFallback className="font-bold">{playerInfo?.username?.[0]?.toUpperCase()}</AvatarFallback>
                </Avatar>
                <div>
                  <div className="flex items-center gap-2">
                    <p className="text-xl font-bold">{playerInfo?.username}</p>
                    <span className="text-[10px] font-bold px-1.5 py-0.5 rounded bg-primary/10 text-primary uppercase leading-none">
                      {platform.replace('_', '.')}
                    </span>
                  </div>
                  <p className="text-xs text-muted-foreground">{t("cacheStatus")}</p>
                </div>
              </div>
              <div className="flex gap-2">
                <Button variant="outline" size="sm" onClick={handleCancel} className="rounded-lg">
                  {t("changePlayer")}
                </Button>
              </div>
            </section>

            {isPolling ? (
              <aside aria-live="polite">
                <Alert className="bg-blue-50/50 dark:bg-blue-900/10 border-blue-200 dark:border-blue-900/50 animate-pulse">
                  <RefreshCw className="h-4 w-4 animate-spin text-blue-500" />
                  <AlertTitle className="text-blue-700 dark:text-blue-400 font-semibold">{t("bgSyncTitle")}</AlertTitle>
                  <AlertDescription className="text-blue-600 dark:text-blue-500">
                    {t("bgSyncDescription")}
                  </AlertDescription>
                </Alert>
              </aside>
            ) : null}

            <section aria-labelledby="filter-title">
              <h2 id="filter-title" className="sr-only">{tCommon("filter")}</h2>
              <OpeningFilter
                colorFilter={colorFilter}
                onColorFilterChange={setColorFilter}
                sortBy={sortBy}
                onSortByChange={setSortBy}
                minGames={minGames}
                onMinGamesChange={setMinGames}
                maxGames={maxGames}
                onMaxGamesChange={setMaxGames}
                allOpenings={allOpenings}
                epdMap={epdMap}
                search={search}
                onSearchChange={setSearch}
              />
            </section>

            {currentSummary && (
              <aside aria-labelledby="summary-title">
                <h2 id="summary-title" className="sr-only">{t("overallStats")}</h2>
                <OpeningSummary summary={currentSummary} colorFilter={colorFilter} />
              </aside>
            )}
            
            <section aria-labelledby="grid-title">
              <h2 id="grid-title" className="sr-only">{t("exploreTitle")}</h2>
              <OpeningGrid stats={filteredAndSortedOpenings} />
            </section>
          </div>
        )}
      </main>
    </div>
  )
}
