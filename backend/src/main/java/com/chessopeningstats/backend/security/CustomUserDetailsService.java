package com.chessopeningstats.backend.security;

import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.infra.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final AccountRepository AccountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return AccountRepository.findByUsername(username)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("username not found"));
    }

    private UserDetails createUserDetails(Account account) {
        return new CustomUserDetails(account, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
