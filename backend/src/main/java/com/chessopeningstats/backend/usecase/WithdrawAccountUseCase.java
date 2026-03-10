package com.chessopeningstats.backend.usecase;

import com.chessopeningstats.backend.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WithdrawAccountUseCase {
    private final AccountService accountService;

    public void withdrawAccount(long accountId) {
        accountService.delete(accountId);
    }
}
