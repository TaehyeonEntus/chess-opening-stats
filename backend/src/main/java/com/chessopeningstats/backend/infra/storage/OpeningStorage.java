package com.chessopeningstats.backend.infra.storage;

import com.chessopeningstats.backend.domain.Opening;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class OpeningStorage {
    private final ConcurrentHashMap<String, Opening> openingMap = new ConcurrentHashMap<>();

    public void storeOne(Opening opening){
        openingMap.put(opening.getEpd(), opening);
    }

    public void storeAll(Collection<Opening> openings){
        openings.forEach(this::storeOne);
    }

    public List<Opening> loadOpeningsByEpds(Collection<String> epds){
        return epds.parallelStream()
                .map(openingMap::get)
                .filter(Objects::nonNull)
                .toList();
    }
}
