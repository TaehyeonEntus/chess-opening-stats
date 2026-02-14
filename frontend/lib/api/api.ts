import type { OpeningResult } from "../types"
import {apiClient} from "@/lib/api/apiClient";
export function fetchAllOpeningResult(): Promise<OpeningResult[]> {
  return apiClient.get(`/api/stat/all`)
}