package com.chessopeningstats.backend.domain;

import com.chessopeningstats.backend.domain.baseEntity.BaseEntity;
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
                        name = "uk_game_player",
                        columnNames = {"game_id", "player_id"}
                )
        }
)
public class GamePlayer extends BaseEntity {
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

