package com.chessopeningstats.backend.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_player_id_game_id",
                        columnNames = {"player_id", "game_id"}
                )
        }
)
public class GamePlayer{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Player player;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private GamePlayerColor color;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private GamePlayerResult result;
}

