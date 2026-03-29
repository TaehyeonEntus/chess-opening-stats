package com.chessopeningstats.backend.web.dashboard;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.service.EmitterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DashboardController.class)
@AutoConfigureRestDocs
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmitterService emitterService;

    @Test
    void getDashboard() throws Exception {
        SseEmitter emitter = new SseEmitter();
        emitter.complete();
        given(emitterService.createEmitter(any(Player.class))).willReturn(emitter);

        mockMvc.perform(get("/dashboard")
                        .param("platform", "CHESS_COM")
                        .param("username", "testuser"))
                .andExpect(status().isOk())
                .andDo(document("get-dashboard",
                        queryParameters(
                                parameterWithName("platform").description("체스 플랫폼 (CHESS_COM, LICHESS)"),
                                parameterWithName("username").description("플레이어 사용자 이름")
                        )
                ));
    }
}
