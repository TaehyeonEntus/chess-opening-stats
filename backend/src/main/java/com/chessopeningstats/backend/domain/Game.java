package com.chessopeningstats.backend.domain;

import com.chessopeningstats.backend.domain.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder.Default;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table
public class Game extends BaseEntity {
    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private GameTime time;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private GameType type;

    @Column(nullable = false)
    private Instant playedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private Opening lastMatchedOpening;

    @Default
    @OneToMany(mappedBy = "game", fetch = FetchType.LAZY)
    private Set<GameOpening> gameOpenings = new HashSet<>();

    @Default
    @OneToMany(mappedBy = "game", fetch = FetchType.LAZY)
    private Set<GamePlayer> gamePlayers = new HashSet<>();
}

