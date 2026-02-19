package com.chessopeningstats.backend.application.usecase.provideStat;

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

    public OpeningStatsResponse getAllOpeningStats() {
        return OpeningStatsResponse.of(gamePlayerRepository.getAllOpeningStats());
    }

    public OpeningStatsResponse getAccountOpeningStats(long accountId) {
        return OpeningStatsResponse.of(gamePlayerRepository.getAccountOpeningStats(accountId));
    }

    public SummaryResponse getAllSummary() {
        List<WinRate> winrates = new ArrayList<>(gamePlayerRepository.getAllWinRate());

        List<OpeningStat> bestWinRateOpeningStats = new ArrayList<>();
        bestWinRateOpeningStats.addAll(getTop3BestWinRateOpeningStats(GamePlayerColor.WHITE));
        bestWinRateOpeningStats.addAll(getTop3BestWinRateOpeningStats(GamePlayerColor.BLACK));

        List<OpeningStat> mostPlayedOpeningStats = new ArrayList<>();
        mostPlayedOpeningStats.addAll(getTop3MostPlayedOpeningStats(GamePlayerColor.WHITE));
        mostPlayedOpeningStats.addAll(getTop3MostPlayedOpeningStats(GamePlayerColor.BLACK));

        return SummaryResponse.of(winrates, bestWinRateOpeningStats, mostPlayedOpeningStats);
    }

    public SummaryResponse getAccountSummaries(long accountId){
        //todo
        return null;
    }

    private List<OpeningStat> getTop3BestWinRateOpeningStats(GamePlayerColor color) {
        return gamePlayerRepository.getAllBestWinRateOpeningStats(color, PageRequest.of(0, 3));

    }

    private List<OpeningStat> getTop3MostPlayedOpeningStats(GamePlayerColor color) {
        return gamePlayerRepository.getAllMostPlayedOpeningStats(color, PageRequest.of(0, 3));
    }
}
