package com.chessopeningstats.backend.application.usecase.provideStat;

import com.chessopeningstats.backend.application.domain.AccountService;
import com.chessopeningstats.backend.application.usecase.provideStat.dto.OpeningStat;
import com.chessopeningstats.backend.application.usecase.provideStat.dto.OpeningStatsResponse;
import com.chessopeningstats.backend.application.usecase.provideStat.dto.SummaryResponse;
import com.chessopeningstats.backend.application.usecase.provideStat.dto.WinRate;
import com.chessopeningstats.backend.domain.GamePlayerColor;
import com.chessopeningstats.backend.infra.repository.GamePlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatProvideService {
    private final GamePlayerRepository gamePlayerRepository;
    private final AccountService accountService;

    public OpeningStatsResponse getAllOpeningStats() {
        return OpeningStatsResponse.of(gamePlayerRepository.getAllOpeningStats());
    }

    public OpeningStatsResponse getAccountOpeningStats(long accountId) {
        return OpeningStatsResponse.of(gamePlayerRepository.getAccountOpeningStats(accountId));
    }

    public SummaryResponse getAllSummaries() {
        String nickname = "All Players Stat";
        List<WinRate> winrates = new ArrayList<>(gamePlayerRepository.getAllWinRate());

        List<OpeningStat> bestWinRateOpeningStats = new ArrayList<>();
        bestWinRateOpeningStats.addAll(getAllTop3BestWinRateOpeningStats(GamePlayerColor.WHITE));
        bestWinRateOpeningStats.addAll(getAllTop3BestWinRateOpeningStats(GamePlayerColor.BLACK));

        List<OpeningStat> mostPlayedOpeningStats = new ArrayList<>();
        mostPlayedOpeningStats.addAll(getAllTop3MostPlayedOpeningStats(GamePlayerColor.WHITE));
        mostPlayedOpeningStats.addAll(getAllTop3MostPlayedOpeningStats(GamePlayerColor.BLACK));

        return SummaryResponse.of(nickname, winrates, bestWinRateOpeningStats, mostPlayedOpeningStats);
    }

    public SummaryResponse getAccountSummaries(long accountId){
        String nickname = accountService.getAccount(accountId).getNickname();

        List<WinRate> winrates = new ArrayList<>(gamePlayerRepository.getAccountWinRate(accountId));

        List<OpeningStat> bestWinRateOpeningStats = new ArrayList<>();
        bestWinRateOpeningStats.addAll(getAccountTop3BestWinRateOpeningStats(accountId, GamePlayerColor.WHITE));
        bestWinRateOpeningStats.addAll(getAccountTop3BestWinRateOpeningStats(accountId, GamePlayerColor.BLACK));

        List<OpeningStat> mostPlayedOpeningStats = new ArrayList<>();
        mostPlayedOpeningStats.addAll(getAccountTop3MostPlayedOpeningStats(accountId, GamePlayerColor.WHITE));
        mostPlayedOpeningStats.addAll(getAccountTop3MostPlayedOpeningStats(accountId, GamePlayerColor.BLACK));

        return SummaryResponse.of(nickname, winrates, bestWinRateOpeningStats, mostPlayedOpeningStats);
    }

    private List<OpeningStat> getAccountTop3BestWinRateOpeningStats(long accountId, GamePlayerColor color) {
        return gamePlayerRepository.getAccountBestWinRateOpeningStats(accountId, color, PageRequest.of(0, 3));

    }

    private List<OpeningStat> getAccountTop3MostPlayedOpeningStats(long accountId, GamePlayerColor color) {
        return gamePlayerRepository.getAccountMostPlayedOpeningStats(accountId, color, PageRequest.of(0, 3));
    }

    private List<OpeningStat> getAllTop3BestWinRateOpeningStats(GamePlayerColor color) {
        return gamePlayerRepository.getAllBestWinRateOpeningStats(color, PageRequest.of(0, 3));

    }

    private List<OpeningStat> getAllTop3MostPlayedOpeningStats(GamePlayerColor color) {
        return gamePlayerRepository.getAllMostPlayedOpeningStats(color, PageRequest.of(0, 3));
    }
}
