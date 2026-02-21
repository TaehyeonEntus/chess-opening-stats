package com.chessopeningstats.backend.web.provideStat;

import com.chessopeningstats.backend.application.usecase.provideStat.StatProvideService;
import com.chessopeningstats.backend.application.usecase.provideStat.dto.OpeningStatsResponse;
import com.chessopeningstats.backend.application.usecase.provideStat.dto.SummaryResponse;
import com.chessopeningstats.backend.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stat")
@RequiredArgsConstructor
public class StatProvideController {
    private final AuthService authService;
    private final StatProvideService statProvideService;

    @GetMapping("/all")
    public OpeningStatsResponse getAllStats(){
        return statProvideService.getAllOpeningStats();
    }

    @GetMapping("/all/summary")
    public SummaryResponse getAllSummaries(){
        return statProvideService.getAllSummaries();
    }

    @GetMapping("/account")
    public OpeningStatsResponse getAccountStats(Authentication authentication) {
        return statProvideService.getAccountOpeningStats(authService.getAccountId(authentication));
    }

    @GetMapping("/account/summary")
    public SummaryResponse getAccountSummaries(Authentication authentication) {
        return statProvideService.getAccountSummaries(authService.getAccountId(authentication));
    }
}
