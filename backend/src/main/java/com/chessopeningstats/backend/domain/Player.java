package com.chessopeningstats.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.Builder.Default;

import java.time.Instant;
import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_username_platform",
                        columnNames = {"username", "platform"}
                )
        }
)
public class Player extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, length = 128)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private Platform platform;

    @Default
    @Column(name = "last_played_at", nullable = false)
    private Instant lastPlayedAt = Instant.EPOCH;

    public static Player of(String username, Platform platform) {
        return Player.builder().username(username).platform(platform).build();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(username, player.username) && platform == player.platform;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, platform);
    }
}
