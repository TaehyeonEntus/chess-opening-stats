package com.chessopeningstats.backend.usecase;

import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.domain.GamePlayerColor;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.service.AccountPlayerService;
import com.chessopeningstats.backend.service.AccountService;
import com.chessopeningstats.backend.service.GameService;
import com.chessopeningstats.backend.service.PlayerService;
import com.chessopeningstats.backend.web.view.dto.home.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetHomeViewUseCase {
    private final ExecutorService virtualThreadPool;
    private final AccountService accountService;
    private final PlayerService playerService;
    private final GameService gameService;
    private final AccountPlayerService accountPlayerService;

    public HomeView getHomeView(long accountId) {
        Account account = accountService.get(accountId);
        List<Player> players = accountPlayerService.getPlayersByAccountId(accountId);
        List<Long> playerIds = players.stream().map(Player::getId).toList();

        AccountSummary accountSummary = new AccountSummary(
                account.getId(),
                account.getNickname(),
                account.getLastSyncedAt()
        );

        List<PlayerSummary> playerSummaries = players.stream()
                .map(player -> new PlayerSummary(
                        player.getId(),
                        player.getUsername(),
                        player.getPlatform(),
                        player.getLastPlayedAt())
                )
                .toList();

        CompletableFuture<Map<GamePlayerColor, List<ColorOpeningStat>>> futureOpeningStatMap =
                CompletableFuture.supplyAsync(
                        () -> gameService.getOpeningStats(playerIds),
                        virtualThreadPool
                );

        Map<GamePlayerColor, ColorRecord> recordMap =
                playerService.getRecordsByPlayerIds(playerIds).stream()
                        .collect(Collectors.toMap(ColorRecord::color, record -> record));

        ColorRecord whiteColorRecords = recordMap.get(GamePlayerColor.WHITE);
        ColorRecord blackColorRecords = recordMap.get(GamePlayerColor.BLACK);

        List<ColorOpeningStat> whiteMostPlayedOpeningStats = gameService.getMostPlayedOpeningStats(playerIds, GamePlayerColor.WHITE);
        List<ColorOpeningStat> blackMostPlayedOpeningStats = gameService.getMostPlayedOpeningStats(playerIds, GamePlayerColor.BLACK);

        List<ColorOpeningStat> whiteHighestWinRateOpeningStats = gameService.getHighestWinRateOpeningStats(playerIds, GamePlayerColor.WHITE);
        List<ColorOpeningStat> blackHighestWinRateOpeningStats = gameService.getHighestWinRateOpeningStats(playerIds, GamePlayerColor.BLACK);

        Map<GamePlayerColor, List<ColorOpeningStat>> openingStatMap = futureOpeningStatMap.join();

        List<ColorOpeningStat> whiteOpeningStats = openingStatMap.get(GamePlayerColor.WHITE);
        List<ColorOpeningStat> blackOpeningStats = openingStatMap.get(GamePlayerColor.BLACK);

        ColorDashboard whiteOpeningsView = new ColorDashboard(
                whiteColorRecords,
                whiteMostPlayedOpeningStats,
                whiteHighestWinRateOpeningStats,
                whiteOpeningStats
        );

        ColorDashboard blackOpeningsView = new ColorDashboard(
                blackColorRecords,
                blackMostPlayedOpeningStats,
                blackHighestWinRateOpeningStats,
                blackOpeningStats
        );

        return new HomeView(accountSummary, playerSummaries, whiteOpeningsView, blackOpeningsView);
    }
}
