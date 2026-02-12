package com.chessopeningstats.backend.domain;

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
@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_game_uuid", columnNames = "uuid")
        }
)
public class Game extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 128)
    private String uuid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private GameTime time;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private GameType type;

    @Column(nullable = false)
    private Instant playedAt;

    @Default
    @OneToMany(mappedBy = "game",fetch = FetchType.LAZY)
    private Set<GameOpening> gameOpenings = new HashSet<>();

    @Default
    @OneToMany(mappedBy = "game", fetch = FetchType.LAZY)
    private Set<GamePlayer> gamePlayers = new HashSet<>();
}

