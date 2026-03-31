package com.chessopeningstats.backend.web.task;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.infra.queue.PlayerQueue;
import com.chessopeningstats.backend.infra.queue.PlayerQueueRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@AutoConfigureRestDocs
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlayerQueueRegistry playerQueueRegistry;

    @Test
    @DisplayName("대기열 등록 성공 (200)")
    void enqueueTask() throws Exception {
        PlayerQueue playerQueue = mock(PlayerQueue.class);
        given(playerQueueRegistry.getQueue(Platform.CHESS_COM)).willReturn(playerQueue);

        mockMvc.perform(post("/task")
                        .queryParam("platform", "CHESS_COM")
                        .queryParam("username", "testuser"))
                .andExpect(status().isOk())
                .andDo(document("enqueue-task",
                        queryParameters(
                                parameterWithName("platform").description("체스 플랫폼 (CHESS_COM, LICHESS)"),
                                parameterWithName("username").description("플레이어 사용자 이름")
                        )
                ));

        verify(playerQueue).enqueue(any());
    }
}
