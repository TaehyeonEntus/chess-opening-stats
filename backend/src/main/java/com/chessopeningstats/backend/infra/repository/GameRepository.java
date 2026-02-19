package com.chessopeningstats.backend.infra.repository;

import com.chessopeningstats.backend.domain.Game;

import java.time.Instant;
import java.util.*;

import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GameRepository extends JpaRepository<Game, Long> {
}
