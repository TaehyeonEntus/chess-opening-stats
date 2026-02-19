package com.chessopeningstats.backend.infra.repository;

import com.chessopeningstats.backend.domain.AccountPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccountPlayerRepository extends JpaRepository<AccountPlayer, Long> {

}
