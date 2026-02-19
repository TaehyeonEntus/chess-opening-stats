package com.chessopeningstats.backend.application.usecase.etc.init;

import com.chessopeningstats.backend.application.usecase.etc.loadOpening.OpeningLoadService;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.AccountPlayer;
import com.chessopeningstats.backend.infra.repository.PlayerRepository;
import com.chessopeningstats.backend.infra.repository.AccountPlayerRepository;
import com.chessopeningstats.backend.infra.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class InitService {
    private final AccountRepository AccountRepository;
    private final PlayerRepository playerRepository;
    private final AccountPlayerRepository accountPlayerRepository;
    private final OpeningLoadService openingLoadService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        openingLoadService.loadOpening();

        Account a1 = Account.builder()
                .username("1")
                .password(passwordEncoder.encode("1"))
                .nickname("태현").build();

        Account a2 = Account.builder()
                .username("2")
                .password(passwordEncoder.encode("2"))
                .nickname("hikaru").build();

        Account a3 = Account.builder()
                .username("3")
                .password(passwordEncoder.encode("3"))
                .nickname("carlsen").build();

        Account a4 = Account.builder()
                .username("4")
                .password(passwordEncoder.encode("4"))
                .nickname("woodward").build();


        Player p1 = Player.builder()
                .username("x0gusplaysgroove")
                .platform(Platform.CHESS_COM)
                .lastPlayedAt(Instant.ofEpochMilli(1L))
                .build();

        Player p2 = Player.builder()
                .username("hikaru")
                .platform(Platform.CHESS_COM)
                .lastPlayedAt(Instant.ofEpochMilli(1L))
                .build();

        Player p3 = Player.builder()
                .username("magnuscarlsen")
                .platform(Platform.CHESS_COM)
                .lastPlayedAt(Instant.ofEpochMilli(1L))
                .build();

        Player p4 = Player.builder()
                .username("mraquariyaz67")
                .platform(Platform.LICHESS)
                .lastPlayedAt(Instant.ofEpochMilli(1L))
                .build();

        AccountPlayer ap1 = AccountPlayer.builder()
                .account(a1)
                .player(p1)
                .build();

        AccountPlayer ap2 = AccountPlayer.builder()
                .account(a2)
                .player(p2)
                .build();

        AccountPlayer ap3 = AccountPlayer.builder()
                .account(a3)
                .player(p3)
                .build();

        AccountPlayer ap4 = AccountPlayer.builder()
                .account(a4)
                .player(p4)
                .build();

        AccountRepository.save(a1);
        AccountRepository.save(a2);
        AccountRepository.save(a3);
        AccountRepository.save(a4);


        playerRepository.save(p1);
        playerRepository.save(p2);
        playerRepository.save(p3);
        playerRepository.save(p4);

        accountPlayerRepository.save(ap1);
        accountPlayerRepository.save(ap2);
        accountPlayerRepository.save(ap3);
        accountPlayerRepository.save(ap4);
    }
}
