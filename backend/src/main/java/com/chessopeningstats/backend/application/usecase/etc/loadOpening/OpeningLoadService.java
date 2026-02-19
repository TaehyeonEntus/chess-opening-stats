package com.chessopeningstats.backend.application.usecase.etc.loadOpening;

import com.chessopeningstats.backend.domain.Opening;
import com.chessopeningstats.backend.infra.client.fetchOpeningClient.OpeningFetchClient;
import com.chessopeningstats.backend.infra.repository.OpeningRepository;
import com.chessopeningstats.backend.infra.storage.OpeningStorage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
@Transactional
public class OpeningLoadService {
    private final OpeningFetchClient client;
    private final OpeningRepository openingRepository;
    private final OpeningStorage openingStorage;

    public void loadOpening() {
        ingestOpenings(fetchOpenings());
        storeOpenings();
    }

    private Collection<Opening> fetchOpenings() {
        return client.fetchOpenings();
    }

    private void ingestOpenings(Collection<Opening> openings) {
        openingRepository.saveAll(openings);
    }

    private void storeOpenings() {
        openingStorage.storeAll(openingRepository.findAll());
    }
}
