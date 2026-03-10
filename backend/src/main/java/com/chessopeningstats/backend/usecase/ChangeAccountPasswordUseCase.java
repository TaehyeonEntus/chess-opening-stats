package com.chessopeningstats.backend.usecase;

import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.exception.InvalidPasswordException;
import com.chessopeningstats.backend.service.AccountService;
import com.chessopeningstats.backend.web.account.dto.ChangePasswordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChangeAccountPasswordUseCase {
    private final AccountService accountService;
    private final PasswordEncoder encoder;

    @Transactional
    public void changePassword(long accountId, ChangePasswordRequest request) {
        Account account = accountService.get(accountId);

        if (!encoder.matches(request.oldPassword(), account.getPassword()))
            throw new InvalidPasswordException();

        account.setPassword(encoder.encode(request.newPassword()));
    }
}
