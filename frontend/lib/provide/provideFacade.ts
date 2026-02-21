import type {OpeningStatView, SummaryResponse} from "@/lib/types";
import {addPlayer, fetchAllOpeningResult, fetchSummary, syncAccount} from "@/lib/api/api";
import type { AddPlayerRequest } from "@/lib/api/api";
import {adaptOpeningResult} from "@/lib/provide/internel/adapter";

export function provideAllOpeningResult(): Promise<OpeningStatView[]> {
    return fetchAllOpeningResult().then(adaptOpeningResult);
}

export function provideSummary(): Promise<SummaryResponse> {
    return fetchSummary();
}

export function provideSyncAccount(): Promise<void> {
    return syncAccount();
}

export function provideAddPlayer(request: AddPlayerRequest): Promise<string> {
    return addPlayer(request);
}
