package com.chessopeningstats.backend.web.stat;

import com.chessopeningstats.backend.application.stat.StatService;
import com.chessopeningstats.backend.application.stat.dto.Stat;
import com.chessopeningstats.backend.exception.PlayerNotFoundException;
import com.chessopeningstats.backend.infra.repository.PlayerRepository;
import com.chessopeningstats.backend.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stat")
@RequiredArgsConstructor
public class StatController {
    private final AuthService authService;
    private final StatService statService;
    private final PlayerRepository playerRepository;

    @GetMapping("/all")
    public List<Stat> getAllStat(){
        return statService.getAllStats();
    }

    @GetMapping("/player")
    public List<Stat> getPlayerStat(Authentication authentication) {
        return statService.getStatsByPlayerId(authService.getPlayerIdFromAuthentication(authentication));
    }

    @GetMapping("/player/{nickname}")
    public List<Stat> getPlayerStatByNickname(@PathVariable String nickname) {
        return statService.getStatsByPlayerId(playerRepository.findByNickname(nickname).orElseThrow(PlayerNotFoundException::new).getId());
    }
}
