package com.chessopeningstats.backend.application.domain;

import com.chessopeningstats.backend.domain.AccountPlayer;
import com.chessopeningstats.backend.infra.repository.AccountPlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountPlayerService {
    private final AccountPlayerRepository accountPlayerRepository;

    public AccountPlayer saveAccountPlayer(AccountPlayer accountPlayer) {
        return accountPlayerRepository.save(accountPlayer);
    }
}
