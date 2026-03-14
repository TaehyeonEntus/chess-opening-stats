package com.chessopeningstats.backend.usecase;

import com.chessopeningstats.backend.domain.Color;
import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.cache.DashboardCache;
import com.chessopeningstats.backend.service.syncgame.dto.ColorDashboard;
import com.chessopeningstats.backend.service.syncgame.dto.ColorRecord;
import com.chessopeningstats.backend.service.syncgame.dto.Dashboard;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GetDashboardUseCaseTest {

    @Mock
    private DashboardCache dashboardCache;

    @InjectMocks
    private GetDashboardUseCase getDashboardUseCase;

    @Test
    void getDashboard_returnsDashboard_fromCache() {
        // given
        Player player = Player.of(Platform.CHESS_COM, "testuser");
        Dashboard dashboard = new Dashboard(
                new ColorDashboard(new ColorRecord(Color.WHITE, 1, 0, 0), List.of(), List.of(), List.of()),
                new ColorDashboard(new ColorRecord(Color.BLACK, 0, 0, 1), List.of(), List.of(), List.of())
        );
        given(dashboardCache.get(player)).willReturn(dashboard);

        // when
        Dashboard result = getDashboardUseCase.getDashboard(player);

        // then
        assertThat(result).isEqualTo(dashboard);
    }
}
