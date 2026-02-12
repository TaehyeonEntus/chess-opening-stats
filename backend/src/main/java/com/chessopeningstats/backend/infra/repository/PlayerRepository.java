package com.chessopeningstats.backend.infra.repository;

import com.chessopeningstats.backend.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findById(long id);
    Optional<Player> findByUsername(String username);
    Optional<Player> findByNickname(String nickname);

    boolean existsByUsername(String username);
    boolean existsByNickname(String nickname);
}
