package com.chessopeningstats.backend.service.syncgame;

import com.chessopeningstats.backend.service.syncgame.dto.AnalyzedGame;
import com.chessopeningstats.backend.service.syncgame.dto.PlayerDashboard;

import java.util.List;

public interface DashboardConvertService {
    PlayerDashboard convertDashboard(List<AnalyzedGame> analyzedGames);
}
