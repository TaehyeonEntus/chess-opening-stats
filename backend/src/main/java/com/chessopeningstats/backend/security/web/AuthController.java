package com.chessopeningstats.backend.security.web;

import com.chessopeningstats.backend.security.AuthService;
import com.chessopeningstats.backend.security.web.dto.LoginRequest;
import com.chessopeningstats.backend.web.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "Authentication related APIs")
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Health Check", description = "Checks the health of the application")
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "User Login", description = "Logs in a user and returns a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Authentication failed (e.g., wrong password)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\": \"Authentication failed\"}")))
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<?>> login(@Valid @RequestBody LoginRequest request) {
        authService.login(request);
        return ResponseEntity.ok(ApiResponseDto.success());
    }
}
