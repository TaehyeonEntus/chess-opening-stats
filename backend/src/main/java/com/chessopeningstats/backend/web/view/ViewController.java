package com.chessopeningstats.backend.web.view;

import com.chessopeningstats.backend.security.CustomUserDetails;
import com.chessopeningstats.backend.usecase.GetHomeViewUseCase;
import com.chessopeningstats.backend.web.dto.ApiResponseDto;
import com.chessopeningstats.backend.web.view.dto.home.HomeView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "View Controller", description = "Endpoints for retrieving views")
@RestController
@RequestMapping("/views")
@RequiredArgsConstructor
public class ViewController {
    private final GetHomeViewUseCase getHomeViewUseCase;

    @Operation(summary = "Get Home View", description = "Retrieves the home view for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved home view"),
            @ApiResponse(responseCode = "404", description = "Account not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\": \"Account not found\"}")))
    })
    @GetMapping("/home")
    public ResponseEntity<ApiResponseDto<HomeView>> getHomeView(@Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        HomeView homeView = getHomeViewUseCase.getHomeView(userDetails.getId());
        return ResponseEntity.ok(ApiResponseDto.success(homeView));
    }
}
