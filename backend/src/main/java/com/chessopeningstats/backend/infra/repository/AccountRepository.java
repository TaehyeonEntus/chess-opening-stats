package com.chessopeningstats.backend.infra.repository;

import com.chessopeningstats.backend.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findById(long id);
    Optional<Account> findByUsername(String username);

    boolean existsByUsername(String username);
    boolean existsByNickname(String nickname);
}
