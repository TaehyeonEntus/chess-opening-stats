interface WinRateBarProps {
  winRate: number
  drawRate: number
  lossRate: number
  size?: "sm" | "md"
}

export function WinRateBar({ winRate, drawRate, lossRate, size = "sm" }: WinRateBarProps) {
  const height = size === "sm" ? "h-2" : "h-3"

  return (
    <div className={`flex w-full overflow-hidden rounded-full ${height}`}>
      {winRate > 0 && (
        <div
          className="bg-emerald-500 transition-all duration-300"
          style={{ width: `${winRate}%` }}
          title={`Win: ${winRate}%`}
        />
      )}
      {drawRate > 0 && (
        <div
          className="bg-amber-400 transition-all duration-300"
          style={{ width: `${drawRate}%` }}
          title={`Draw: ${drawRate}%`}
        />
      )}
      {lossRate > 0 && (
        <div
          className="bg-rose-500 transition-all duration-300"
          style={{ width: `${lossRate}%` }}
          title={`Loss: ${lossRate}%`}
        />
      )}
    </div>
  )
}
