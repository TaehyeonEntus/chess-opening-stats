package com.chessopeningstats.backend.web.provideAccountInfo;

import com.chessopeningstats.backend.application.usecase.provideAccountInfo.AccountInfoProvideService;
import com.chessopeningstats.backend.application.usecase.provideAccountInfo.dto.AccountInfoResponse;
import com.chessopeningstats.backend.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountInfoProvideController {
    private final AuthService authService;
    private final AccountInfoProvideService accountInfoProvideService;

    @GetMapping("/info")
    public AccountInfoResponse syncAccount(Authentication authentication) {
        return accountInfoProvideService.getAccountInfo(authService.getAccountId(authentication));
    }
}