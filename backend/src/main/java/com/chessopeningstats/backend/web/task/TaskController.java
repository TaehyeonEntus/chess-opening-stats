package com.chessopeningstats.backend.web.task;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.queue.PlayerQueueRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TaskController {
    private final PlayerQueueRegistry playerQueueRegistry;
    @PostMapping("/task")
    public ResponseEntity<?> task(@RequestParam Platform platform, @RequestParam String username) {
        playerQueueRegistry.getQueue(platform).enqueue(Player.of(platform, username));
        return ResponseEntity.ok().build();
    }
}
