import type {OpeningStatView, SummaryResponse} from "@/lib/types";
import {fetchAllOpeningResult, fetchSummary} from "@/lib/api/api";
import {adaptOpeningResult} from "@/lib/provide/internel/adapter";

export function provideAllOpeningResult(): Promise<OpeningStatView[]> {
    return fetchAllOpeningResult().then(adaptOpeningResult);
}

export function provideSummary(): Promise<SummaryResponse> {
    return fetchSummary();
}
