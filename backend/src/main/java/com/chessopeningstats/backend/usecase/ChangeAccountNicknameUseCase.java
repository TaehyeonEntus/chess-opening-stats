package com.chessopeningstats.backend.usecase;

import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.service.AccountService;
import com.chessopeningstats.backend.web.account.dto.ChangeNicknameRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChangeAccountNicknameUseCase {
    private final AccountService accountService;

    @Transactional
    public void changeNickname(long accountId, ChangeNicknameRequest request) {
        Account account = accountService.get(accountId);

        account.setNickname(request.newNickname());
    }
}
