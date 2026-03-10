package com.chessopeningstats.backend.infra.repository;

import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.domain.GamePlayerColor;
import com.chessopeningstats.backend.web.view.dto.home.ColorRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findById(long id);

    Optional<Account> findByUsername(String username);

    @Query("""
            select a.id
            from Account a
            """)
    List<Long> findAllIds();

    boolean existsByUsername(String username);

    boolean existsByNickname(String nickname);


}
