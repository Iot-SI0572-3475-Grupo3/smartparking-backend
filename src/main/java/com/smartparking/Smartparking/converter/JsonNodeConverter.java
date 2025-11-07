package com.smartparking.Smartparking.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Converter(autoApply = false) // CAMBIAR A false – NO aplica automáticamente
@Slf4j
public class JsonNodeConverter implements AttributeConverter<String, String> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null || attribute.isBlank()) {
            return null;
        }
        try {
            // Valida que sea JSON válido
            mapper.readTree(attribute);
            return attribute;
        } catch (JsonProcessingException e) {
            log.error("JSON inválido en vehicleInfo: {}", attribute, e);
            throw new IllegalArgumentException("vehicleInfo debe contener JSON válido", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return dbData; // Ya es String válido
    }
}