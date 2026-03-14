package com.chessopeningstats.backend.usecase;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.exception.UsernameNotFoundOnPlatformException;
import com.chessopeningstats.backend.infra.client.playerexistence.dto.PlayerExistenceDto;
import com.chessopeningstats.backend.service.playerexistence.PlayerExistenceService;
import com.chessopeningstats.backend.service.playerexistence.registry.PlayerExistenceServiceRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ExistsPlayerUseCaseTest {

    @Mock
    private PlayerExistenceServiceRegistry playerExistenceServiceRegistry;

    @Mock
    private PlayerExistenceService playerExistenceService;

    @InjectMocks
    private ExistsPlayerUseCase existsPlayerUseCase;

    @Test
    void existsPlayer_returnsDto_whenPlayerExists() {
        // given
        Player player = Player.of(Platform.CHESS_COM, "testuser");
        PlayerExistenceDto expectedDto = new PlayerExistenceDto("image", 123L);
        given(playerExistenceServiceRegistry.getService(Platform.CHESS_COM)).willReturn(playerExistenceService);
        given(playerExistenceService.existsUsername("testuser")).willReturn(expectedDto);

        // when
        PlayerExistenceDto result = existsPlayerUseCase.existsPlayer(player);

        // then
        assertThat(result.getImage_url()).isEqualTo("image");
        assertThat(result.getLast_online()).isEqualTo(123L);
    }

    @Test
    void existsPlayer_throwsException_whenPlayerDoesNotExist() {
        // given
        Player player = Player.of(Platform.CHESS_COM, "nonexistent");
        given(playerExistenceServiceRegistry.getService(Platform.CHESS_COM)).willReturn(playerExistenceService);
        given(playerExistenceService.existsUsername("nonexistent")).willThrow(new UsernameNotFoundOnPlatformException("nonexistent", Platform.CHESS_COM));

        // then
        assertThatThrownBy(() -> existsPlayerUseCase.existsPlayer(player))
                .isInstanceOf(UsernameNotFoundOnPlatformException.class);
    }
}
