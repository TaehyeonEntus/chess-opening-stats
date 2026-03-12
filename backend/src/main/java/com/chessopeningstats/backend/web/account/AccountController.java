package com.chessopeningstats.backend.web.account;

import com.chessopeningstats.backend.security.CustomUserDetails;
import com.chessopeningstats.backend.usecase.*;
import com.chessopeningstats.backend.web.account.dto.*;
import com.chessopeningstats.backend.web.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Account Management", description = "APIs for managing user accounts")
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final RegisterAccountUseCase registerAccountUseCase;
    private final WithdrawAccountUseCase withdrawAccountUseCase;
    private final GetAccountDetailUseCase getAccountDetailUseCase;
    private final GetPlayerSummariesUseCase getPlayerSummariesUseCase;
    private final LinkPlayerUseCase linkPlayerUseCase;
    private final UnlinkPlayerUseCase unlinkPlayerUseCase;
    private final ChangeAccountNicknameUseCase changeAccountNicknameUseCase;
    private final ChangeAccountPasswordUseCase changeAccountPasswordUseCase;
    private final SyncGameUseCase syncGameUseCase;

    @Operation(summary = "Register a new account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful registration"),
            @ApiResponse(responseCode = "409", description = "Username or Nickname already exists",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\": \"Username already exists\"}")))
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto<?>> register(
            @Valid @RequestBody RegisterAccountRequest request) {
        registerAccountUseCase.register(request);
        return ResponseEntity.ok(ApiResponseDto.success());
    }

    @Operation(summary = "Get current user's account details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved account details"),
            @ApiResponse(responseCode = "404", description = "Account not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\": \"Account not found\"}")))
    })
    @GetMapping("/me")
    public ResponseEntity<ApiResponseDto<?>> me(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponseDto.success(getAccountDetailUseCase.getAccountDetail(userDetails.getId())));
    }

    @Operation(summary = "Withdraw (delete) the current user's account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account withdrawn successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\": \"Account not found\"}")))
    })
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponseDto<?>> withdraw(
            @Parameter(hidden = true) HttpServletRequest request,
            @Parameter(hidden = true) HttpServletResponse response,
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        withdrawAccountUseCase.withdrawAccount(userDetails.getId());
        new SecurityContextLogoutHandler().logout(request, response, authentication);
        return ResponseEntity.ok(ApiResponseDto.success());
    }

    @Operation(summary = "Change the current user's password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid current password",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\": \"Invalid password\"}"))),
            @ApiResponse(responseCode = "404", description = "Account not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\": \"Account not found\"}")))
    })
    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponseDto<?>> changePassword(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request) {
        changeAccountPasswordUseCase.changePassword(userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponseDto.success());
    }

    @Operation(summary = "Change the current user's nickname")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nickname changed successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\": \"Account not found\"}"))),
            @ApiResponse(responseCode = "409", description = "Nickname already exists",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\": \"Nickname already exists\"}")))
    })
    @PatchMapping("/me/nickname")
    public ResponseEntity<ApiResponseDto<?>> changeNickname(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ChangeNicknameRequest request) {
        changeAccountNicknameUseCase.changeNickname(userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponseDto.success());
    }

    @Operation(summary = "Get linked players for the current user")
    @GetMapping("/me/players")
    public ResponseEntity<ApiResponseDto<?>> getPlayers(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponseDto.success(getPlayerSummariesUseCase.getPlayerSummaries(userDetails.getId())));
    }

    @Operation(summary = "Link a player to the current user's account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Player linked successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found / Username not found on platform",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\": \"...\"}"))),
            @ApiResponse(responseCode = "409", description = "Player already linked",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\": \"Player already linked\"}")))
    })
    @PostMapping("/me/players")
    public ResponseEntity<ApiResponseDto<?>> linkPlayer(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody LinkPlayerRequest request) {
        linkPlayerUseCase.linkPlayerToAccount(userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponseDto.success());
    }

    @Operation(summary = "Unlink a player from the current user's account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Player unlinked successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found / Player not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\": \"...\"}")))
    })
    @DeleteMapping("/me/players")
    public ResponseEntity<ApiResponseDto<?>> unlinkPlayer(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UnlinkPlayerRequest request) {
        unlinkPlayerUseCase.unlinkPlayerFromAccount(userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponseDto.success());
    }

    @Operation(summary = "Sync games for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sync request accepted"),
            @ApiResponse(responseCode = "400", description = "No linked players",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\": \"No linked players found\"}"))),
            @ApiResponse(responseCode = "404", description = "Account not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\": \"Account not found\"}"))),
            @ApiResponse(responseCode = "429", description = "Too many sync requests",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\": \"Too many sync requests\"}")))
    })
    @PostMapping("/me/sync")
    public ResponseEntity<ApiResponseDto<?>> sync(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
        syncGameUseCase.syncGames(userDetails.getId());
        return ResponseEntity.ok(ApiResponseDto.success());
    }
}
