import type { HomeView } from "@/lib/types";
import { addPlayer, fetchHomeView, syncAccount, checkPlayerExists } from "@/lib/api/api";
import type { AddPlayerRequest, PlayerInfo } from "@/lib/api/api";

export function provideHomeView(): Promise<HomeView> {
    return fetchHomeView();
}

export function provideSyncAccount(): Promise<{ waiting: number }> {
    return syncAccount();
}

export function provideAddPlayer(request: AddPlayerRequest): Promise<void> {
    return addPlayer(request);
}

export function provideCheckPlayerExists(platform: string, username: string): Promise<PlayerInfo> {
    return checkPlayerExists(platform, username);
}
