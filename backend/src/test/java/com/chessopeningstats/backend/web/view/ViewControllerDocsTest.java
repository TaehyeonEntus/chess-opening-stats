package com.chessopeningstats.backend.web.view;

import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.domain.GamePlayerColor;
import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.security.CustomUserDetails;
import com.chessopeningstats.backend.support.RestDocsSupport;
import com.chessopeningstats.backend.support.TestSecurityConfig;
import com.chessopeningstats.backend.usecase.GetHomeViewUseCase;
import com.chessopeningstats.backend.web.view.dto.home.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ViewController.class)
@Import(TestSecurityConfig.class)
public class ViewControllerDocsTest extends RestDocsSupport {

    @MockBean private GetHomeViewUseCase getHomeViewUseCase;

    private CustomUserDetails userDetails;

    @BeforeEach
    void init() {
        Account account = Account.builder()
                .id(1L)
                .username("testuser")
                .nickname("testnickname")
                .build();
        userDetails = new CustomUserDetails(account, List.of());
    }

    @Test
    @DisplayName("홈 뷰 조회 API")
    void getHomeView() throws Exception {
        AccountSummary accountSummary = new AccountSummary(1L, "testnickname", Instant.now());
        PlayerSummary playerSummary = new PlayerSummary(1L, "chessPlayer", Platform.CHESS_COM, Instant.now());
        
        ColorRecord recordWhite = new ColorRecord(GamePlayerColor.WHITE, 5, 2, 3);
        ColorRecord recordBlack = new ColorRecord(GamePlayerColor.BLACK, 4, 1, 5);
        ColorOpeningStat openingStat = new ColorOpeningStat(GamePlayerColor.WHITE, 1L, 3, 1, 1);
        
        ColorDashboard whiteDashboard = new ColorDashboard(
                recordWhite,
                List.of(openingStat),
                List.of(openingStat),
                List.of(openingStat)
        );

        ColorDashboard blackDashboard = new ColorDashboard(
                recordBlack,
                List.of(openingStat),
                List.of(openingStat),
                List.of(openingStat)
        );

        HomeView response = new HomeView(
                accountSummary,
                List.of(playerSummary),
                whiteDashboard,
                blackDashboard
        );

        given(getHomeViewUseCase.getHomeView(anyLong())).willReturn(response);

        mockMvc.perform(
                get("/views/home")
                        .with(user(userDetails))
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andDo(document("view-home",
                        responseFields(
                                fieldWithPath("account").type(JsonFieldType.OBJECT).description("계정 정보"),
                                fieldWithPath("account.id").type(JsonFieldType.NUMBER).description("계정 고유 ID"),
                                fieldWithPath("account.nickname").type(JsonFieldType.STRING).description("계정 닉네임"),
                                fieldWithPath("account.lastSyncedAt").type(JsonFieldType.STRING).description("마지막 동기화 시각").optional(),

                                fieldWithPath("players").type(JsonFieldType.ARRAY).description("연동된 플레이어 목록"),
                                fieldWithPath("players[].id").type(JsonFieldType.NUMBER).description("플레이어 고유 ID"),
                                fieldWithPath("players[].username").type(JsonFieldType.STRING).description("플랫폼 아이디"),
                                fieldWithPath("players[].platform").type(JsonFieldType.STRING).description("플랫폼"),
                                fieldWithPath("players[].lastPlayedAt").type(JsonFieldType.STRING).description("마지막 플레이 시각").optional(),

                                // White Dashboard
                                fieldWithPath("white").type(JsonFieldType.OBJECT).description("백 피스 대시보드"),
                                fieldWithPath("white.record").type(JsonFieldType.OBJECT).description("백 피스 전적"),
                                fieldWithPath("white.record.color").type(JsonFieldType.STRING).description("색상(WHITE, BLACK)"),
                                fieldWithPath("white.record.win").type(JsonFieldType.NUMBER).description("승리 수"),
                                fieldWithPath("white.record.draw").type(JsonFieldType.NUMBER).description("무승부 수"),
                                fieldWithPath("white.record.lose").type(JsonFieldType.NUMBER).description("패배 수"),
                                
                                fieldWithPath("white.mostPlayedOpenings").type(JsonFieldType.ARRAY).description("가장 많이 플레이한 오프닝"),
                                fieldWithPath("white.mostPlayedOpenings[].color").type(JsonFieldType.STRING).description("색상"),
                                fieldWithPath("white.mostPlayedOpenings[].id").type(JsonFieldType.NUMBER).description("오프닝 ID"),
                                fieldWithPath("white.mostPlayedOpenings[].win").type(JsonFieldType.NUMBER).description("승리 수"),
                                fieldWithPath("white.mostPlayedOpenings[].draw").type(JsonFieldType.NUMBER).description("무승부 수"),
                                fieldWithPath("white.mostPlayedOpenings[].lose").type(JsonFieldType.NUMBER).description("패배 수"),

                                fieldWithPath("white.highestWinRateOpenings").type(JsonFieldType.ARRAY).description("승률이 가장 높은 오프닝"),
                                fieldWithPath("white.highestWinRateOpenings[].color").type(JsonFieldType.STRING).description("색상"),
                                fieldWithPath("white.highestWinRateOpenings[].id").type(JsonFieldType.NUMBER).description("오프닝 ID"),
                                fieldWithPath("white.highestWinRateOpenings[].win").type(JsonFieldType.NUMBER).description("승리 수"),
                                fieldWithPath("white.highestWinRateOpenings[].draw").type(JsonFieldType.NUMBER).description("무승부 수"),
                                fieldWithPath("white.highestWinRateOpenings[].lose").type(JsonFieldType.NUMBER).description("패배 수"),

                                fieldWithPath("white.openings").type(JsonFieldType.ARRAY).description("전체 오프닝 통계"),
                                fieldWithPath("white.openings[].color").type(JsonFieldType.STRING).description("색상"),
                                fieldWithPath("white.openings[].id").type(JsonFieldType.NUMBER).description("오프닝 ID"),
                                fieldWithPath("white.openings[].win").type(JsonFieldType.NUMBER).description("승리 수"),
                                fieldWithPath("white.openings[].draw").type(JsonFieldType.NUMBER).description("무승부 수"),
                                fieldWithPath("white.openings[].lose").type(JsonFieldType.NUMBER).description("패배 수"),

                                // Black Dashboard
                                fieldWithPath("black").type(JsonFieldType.OBJECT).description("흑 피스 대시보드"),
                                fieldWithPath("black.record").type(JsonFieldType.OBJECT).description("흑 피스 전적"),
                                fieldWithPath("black.record.color").type(JsonFieldType.STRING).description("색상(WHITE, BLACK)"),
                                fieldWithPath("black.record.win").type(JsonFieldType.NUMBER).description("승리 수"),
                                fieldWithPath("black.record.draw").type(JsonFieldType.NUMBER).description("무승부 수"),
                                fieldWithPath("black.record.lose").type(JsonFieldType.NUMBER).description("패배 수"),

                                fieldWithPath("black.mostPlayedOpenings").type(JsonFieldType.ARRAY).description("가장 많이 플레이한 오프닝"),
                                fieldWithPath("black.mostPlayedOpenings[].color").type(JsonFieldType.STRING).description("색상"),
                                fieldWithPath("black.mostPlayedOpenings[].id").type(JsonFieldType.NUMBER).description("오프닝 ID"),
                                fieldWithPath("black.mostPlayedOpenings[].win").type(JsonFieldType.NUMBER).description("승리 수"),
                                fieldWithPath("black.mostPlayedOpenings[].draw").type(JsonFieldType.NUMBER).description("무승부 수"),
                                fieldWithPath("black.mostPlayedOpenings[].lose").type(JsonFieldType.NUMBER).description("패배 수"),

                                fieldWithPath("black.highestWinRateOpenings").type(JsonFieldType.ARRAY).description("승률이 가장 높은 오프닝"),
                                fieldWithPath("black.highestWinRateOpenings[].color").type(JsonFieldType.STRING).description("색상"),
                                fieldWithPath("black.highestWinRateOpenings[].id").type(JsonFieldType.NUMBER).description("오프닝 ID"),
                                fieldWithPath("black.highestWinRateOpenings[].win").type(JsonFieldType.NUMBER).description("승리 수"),
                                fieldWithPath("black.highestWinRateOpenings[].draw").type(JsonFieldType.NUMBER).description("무승부 수"),
                                fieldWithPath("black.highestWinRateOpenings[].lose").type(JsonFieldType.NUMBER).description("패배 수"),

                                fieldWithPath("black.openings").type(JsonFieldType.ARRAY).description("전체 오프닝 통계"),
                                fieldWithPath("black.openings[].color").type(JsonFieldType.STRING).description("색상"),
                                fieldWithPath("black.openings[].id").type(JsonFieldType.NUMBER).description("오프닝 ID"),
                                fieldWithPath("black.openings[].win").type(JsonFieldType.NUMBER).description("승리 수"),
                                fieldWithPath("black.openings[].draw").type(JsonFieldType.NUMBER).description("무승부 수"),
                                fieldWithPath("black.openings[].lose").type(JsonFieldType.NUMBER).description("패배 수")
                        )
                ));
    }
}
