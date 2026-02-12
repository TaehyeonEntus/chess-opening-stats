package com.chessopeningstats.backend.infra.repository;

import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.domain.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findById(long id);
    Optional<Account> findByUsernameAndPlatform(String username, Platform platform);
    boolean existsByUsernameAndPlatform(String username, Platform platform);
}
