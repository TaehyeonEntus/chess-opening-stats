package com.chessopeningstats.backend.domain;

import com.chessopeningstats.backend.domain.baseEntity.BaseEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import lombok.Builder.Default;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 64)
    private String nickname;

    @Default
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AccountPlayer> accountPlayers = new HashSet<>();

    @Default
    @Column(name = "last_synced_at", nullable = false)
    private Instant lastSyncedAt = Instant.EPOCH;
}
