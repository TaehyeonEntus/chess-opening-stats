package com.chessopeningstats.backend.security;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.exception.PlayerNotFoundException;
import com.chessopeningstats.backend.infra.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final PlayerRepository playerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return playerRepository.findByUsername(username)
                .map(this::createUserDetails)
                .orElseThrow(PlayerNotFoundException::new);
    }

    private UserDetails createUserDetails(Player player) {
        return new CustomUserDetails(player, Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
