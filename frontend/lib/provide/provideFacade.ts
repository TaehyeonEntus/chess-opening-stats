import type {OpeningStatView} from "@/lib/types";
import {fetchAllOpeningResult} from "@/lib/api/api";
import {adaptOpeningResult} from "@/lib/provide/internel/adapter";

export function provideAllOpeningResult(): Promise<OpeningStatView[]> {
    return fetchAllOpeningResult().then(adaptOpeningResult);
}