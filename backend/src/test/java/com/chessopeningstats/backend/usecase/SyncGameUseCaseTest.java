package com.chessopeningstats.backend.usecase;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.cache.DashboardCache;
import com.chessopeningstats.backend.infra.queue.QueueRouter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SyncGameUseCaseTest {

    @Mock
    private DashboardCache dashboardCache;

    @Mock
    private QueueRouter queueRouter;

    @InjectMocks
    private SyncGameUseCase syncGameUseCase;

    @Test
    void syncGame_addsToQueue_whenCacheNotContainsPlayer() {
        // given
        Player player = Player.of(Platform.CHESS_COM, "testuser");
        given(dashboardCache.contains(player)).willReturn(false);

        // when
        syncGameUseCase.syncGame(player);

        // then
        verify(queueRouter, times(1)).add(player);
    }

    @Test
    void syncGame_doesNotAddToQueue_whenCacheContainsPlayer() {
        // given
        Player player = Player.of(Platform.CHESS_COM, "testuser");
        given(dashboardCache.contains(player)).willReturn(true);

        // when
        syncGameUseCase.syncGame(player);

        // then
        verify(queueRouter, never()).add(player);
    }
}
