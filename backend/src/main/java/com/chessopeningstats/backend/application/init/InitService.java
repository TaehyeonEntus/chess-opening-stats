package com.chessopeningstats.backend.application.init;

import com.chessopeningstats.backend.application.loadOpening.OpeningLoadService;
import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.domain.PlayerAccount;
import com.chessopeningstats.backend.infra.repository.AccountRepository;
import com.chessopeningstats.backend.infra.repository.PlayerAccountRepository;
import com.chessopeningstats.backend.infra.repository.PlayerRepository;
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
    private final PlayerRepository playerRepository;
    private final AccountRepository accountRepository;
    private final PlayerAccountRepository playerAccountRepository;
    private final OpeningLoadService openingLoadService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void init(){
        openingLoadService.loadOpening();

        Player p1 = Player.builder()
                .username("1")
                .password(passwordEncoder.encode("1"))
                .nickname("태현").build();

        Player p2 = Player.builder()
                .username("2")
                .password(passwordEncoder.encode("2"))
                .nickname("hikaru").build();

        Player p3 = Player.builder()
                .username("3")
                .password(passwordEncoder.encode("3"))
                .nickname("carlsen").build();



        Account a1 = Account.builder()
                .username("x0gusplaysgroove")
                .platform(Platform.CHESS_COM)
                .lastPlayedAt(Instant.ofEpochMilli(1L))
                .build();

        Account a2 = Account.builder()
                .username("hikaru")
                .platform(Platform.CHESS_COM)
                .lastPlayedAt(Instant.ofEpochMilli(1L))
                .build();

        Account a3 = Account.builder()
                .username("magnuscarlsen")
                .platform(Platform.CHESS_COM)
                .lastPlayedAt(Instant.ofEpochMilli(1L))
                .build();

        PlayerAccount pa1 = PlayerAccount.builder()
                .player(p1)
                .account(a1)
                .build();

        PlayerAccount pa2 = PlayerAccount.builder()
                .player(p2)
                .account(a2)
                .build();

        PlayerAccount pa3 = PlayerAccount.builder()
                .player(p3)
                .account(a3)
                .build();

        playerRepository.save(p1);
        playerRepository.save(p2);
        playerRepository.save(p3);

        accountRepository.save(a1);
        accountRepository.save(a2);
        accountRepository.save(a3);

        playerAccountRepository.save(pa1);
        playerAccountRepository.save(pa2);
        playerAccountRepository.save(pa3);
    }
}
