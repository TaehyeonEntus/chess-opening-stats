package com.chessopeningstats.backend.usecase;

import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.service.AccountService;
import com.chessopeningstats.backend.web.account.dto.AccountDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAccountDetailUseCase {
    private final AccountService accountService;

    public AccountDetail getAccountDetail(long accountId) {
        Account account = accountService.get(accountId);
        return new AccountDetail(
                account.getId(),
                account.getUsername(),
                account.getNickname(),
                account.getLastSyncedAt()
        );
    }
}
