package com.chessopeningstats.backend.infra.cache;

import com.chessopeningstats.backend.domain.Opening;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class OpeningCache {
    private final Map<String, Opening> openingMap = new HashMap<>();

    public List<Opening> getOpeningsByEpds(Collection<String> epds) {
        return epds.stream()
                .map(openingMap::get)
                .filter(Objects::nonNull)
                .toList();
    }

    public void cacheAll(Collection<Opening> openings) {
        openings.forEach(this::cache);
    }

    private void cache(Opening opening) {
        openingMap.put(opening.epd(), opening);
    }
}
