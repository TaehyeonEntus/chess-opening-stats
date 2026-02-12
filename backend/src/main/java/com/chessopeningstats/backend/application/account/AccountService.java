package com.chessopeningstats.backend.application.account;

import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.exception.AccountNotFoundException;
import com.chessopeningstats.backend.infra.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    public Account create(Account account) {
        return accountRepository.save(account);
    }

    public boolean existsByUsernameAndPlatform(String username, Platform platform){
        return accountRepository.existsByUsernameAndPlatform(username, platform);
    }

    public Account getByUsernameAndPlatform(String username, Platform platform) {
        return accountRepository.findByUsernameAndPlatform(username, platform).orElseThrow(AccountNotFoundException::new);
    }
}
