import { AxiosError } from "axios"
import { syncAccount } from "@/lib/api/api"

export interface RunSyncFlowOptions {
  pollIntervalMs?: number
  maxPollCount?: number
}

export async function runSyncFlow(options?: RunSyncFlowOptions): Promise<{ waiting: number }> {
  // fetchSyncStatus is no longer supported by the backend ABI.
  // We assume syncAccount is fully synchronous or fires and forgets.
  try {
    return await syncAccount()
  } catch (err) {
    if (err instanceof AxiosError && err.response?.status === 409) {
      console.warn("Sync conflict (already running or invalid state)", err)
    }
    throw err
  }
}
