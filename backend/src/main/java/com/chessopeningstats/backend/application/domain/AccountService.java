package com.chessopeningstats.backend.application.domain;

import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.exception.AccountNotFoundException;
import com.chessopeningstats.backend.infra.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    public Account getAccount(long accountId){
        return accountRepository.findById(accountId).orElseThrow(AccountNotFoundException::new);
    }

    public Account saveAccount(Account account) {
        return accountRepository.save(account);
    }

    public void deleteAccount(long accountId){
        accountRepository.deleteById(accountId);
    }

    public boolean existsByUsername(String username) {
        return accountRepository.existsByUsername(username);
    }

    public boolean existsByNickname(String nickname) {
        return accountRepository.existsByNickname(nickname);
    }

    @Transactional
    public void changePassword(long accountId, String encodedPassword){
        getAccount(accountId).setPassword(encodedPassword);
    }

    @Transactional
    public void updateLastSyncedAt(long accountId, Instant lastSyncedAt){
        getAccount(accountId).setLastSyncedAt(lastSyncedAt);
    }
}
