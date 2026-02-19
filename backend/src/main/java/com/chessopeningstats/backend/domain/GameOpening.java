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
                        name = "uk_game_opening",
                        columnNames = {"game_id", "opening_id"}
                )
        }
)
public class GameOpening extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "opening_id")
    private Opening opening;

}
