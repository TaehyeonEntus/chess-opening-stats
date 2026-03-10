package com.chessopeningstats.backend.domain;

import com.chessopeningstats.backend.util.converter.LongListConverter;
import jakarta.persistence.*;
import lombok.*;
import lombok.Builder.Default;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        indexes = {
                @Index(
                        name = "idx_played_at",
                        columnList = "played_at"
                ),
                @Index(
                        name = "idx_last_matched_opening_id",
                        columnList = "last_matched_opening_id"
                )
        }
)
public class Game extends BaseEntity {
    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private GameType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private GameTime time;

    private Long lastMatchedOpeningId;

    @Default
    @Convert(converter = LongListConverter.class)
    List<Long> openingIds = new ArrayList<>();

    @Column(nullable = false)
    private Instant playedAt;

    @Default
    @OneToMany(mappedBy = "game", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GamePlayer> gamePlayers = new ArrayList<>();
}

