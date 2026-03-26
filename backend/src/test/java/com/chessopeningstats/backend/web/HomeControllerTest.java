package com.chessopeningstats.backend.web;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.cache.DashboardCache;
import com.chessopeningstats.backend.infra.repository.EmitterRepository;
import com.chessopeningstats.backend.usecase.ExistsPlayerUseCase;
import com.chessopeningstats.backend.usecase.SyncPlayerUseCase;
import com.chessopeningstats.backend.web.dto.PlayerExistenceResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HomeController.class)
@AutoConfigureRestDocs
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExistsPlayerUseCase existsPlayerUseCase;

    @MockitoBean
    private SyncPlayerUseCase syncPlayerUseCase;

    @MockitoBean
    private EmitterRepository emitterRepository;

    @MockitoBean
    private DashboardCache dashboardCache;

    @Test
    void existsPlayer() throws Exception {
        given(existsPlayerUseCase.existsPlayer(any(Player.class))).willReturn(new PlayerExistenceResponse("image", 123L));

        mockMvc.perform(get("/player")
                        .param("platform", "CHESS_COM")
                        .param("username", "testuser"))
                .andExpect(status().isOk())
                .andDo(document("exists-player",
                        queryParameters(
                                parameterWithName("platform").description("체스 플랫폼 (CHESS_COM, LICHESS)"),
                                parameterWithName("username").description("플레이어 사용자 이름")
                        ),
                        responseFields(
                                fieldWithPath("image_url").description("플레이어 이미지 URL"),
                                fieldWithPath("last_online").description("마지막 온라인 시간")
                        )
                ));
    }

    @Test
    void syncGames() throws Exception {
        mockMvc.perform(get("/sync")
                        .queryParam("platform", "CHESS_COM")
                        .queryParam("username", "testuser"))
                .andExpect(status().isOk())
                .andDo(document("sync-games",
                        queryParameters(
                                parameterWithName("platform").description("체스 플랫폼 (CHESS_COM, LICHESS)"),
                                parameterWithName("username").description("플레이어 사용자 이름")
                        )
                ));
    }
}
