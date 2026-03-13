package com.chessopeningstats.backend.usecase;

import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.exception.NicknameAlreadyExistsException;
import com.chessopeningstats.backend.exception.UsernameAlreadyExistsException;
import com.chessopeningstats.backend.service.AccountService;
import com.chessopeningstats.backend.web.account.dto.RegisterAccountRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterAccountUseCase {
    private final AccountService accountService;
    private final PasswordEncoder encoder;

    public void register(RegisterAccountRequest request) {
        if (accountService.existsByUsername(request.username()))
            throw new UsernameAlreadyExistsException();

        if (accountService.existsByNickname(request.nickname()))
            throw new NicknameAlreadyExistsException();

        accountService.save(
                Account.builder()
                        .username(request.username())
                        .password(encoder.encode(request.password()))
                        .nickname(request.nickname())
                        .build()
        );
    }

}
