package com.chessopeningstats.backend.infra.cache;

import com.chessopeningstats.backend.domain.Opening;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class OpeningCache {
    private final Map<Long, Opening> openingMap = new HashMap<>();

    public List<Long> getOpeningsIdsByKeys(Collection<Long> keys) {
        return keys.stream()
                .map(openingMap::get)
                .filter(Objects::nonNull)
                .map(Opening::id)
                .toList();
    }

    public void cacheAll(Collection<Opening> openings) {
        openings.forEach(this::cache);
    }

    private void cache(Opening opening) {
        openingMap.put(opening.key(), opening);
    }
}
