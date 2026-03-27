package com.chessopeningstats.backend.web.player;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.infra.client.playerprofile.dto.PlayerProfile;
import com.chessopeningstats.backend.service.PlayerProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlayerController.class)
@AutoConfigureRestDocs
class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlayerProfileService playerProfileService;

    @Test
    void getProfile() throws Exception {
        given(playerProfileService.fetchPlayerProfile(any(Platform.class), anyString()))
                .willReturn(new PlayerProfile("https://example.com/image.png", 1711641600L));

        mockMvc.perform(get("/player/profile")
                        .param("platform", "CHESS_COM")
                        .param("username", "testuser"))
                .andExpect(status().isOk())
                .andDo(document("get-profile",
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
}
