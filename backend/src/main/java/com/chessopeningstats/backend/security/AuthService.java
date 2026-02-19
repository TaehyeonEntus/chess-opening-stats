package com.chessopeningstats.backend.security;

import com.chessopeningstats.backend.application.domain.AccountService;
import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.exception.NicknameAlreadyExistsException;
import com.chessopeningstats.backend.exception.PasswordMismatchException;
import com.chessopeningstats.backend.exception.UsernameAlreadyExistsException;
import com.chessopeningstats.backend.security.jwt.JwtTokenProvider;
import com.chessopeningstats.backend.security.web.dto.LoginRequest;
import com.chessopeningstats.backend.security.web.dto.RegisterRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authManager;
    private final AccountService accountService;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public String login(LoginRequest request) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return tokenProvider.createToken(authentication);
    }

    @Transactional
    public void register(RegisterRequest request) {
        checkPasswordMatch(request.getPassword(), request.getPasswordConfirm());
        checkUsernameAvailable(request.getUsername());
        checkNicknameAvailable(request.getNickname());

        Account account = Account.builder()
                .username(request.getUsername())
                .password(encoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .build();

        accountService.saveAccount(account);
    }


    private void checkPasswordMatch(String password, String passwordConfirm) {
        if (!password.equals(passwordConfirm))
            throw new PasswordMismatchException();
    }

    private void checkUsernameAvailable(String username) {
        if (accountService.existsByUsername(username))
            throw new UsernameAlreadyExistsException();
    }

    private void checkNicknameAvailable(String nickname) {
        if (accountService.existsByNickname(nickname))
            throw new NicknameAlreadyExistsException();
    }

    public long getAccountId(Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
        return Long.parseLong(userDetails.getAccountId());
    }
}
