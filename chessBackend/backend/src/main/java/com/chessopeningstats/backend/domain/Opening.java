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
        indexes = {
                @Index(name = "idx_opening_epd", columnList = "epd")
        }
)
public class Opening {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String eco;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 300)
    private String pgn;

    @Column(nullable = false, length = 100, unique = true)
    private String epd;
}
