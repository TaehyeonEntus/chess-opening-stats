package com.chessopeningstats.backend.infra.storage;

import com.chessopeningstats.backend.domain.Opening;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class OpeningStorage {
    private final Map<Long, Opening> openingMap = new HashMap<>();

    public List<Long> getOpeningsIdsByKeys(Collection<Long> keys) {
        return keys.stream()
                .map(openingMap::get)
                .filter(Objects::nonNull)
                .map(Opening::id)
                .toList();
    }

    public void storeAll(Collection<Opening> openings) {
        openings.forEach(this::store);
    }

    private void store(Opening opening) {
        openingMap.put(opening.key(), opening);
    }
}
