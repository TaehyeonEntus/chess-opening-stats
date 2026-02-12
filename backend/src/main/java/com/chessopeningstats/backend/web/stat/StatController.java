package com.chessopeningstats.backend.web.stat;

import com.chessopeningstats.backend.application.stat.StatService;
import com.chessopeningstats.backend.application.stat.dto.Stat;
import com.chessopeningstats.backend.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stat")
@RequiredArgsConstructor
public class StatController {
    private final AuthService authService;
    private final StatService statService;

    @GetMapping("/player")
    public Stat getPlayerStat(Authentication authentication) {
        return statService.getStatByPlayerId(authService.getPlayerIdFromAuthentication(authentication));
    }

    @GetMapping("/account")
    public Stat getAccountStat(@RequestParam List<Long> accountId){
        if(accountId.isEmpty()) return new Stat(0,0,0);
        return statService.getStatByAccountIds(accountId);
    }

    @GetMapping("/player/opening")
    public Stat getPlayerStatByEpds(Authentication authentication, @RequestParam List<String> epd){
        return statService.getOpeningStatByPlayerId(authService.getPlayerIdFromAuthentication(authentication), epd);
    }

    @GetMapping("/account/opening")
    public Stat getAccountStatByEpds(@RequestParam List<Long> accountId, @RequestParam List<String> epd){
        if(accountId.isEmpty() || epd.isEmpty()) return new Stat(0,0,0);
        return statService.getOpeningStatByAccountIds(accountId, epd);
    }
}
