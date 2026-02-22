import { AxiosError } from "axios"
import { fetchSyncStatus, syncAccount } from "@/lib/api/api"

function delay(ms: number): Promise<void> {
  return new Promise((resolve) => setTimeout(resolve, ms))
}

export interface RunSyncFlowOptions {
  pollIntervalMs?: number
  maxPollCount?: number
}

export async function runSyncFlow(options?: RunSyncFlowOptions): Promise<void> {
  const pollIntervalMs = options?.pollIntervalMs ?? 3000
  const maxPollCount = options?.maxPollCount ?? 100

  const initialStatus = await fetchSyncStatus()

  if (!initialStatus.running) {
    try {
      await syncAccount()
    } catch (err) {
      if (!(err instanceof AxiosError) || err.response?.status !== 409) {
        throw err
      }
    }
  }

  for (let pollCount = 0; pollCount < maxPollCount; pollCount += 1) {
    await delay(pollIntervalMs)
    const status = await fetchSyncStatus()
    if (!status.running) {
      return
    }
  }

  throw new Error("Sync status polling timed out")
}
