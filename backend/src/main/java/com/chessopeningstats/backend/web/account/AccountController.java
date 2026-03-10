package com.chessopeningstats.backend.web.account;

import com.chessopeningstats.backend.security.CustomUserDetails;
import com.chessopeningstats.backend.usecase.*;
import com.chessopeningstats.backend.web.account.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(getAccountDetailUseCase.getAccountDetail(userDetails.getId()));
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> withdraw(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        withdrawAccountUseCase.withdrawAccount(userDetails.getId());
        new SecurityContextLogoutHandler().logout(request, response, authentication);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/me/password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request) {
        changeAccountPasswordUseCase.changePassword(userDetails.getId(), request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/me/nickname")
    public ResponseEntity<?> changeNickname(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ChangeNicknameRequest request) {
        changeAccountNicknameUseCase.changeNickname(userDetails.getId(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me/players")
    public ResponseEntity<?> getPlayers(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(getPlayerSummariesUseCase.getPlayerSummaries(userDetails.getId()));
    }

    @PostMapping("/me/players")
    public ResponseEntity<?> linkPlayer(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody LinkPlayerRequest request) {
        linkPlayerUseCase.linkPlayerToAccount(userDetails.getId(), request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me/players")
    public ResponseEntity<?> unlinkPlayer(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UnlinkPlayerRequest request) {
        unlinkPlayerUseCase.unlinkPlayerFromAccount(userDetails.getId(), request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/me/sync")
    public ResponseEntity<?> sync(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        syncGameUseCase.syncGames(userDetails.getId());
        return ResponseEntity.ok().build();
    }
}
