ALTER TABLE game_player
    DROP FOREIGN KEY fk_game_player_player;

ALTER TABLE game_player
    ADD CONSTRAINT fk_game_player_player
        FOREIGN KEY (player_id)
            REFERENCES player (id)
            ON DELETE CASCADE;

ALTER TABLE game_player
    DROP FOREIGN KEY fk_game_player_game;

ALTER TABLE game_player
    ADD CONSTRAINT fk_game_player_game
        FOREIGN KEY (game_id)
            REFERENCES game (id)
            ON DELETE CASCADE;