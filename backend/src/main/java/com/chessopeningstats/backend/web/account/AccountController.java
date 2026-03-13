package com.chessopeningstats.backend.web.account;

import com.chessopeningstats.backend.security.CustomUserDetails;
import com.chessopeningstats.backend.usecase.*;
import com.chessopeningstats.backend.web.account.dto.ChangeNicknameRequest;
import com.chessopeningstats.backend.web.account.dto.ChangePasswordRequest;
import com.chessopeningstats.backend.web.account.dto.LinkPlayerRequest;
import com.chessopeningstats.backend.web.account.dto.RegisterAccountRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterAccountRequest request) {
        registerAccountUseCase.register(request);
        return ResponseEntity.ok(Map.of("message", "Account registered successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok().body(getAccountDetailUseCase.getAccountDetail(userDetails.getId()));
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> withdraw(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        withdrawAccountUseCase.withdrawAccount(userDetails.getId());
        new SecurityContextLogoutHandler().logout(request, response, authentication);
        return ResponseEntity.ok().body(Map.of("message", "Account withdrawn successfully"));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ChangePasswordRequest request) {
        changeAccountPasswordUseCase.changePassword(userDetails.getId(), request);
        return ResponseEntity.ok().body(Map.of("message", "Password changed successfully"));
    }

    @PatchMapping("/me/nickname")
    public ResponseEntity<?> changeNickname(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ChangeNicknameRequest request) {
        changeAccountNicknameUseCase.changeNickname(userDetails.getId(), request);
        return ResponseEntity.ok().body(Map.of("message", "Nickname changed successfully"));
    }


    @GetMapping("/me/players")
    public ResponseEntity<?> getPlayers(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok().body(getPlayerSummariesUseCase.getPlayerSummaries(userDetails.getId()));
    }

    @PostMapping("/me/players")
    public ResponseEntity<?> linkPlayer(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody LinkPlayerRequest request) {
        linkPlayerUseCase.linkPlayerToAccount(userDetails.getId(), request);
        return ResponseEntity.ok().body(Map.of("message", "Player linked successfully"));
    }

    @DeleteMapping("/me/players/{playerId}")
    public ResponseEntity<?> unlinkPlayer(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long playerId) {
        unlinkPlayerUseCase.unlinkPlayerFromAccount(userDetails.getId(), playerId);
        return ResponseEntity.ok().body(Map.of("message", "Player unlinked successfully"));
    }

    @PostMapping("/me/sync")
    public ResponseEntity<?> syncGames(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok().body(syncGameUseCase.syncGames(userDetails.getId()));
    }

    @GetMapping("/me/sync/status")
    public ResponseEntity<?> syncStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok().body(syncGameUseCase.syncStatus(userDetails.getId()));
    }
}
