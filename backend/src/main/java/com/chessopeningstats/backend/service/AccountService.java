package com.chessopeningstats.backend.service;

import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.exception.AccountNotFoundException;
import com.chessopeningstats.backend.infra.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    public List<Long> getAllIds() {
        return accountRepository.findAllIds();
    }

    public Account save(Account account) {
        return accountRepository.save(account);
    }

    public Account get(long accountId) {
        return accountRepository.findById(accountId).orElseThrow(AccountNotFoundException::new);
    }

    public void delete(long accountId) {
        accountRepository.deleteById(accountId);
    }

    public boolean existsByUsername(String username) {
        return accountRepository.existsByUsername(username);
    }

    public boolean existsByNickname(String nickname) {
        return accountRepository.existsByNickname(nickname);
    }

    @Transactional
    public void updateLastSyncedAt(long accountId, Instant lastSyncedAt){
        get(accountId).setLastSyncedAt(lastSyncedAt);
    }
}
