package com.chessopeningstats.backend.security;

import com.chessopeningstats.backend.application.domain.AccountPlayerService;
import com.chessopeningstats.backend.application.domain.AccountService;
import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.domain.AccountPlayer;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.exception.InvalidPasswordException;
import com.chessopeningstats.backend.exception.NicknameAlreadyExistsException;
import com.chessopeningstats.backend.exception.PasswordMismatchException;
import com.chessopeningstats.backend.exception.UsernameAlreadyExistsException;
import com.chessopeningstats.backend.security.jwt.JwtTokenProvider;
import com.chessopeningstats.backend.security.web.dto.ChangePasswordRequest;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authManager;
    private final AccountPlayerService accountPlayerService;
    private final AccountService accountService;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider tokenProvider;

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

    @Transactional
    public void delete(long accountId){
        Account account = accountService.getAccount(accountId);
        List<Player> players = account.getAccountPlayers().stream().map(AccountPlayer::getPlayer).toList();

        for(Player player : players)
            accountPlayerService.deleteByAccountAndPlayer(account.getId(), player.getId());

        accountService.deleteAccount(accountId);
    }

    public void changePassword(long accountId, ChangePasswordRequest request) {
        Account account = accountService.getAccount(accountId);

        checkPasswordValid(request.getOldPassword(), account.getPassword());
        checkPasswordMatch(request.getOldPassword(), request.getNewPassword());

        accountService.changePassword(accountId, encoder.encode(request.getNewPassword()));
    }

    public void logout(){
        //refresh token 추가시 작업 필요
    }

    private void checkPasswordValid(String oldPassword, String newPassword){
        if(!encoder.matches(oldPassword, newPassword))
            throw new InvalidPasswordException();
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
