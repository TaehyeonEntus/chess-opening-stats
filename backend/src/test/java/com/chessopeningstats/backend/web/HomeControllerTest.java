package com.chessopeningstats.backend.web;

import com.chessopeningstats.backend.domain.Color;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.service.syncgame.dto.ColorDashboard;
import com.chessopeningstats.backend.service.syncgame.dto.ColorOpeningStat;
import com.chessopeningstats.backend.service.syncgame.dto.ColorRecord;
import com.chessopeningstats.backend.service.syncgame.dto.Dashboard;
import com.chessopeningstats.backend.usecase.ExistsPlayerUseCase;
import com.chessopeningstats.backend.usecase.GetDashboardUseCase;
import com.chessopeningstats.backend.usecase.SyncGameUseCase;
import com.chessopeningstats.backend.web.dto.PlayerExistenceResponse;
import com.chessopeningstats.backend.web.dto.SyncGameResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
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
    private GetDashboardUseCase getDashboardUseCase;

    @MockitoBean
    private ExistsPlayerUseCase existsPlayerUseCase;

    @MockitoBean
    private SyncGameUseCase syncGameUseCase;

    @Test
    void getDashboard() throws Exception {
        Dashboard dashboard = new Dashboard(
                new ColorDashboard(
                        new ColorRecord(Color.WHITE, 10, 2, 3),
                        List.of(new ColorOpeningStat(1L, Color.WHITE, 5, 1, 0)),
                        List.of(new ColorOpeningStat(1L, Color.WHITE, 5, 1, 0)),
                        List.of(new ColorOpeningStat(1L, Color.WHITE, 5, 1, 0))
                ),
                new ColorDashboard(
                        new ColorRecord(Color.BLACK, 8, 1, 6),
                        List.of(new ColorOpeningStat(2L, Color.BLACK, 4, 0, 2)),
                        List.of(new ColorOpeningStat(2L, Color.BLACK, 4, 0, 2)),
                        List.of(new ColorOpeningStat(2L, Color.BLACK, 4, 0, 2))
                )
        );

        given(getDashboardUseCase.getDashboard(any(Player.class))).willReturn(dashboard);

        mockMvc.perform(get("/dashboard")
                        .param("platform", "CHESS_COM")
                        .param("username", "testuser"))
                .andExpect(status().isOk())
                .andDo(document("get-dashboard",
                        queryParameters(
                                parameterWithName("platform").description("체스 플랫폼 (CHESS_COM, LICHESS)"),
                                parameterWithName("username").description("플레이어 사용자 이름")
                        ),
                        responseFields(
                                fieldWithPath("white.record.color").description("기물 색상"),
                                fieldWithPath("white.record.win").description("승리 횟수"),
                                fieldWithPath("white.record.draw").description("무승부 횟수"),
                                fieldWithPath("white.record.lose").description("패배 횟수"),
                                fieldWithPath("white.mostPlayedOpenings[].openingId").description("오프닝 ID"),
                                fieldWithPath("white.mostPlayedOpenings[].color").description("기물 색상"),
                                fieldWithPath("white.mostPlayedOpenings[].win").description("승리 횟수"),
                                fieldWithPath("white.mostPlayedOpenings[].draw").description("무승부 횟수"),
                                fieldWithPath("white.mostPlayedOpenings[].lose").description("패배 횟수"),
                                fieldWithPath("white.highestWinRateOpenings[].openingId").description("오프닝 ID"),
                                fieldWithPath("white.highestWinRateOpenings[].color").description("기물 색상"),
                                fieldWithPath("white.highestWinRateOpenings[].win").description("승리 횟수"),
                                fieldWithPath("white.highestWinRateOpenings[].draw").description("무승부 횟수"),
                                fieldWithPath("white.highestWinRateOpenings[].lose").description("패배 횟수"),
                                fieldWithPath("white.openings[].openingId").description("오프닝 ID"),
                                fieldWithPath("white.openings[].color").description("기물 색상"),
                                fieldWithPath("white.openings[].win").description("승리 횟수"),
                                fieldWithPath("white.openings[].draw").description("무승부 횟수"),
                                fieldWithPath("white.openings[].lose").description("패배 횟수"),
                                fieldWithPath("black.record.color").description("기물 색상"),
                                fieldWithPath("black.record.win").description("승리 횟수"),
                                fieldWithPath("black.record.draw").description("무승부 횟수"),
                                fieldWithPath("black.record.lose").description("패배 횟수"),
                                fieldWithPath("black.mostPlayedOpenings[].openingId").description("오프닝 ID"),
                                fieldWithPath("black.mostPlayedOpenings[].color").description("기물 색상"),
                                fieldWithPath("black.mostPlayedOpenings[].win").description("승리 횟수"),
                                fieldWithPath("black.mostPlayedOpenings[].draw").description("무승부 횟수"),
                                fieldWithPath("black.mostPlayedOpenings[].lose").description("패배 횟수"),
                                fieldWithPath("black.highestWinRateOpenings[].openingId").description("오프닝 ID"),
                                fieldWithPath("black.highestWinRateOpenings[].color").description("기물 색상"),
                                fieldWithPath("black.highestWinRateOpenings[].win").description("승리 횟수"),
                                fieldWithPath("black.highestWinRateOpenings[].draw").description("무승부 횟수"),
                                fieldWithPath("black.highestWinRateOpenings[].lose").description("패배 횟수"),
                                fieldWithPath("black.openings[].openingId").description("오프닝 ID"),
                                fieldWithPath("black.openings[].color").description("기물 색상"),
                                fieldWithPath("black.openings[].win").description("승리 횟수"),
                                fieldWithPath("black.openings[].draw").description("무승부 횟수"),
                                fieldWithPath("black.openings[].lose").description("패배 횟수")
                        )
                ));
    }

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
        given(syncGameUseCase.syncGame(any(Player.class))).willReturn(new SyncGameResponse(1));

        mockMvc.perform(post("/sync")
                        .queryParam("platform", "CHESS_COM")
                        .queryParam("username", "testuser"))
                .andExpect(status().isOk())
                .andDo(document("sync-games",
                        queryParameters(
                                parameterWithName("platform").description("체스 플랫폼 (CHESS_COM, LICHESS)"),
                                parameterWithName("username").description("플레이어 사용자 이름")
                        ),
                        responseFields(
                                fieldWithPath("waiting").description("큐에 대기중인 플레이어 수")
                        )
                ));
    }
}
