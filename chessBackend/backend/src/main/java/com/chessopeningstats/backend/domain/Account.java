package com.chessopeningstats.backend.domain;

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
public class Account extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Default
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private Set<PlayerAccount> playerAccounts = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private Platform platform;

    @Column(nullable = false, length = 128)
    private String username;

    @Default
    @Column(name = "last_played_at", nullable = false)
    private Instant lastPlayedAt = Instant.EPOCH;
}
