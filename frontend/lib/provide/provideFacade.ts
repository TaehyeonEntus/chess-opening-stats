import type { HomeView } from "@/lib/types";
import { addPlayer, fetchHomeView, syncAccount } from "@/lib/api/api";
import type { AddPlayerRequest } from "@/lib/api/api";

export function provideHomeView(): Promise<HomeView> {
    return fetchHomeView();
}

export function provideSyncAccount(): Promise<{ waiting: number }> {
    return syncAccount();
}

export function provideAddPlayer(request: AddPlayerRequest): Promise<void> {
    return addPlayer(request);
}
