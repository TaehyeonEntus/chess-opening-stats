package com.chessopeningstats.backend.util.cacheopening;

import com.chessopeningstats.backend.domain.Opening;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OpeningFetchClient {
    public List<Opening> fetchOpenings() {
        List<Opening> openings = new ArrayList<>();
        try (Reader reader = new InputStreamReader(new ClassPathResource("OPENING.csv").getInputStream(), StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.Builder.create()
                     .setDelimiter(',')
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .setTrim(true)
                     .build())) {

            csvParser.stream().forEach(record ->
                    openings.add(
                            new Opening(
                                    Long.parseLong(record.get("id")),
                                    record.get("eco"),
                                    record.get("name"),
                                    record.get("pgn"),
                                    record.get("epd")
                            )
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException("오프닝 파일을 읽는 중 오류가 발생했습니다.", e);
        }

        return openings;
    }
}

