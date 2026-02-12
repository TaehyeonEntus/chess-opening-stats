package com.chessopeningstats.backend.infra.client.fetchOpeningClient;

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

        try (Reader reader = new InputStreamReader(new ClassPathResource("opening.tsv").getInputStream(), StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.Builder.create()
                     .setDelimiter('\t') // 탭 구분자 명시
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .setTrim(true)
                     .build())) {

            csvParser.stream()
                    .forEach(record -> openings.add(
                            Opening.builder()
                                    .eco(record.get("eco"))
                                    .name(record.get("name"))
                                    .pgn(record.get("pgn"))
                                    .epd(record.get("epd"))
                                    .build()));
        } catch (IOException e) {
            throw new RuntimeException("오프닝 파일을 읽는 중 오류가 발생했습니다.", e);
        }

        return openings;
    }
}

