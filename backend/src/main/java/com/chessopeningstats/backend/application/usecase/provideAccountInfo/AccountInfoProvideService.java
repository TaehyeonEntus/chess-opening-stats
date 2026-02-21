package com.chessopeningstats.backend.application.usecase.provideAccountInfo;

import com.chessopeningstats.backend.application.domain.AccountService;
import com.chessopeningstats.backend.application.usecase.provideAccountInfo.dto.AccountInfoResponse;
import com.chessopeningstats.backend.application.usecase.provideAccountInfo.dto.PlayerInfo;
import com.chessopeningstats.backend.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountInfoProvideService {
    private final AccountService accountService;

    @Transactional(readOnly = true)
    public AccountInfoResponse getAccountInfo(long accountId) {
        Account account = accountService.getAccount(accountId);
        return AccountInfoResponse.of(
                account.getNickname(),
                account.getLastSyncedAt(),
                account.getAccountPlayers().stream()
                        .map(ap ->
                                PlayerInfo.of(
                                        ap.getPlayer().getUsername(),
                                        ap.getPlayer().getPlatform(),
                                        ap.getPlayer().getLastPlayedAt()))
                        .toList());
    }
}
