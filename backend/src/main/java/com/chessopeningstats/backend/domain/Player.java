package com.chessopeningstats.backend.domain;

import com.chessopeningstats.backend.domain.baseEntity.BaseEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import lombok.Builder.Default;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Default
    @OneToMany(mappedBy = "player", fetch = FetchType.LAZY)
    private Set<AccountPlayer> accountPlayers = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private Platform platform;

    @Column(nullable = false, length = 128)
    private String username;

    @Default
    @Column(name = "last_played_at", nullable = false)
    private Instant lastPlayedAt = Instant.EPOCH;
}
