package com.chessopeningstats.backend.application.player;

import com.chessopeningstats.backend.application.account.AccountCheckService;
import com.chessopeningstats.backend.application.account.AccountService;
import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.domain.PlayerAccount;
import com.chessopeningstats.backend.exception.AccountNotFoundException;
import com.chessopeningstats.backend.exception.PlayerNotFoundException;
import com.chessopeningstats.backend.infra.repository.PlayerAccountRepository;
import com.chessopeningstats.backend.infra.repository.PlayerRepository;
import com.chessopeningstats.backend.web.account.dto.AddAccountRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final AccountService accountService;
    private final PlayerAccountRepository playerAccountRepository;
    private final AccountCheckService accountCheckService;

    public Player create(Player player) {
        return playerRepository.save(player);
    }

    public boolean existsByUsername(String username) {
        return playerRepository.existsByUsername(username);
    }

    public boolean existsByNickname(String nickname) {
        return playerRepository.existsByNickname(nickname);
    }

    @Transactional
    public void addAccountOnPlayer(long playerId, AddAccountRequest request) {
        Player player = playerRepository.findById(playerId).orElseThrow(PlayerNotFoundException::new);
        Account account;

        String username = request.getUsername();
        Platform platform = request.getPlatform();

        if (accountService.existsByUsernameAndPlatform(username, platform)) {
            account = accountService.getByUsernameAndPlatform(username, platform);
        } else if (accountCheckService.checkAccount(username, platform))
            account = accountService.create(Account.builder().username(username).platform(platform).build());
        else
            throw new AccountNotFoundException();

        PlayerAccount pa =PlayerAccount.builder()
                .player(player)
                .account(account)
                .build();

        playerAccountRepository.save(pa);
    }
}
