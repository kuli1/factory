package io.dddbyexamples.tools;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import javax.persistence.AttributeConverter;
import java.io.IOException;

public abstract class JsonConverter<T> implements AttributeConverter<T, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
            .setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            .setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES))
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());

    private final Class<T> type;

    public JsonConverter(Class<T> type) {
        this.type = type;
    }

    @Override
    public String convertToDatabaseColumn(T object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public T convertToEntityAttribute(String data) {
        try {
            return objectMapper.readValue(data, type);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
