package com.chessopeningstats.backend.util.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.flywaydb.core.internal.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Converter(autoApply = true)
public class LongListConverter implements AttributeConverter<List<Long>, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<List<Long>> TYPE_REF = new TypeReference<>() {
    };
    private static final String EMPTY_JSON_ARRAY = "[]";

    @Override
    public String convertToDatabaseColumn(List<Long> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return EMPTY_JSON_ARRAY;
        }
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("List<Long> → JSON 직렬화 실패: " + attribute, e);
        }
    }

    @Override
    public List<Long> convertToEntityAttribute(String dbData) {
        if (!StringUtils.hasText(dbData) || EMPTY_JSON_ARRAY.equals(dbData.trim())) {
            return new ArrayList<>();
        }
        try {
            return MAPPER.readValue(dbData, TYPE_REF);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("JSON → List<Long> 역직렬화 실패: " + dbData, e);
        }
    }
}
