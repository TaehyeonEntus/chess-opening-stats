package com.chessopeningstats.backend.web.dashboard;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.exception.DashboardNotFoundException;
import com.chessopeningstats.backend.service.playerdashboard.dto.ColorDashboard;
import com.chessopeningstats.backend.service.playerdashboard.dto.Dashboard;
import com.chessopeningstats.backend.service.playerdashboard.dto.Stat;
import com.chessopeningstats.backend.service.playerdashboard.impl.DashboardCacheService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DashboardController.class)
@AutoConfigureRestDocs
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardCacheService dashboardCacheService;

    @Test
    @DisplayName("대시보드 조회 성공 (200)")
    void getDashboardSuccess() throws Exception {
        Dashboard dashboard = new Dashboard(
                new ColorDashboard(new Stat(10, 2, 3), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()),
                new ColorDashboard(new Stat(8, 1, 6), Collections.emptyList(), Collections.emptyList(), Collections.emptyList())
        );
        given(dashboardCacheService.get(any(Player.class))).willReturn(dashboard);

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
                                fieldWithPath("white.stat.win").description("승리 횟수"),
                                fieldWithPath("white.stat.draw").description("무승부 횟수"),
                                fieldWithPath("white.stat.lose").description("패배 횟수"),
                                fieldWithPath("white.mostPlayedOpenings").description("가장 많이 플레이한 오프닝 목록"),
                                fieldWithPath("white.highestWinRateOpenings").description("승률이 가장 높은 오프닝 목록"),
                                fieldWithPath("white.openings").description("전체 오프닝 통계 목록"),
                                fieldWithPath("black.stat.win").description("승리 횟수"),
                                fieldWithPath("black.stat.draw").description("무승부 횟수"),
                                fieldWithPath("black.stat.lose").description("패배 횟수"),
                                fieldWithPath("black.mostPlayedOpenings").description("가장 많이 플레이한 오프닝 목록"),
                                fieldWithPath("black.highestWinRateOpenings").description("승률이 가장 높은 오프닝 목록"),
                                fieldWithPath("black.openings").description("전체 오프닝 통계 목록")
                        )
                ));
    }

    @Test
    @DisplayName("대시보드 조회 실패 - 데이터 없음 (404)")
    void getDashboardNotFound() throws Exception {
        given(dashboardCacheService.get(any(Player.class))).willThrow(new DashboardNotFoundException());

        mockMvc.perform(get("/dashboard")
                        .param("platform", "CHESS_COM")
                        .param("username", "testuser"))
                .andExpect(status().isNotFound())
                .andDo(document("get-dashboard-not-found"));
    }
}
