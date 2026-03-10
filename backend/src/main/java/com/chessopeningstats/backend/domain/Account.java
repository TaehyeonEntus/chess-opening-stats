package com.chessopeningstats.backend.domain;

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
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_account_username",
                        columnNames = {"username"}
                ),
                @UniqueConstraint(
                        name = "uk_account_nickname",
                        columnNames = {"nickname"}
                )
        }
)
public class Account extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, length = 64)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 64)
    private String nickname;

    @Default
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountPlayer> accountPlayers = new ArrayList<>();

    @Default
    @Column(name = "last_synced_at", nullable = false)
    private Instant lastSyncedAt = Instant.EPOCH;
}
