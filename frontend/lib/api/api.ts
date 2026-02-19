import type { OpeningResult, SummaryResponse, OpeningStatsResponse } from "@/lib/types"
import { apiClient } from "@/lib/api/apiClient"

export function fetchAllOpeningResult(): Promise<OpeningResult[]> {
  return apiClient.get<OpeningStatsResponse>("/api/stat/all").then(response => response.openingStats)
}

export function fetchSummary(): Promise<SummaryResponse> {
  return apiClient.get<SummaryResponse>("/api/stat/all/summary")
}
