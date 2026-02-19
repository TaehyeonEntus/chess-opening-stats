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
                        name = "uk_account_player",
                        columnNames = {"account_id", "player_id"}
                )
        }
)
public class AccountPlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id")
    private Player player;

    public static AccountPlayer of(Account account, Player player){
        return AccountPlayer.builder().account(account).player(player).build();
    }
}
