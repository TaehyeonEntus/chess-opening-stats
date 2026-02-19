package com.chessopeningstats.backend.infra.repository;

import com.chessopeningstats.backend.domain.Opening;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpeningRepository extends JpaRepository<Opening, Long> {
}
