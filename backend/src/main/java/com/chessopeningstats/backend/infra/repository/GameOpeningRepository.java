package com.chessopeningstats.backend.infra.repository;

import com.chessopeningstats.backend.domain.GameOpening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface GameOpeningRepository extends JpaRepository<GameOpening, Long> {
}
