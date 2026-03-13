package com.chessopeningstats.backend.web.account;

import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.exception.UsernameAlreadyExistsException;
import com.chessopeningstats.backend.security.CustomUserDetails;
import com.chessopeningstats.backend.support.RestDocsSupport;
import com.chessopeningstats.backend.support.TestSecurityConfig;
import com.chessopeningstats.backend.usecase.*;
import com.chessopeningstats.backend.web.account.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.doThrow;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@Import(TestSecurityConfig.class)
public class AccountControllerDocsTest extends RestDocsSupport {

    @MockBean private RegisterAccountUseCase registerAccountUseCase;
    @MockBean private WithdrawAccountUseCase withdrawAccountUseCase;
    @MockBean private GetAccountDetailUseCase getAccountDetailUseCase;
    @MockBean private GetPlayerSummariesUseCase getPlayerSummariesUseCase;
    @MockBean private LinkPlayerUseCase linkPlayerUseCase;
    @MockBean private UnlinkPlayerUseCase unlinkPlayerUseCase;
    @MockBean private ChangeAccountNicknameUseCase changeAccountNicknameUseCase;
    @MockBean private ChangeAccountPasswordUseCase changeAccountPasswordUseCase;
    @MockBean private SyncGameUseCase syncGameUseCase;

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
    @DisplayName("회원 가입 API")
    @WithMockUser
    void register() throws Exception {
        RegisterAccountRequest request = new RegisterAccountRequest(
                "testuser",
                "testnickname",
                "password123!",
                "password123!"
        );

        mockMvc.perform(
                post("/accounts/register")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
        )
                .andExpect(status().isOk())
                .andDo(document("account-register",
                        requestFields(
                                fieldWithPath("username").type(JsonFieldType.STRING).description("사용자 아이디"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("사용자 닉네임"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("사용자 비밀번호"),
                                fieldWithPath("passwordConfirm").type(JsonFieldType.STRING).description("사용자 비밀번호 확인")
                        ),
                        responseFields(
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지")
                        )
                ));
    }

    @Test
    @DisplayName("회원 가입 API - 중복 아이디 에러")
    void register_error_duplicate_username() throws Exception {
        RegisterAccountRequest request = new RegisterAccountRequest(
                "duplicateUser",
                "testnickname",
                "password123!",
                "password123!"
        );

        doThrow(new UsernameAlreadyExistsException())
                .when(registerAccountUseCase).register(any());

        mockMvc.perform(
                post("/accounts/register")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
        )
                .andExpect(status().isConflict())
                .andDo(document("account-register-error-duplicate",
                        responseFields(
                                fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지")
                        )
                ));
    }

    @Test
    @DisplayName("내 정보 조회 API")
    void me() throws Exception {
        AccountDetail response = new AccountDetail(1L, "testuser", "testnickname", Instant.now());
        given(getAccountDetailUseCase.getAccountDetail(anyLong())).willReturn(response);

        mockMvc.perform(
                get("/accounts/me")
                        .with(user(userDetails))
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andDo(document("account-me",
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("회원 고유 ID"),
                                fieldWithPath("username").type(JsonFieldType.STRING).description("사용자 아이디"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("사용자 닉네임"),
                                fieldWithPath("lastSyncedAt").type(JsonFieldType.STRING).description("마지막 동기화 시각").optional()
                        )
                ));
    }

    @Test
    @DisplayName("회원 탈퇴 API")
    void withdraw() throws Exception {
        mockMvc.perform(
                delete("/accounts/me")
                        .with(user(userDetails))
                        .with(csrf())
        )
                .andExpect(status().isOk())
                .andDo(document("account-withdraw",
                        responseFields(
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지")
                        )
                ));
    }

    @Test
    @DisplayName("비밀번호 변경 API")
    void changePassword() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("oldPass123!", "newPass123!", "newPass123!");

        mockMvc.perform(
                patch("/accounts/me/password")
                        .with(user(userDetails))
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andDo(document("account-change-password",
                        requestFields(
                                fieldWithPath("oldPassword").type(JsonFieldType.STRING).description("기존 비밀번호"),
                                fieldWithPath("newPassword").type(JsonFieldType.STRING).description("새 비밀번호"),
                                fieldWithPath("newPasswordConfirm").type(JsonFieldType.STRING).description("새 비밀번호 확인")
                        ),
                        responseFields(
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지")
                        )
                ));
    }

    @Test
    @DisplayName("닉네임 변경 API")
    void changeNickname() throws Exception {
        ChangeNicknameRequest request = new ChangeNicknameRequest("newNickname");

        mockMvc.perform(
                patch("/accounts/me/nickname")
                        .with(user(userDetails))
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andDo(document("account-change-nickname",
                        requestFields(
                                fieldWithPath("newNickname").type(JsonFieldType.STRING).description("새 닉네임")
                        ),
                        responseFields(
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지")
                        )
                ));
    }

    @Test
    @DisplayName("연동된 플레이어 목록 조회 API")
    void getPlayers() throws Exception {
        PlayerSummary summary = new PlayerSummary(1L, "chessPlayer", Platform.CHESS_COM, Instant.now());
        given(getPlayerSummariesUseCase.getPlayerSummaries(anyLong())).willReturn(List.of(summary));

        mockMvc.perform(
                get("/accounts/me/players")
                        .with(user(userDetails))
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andDo(document("account-get-players",
                        responseFields(
                                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("플레이어 목록"),
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("플레이어 고유 ID"),
                                fieldWithPath("[].username").type(JsonFieldType.STRING).description("플랫폼 아이디"),
                                fieldWithPath("[].platform").type(JsonFieldType.STRING).description("플랫폼 종류(CHESS_COM, LICHESS)"),
                                fieldWithPath("[].lastPlayedAt").type(JsonFieldType.STRING).description("마지막 플레이 시각").optional()
                        )
                ));
    }

    @Test
    @DisplayName("플레이어 연동 API")
    void linkPlayer() throws Exception {
        LinkPlayerRequest request = new LinkPlayerRequest("chessPlayer", Platform.CHESS_COM);

        mockMvc.perform(
                post("/accounts/me/players")
                        .with(user(userDetails))
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andDo(document("account-link-player",
                        requestFields(
                                fieldWithPath("username").type(JsonFieldType.STRING).description("플랫폼 아이디"),
                                fieldWithPath("platform").type(JsonFieldType.STRING).description("플랫폼 종류(CHESS_COM, LICHESS)")
                        ),
                        responseFields(
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지")
                        )
                ));
    }

    @Test
    @DisplayName("플레이어 연동 해제 API")
    void unlinkPlayer() throws Exception {
        mockMvc.perform(
                delete("/accounts/me/players/{playerId}", 1L)
                        .with(user(userDetails))
                        .with(csrf())
        )
                .andExpect(status().isOk())
                .andDo(document("account-unlink-player",
                        pathParameters(
                                parameterWithName("playerId").description("플레이어 고유 ID")
                        ),
                        responseFields(
                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메시지")
                        )
                ));
    }

    @Test
    @DisplayName("게임 전적 동기화 API")
    void sync() throws Exception {
        SyncGameResponse response = new SyncGameResponse(10, 5);
        given(syncGameUseCase.syncGames(anyLong())).willReturn(response);

        mockMvc.perform(
                post("/accounts/me/sync")
                        .with(user(userDetails))
                        .with(csrf())
        )
                .andExpect(status().isOk())
                .andDo(document("account-sync",
                        responseFields(
                                fieldWithPath("chess_com").type(JsonFieldType.NUMBER).description("Chess.com 플랫폼의 동기화 작업 큐에서 대기 중인 전체 플레이어 수"),
                                fieldWithPath("lichess").type(JsonFieldType.NUMBER).description("Lichess 플랫폼의 동기화 작업 큐에서 대기 중인 전체 플레이어 수")
                        )
                ));
    }

    @Test
    @DisplayName("게임 전적 동기화 상태 조회 API")
    void syncStatus() throws Exception {
        SyncGameStatus response = new SyncGameStatus(true);
        given(syncGameUseCase.syncStatus(anyLong())).willReturn(response);

        mockMvc.perform(
                get("/accounts/me/sync/status")
                        .with(user(userDetails))
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andDo(document("account-sync-status",
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.BOOLEAN).description("동기화 진행 중 여부 (true: 진행 중, false: 완료 또는 진행 중 아님)")
                        )
                ));
    }
}
