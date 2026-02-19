package com.chessopeningstats.backend.infra.repository;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.domain.Platform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findById(long id);
    Optional<Player> findByUsernameAndPlatform(String username, Platform platform);
    boolean existsByUsernameAndPlatform(String username, Platform platform);
}
