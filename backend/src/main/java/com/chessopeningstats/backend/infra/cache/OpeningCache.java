package com.chessopeningstats.backend.infra.cache;

import com.chessopeningstats.backend.domain.Opening;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class OpeningCache {
    private final Map<String, Opening> epdToOpeningMap = new HashMap<>();
    private final Map<Long, Opening> idToOpeningMap = new HashMap<>();

    public List<Opening> getOpeningsByEpds(Collection<String> epds) {
        return epds.stream()
                .map(epdToOpeningMap::get)
                .filter(Objects::nonNull)
                .toList();
    }

    public Opening get(Long id) {
        return idToOpeningMap.get(id);
    }

    public void cacheAll(Collection<Opening> openings) {
        openings.forEach(this::cacheOne);
    }

    private void cacheOne(Opening opening) {
        epdToOpeningMap.put(opening.epd(), opening);
        idToOpeningMap.put(opening.id(), opening);
    }
}
