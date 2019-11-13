package org.openmrs.module.sync2.api.model;

import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.module.webservices.rest.SimpleObject;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;

/**
 * @uthor Willa Mhawila<a.mhawila@gmail.com> on 11/7/19.
 */
@Converter
public class SimpleObjectConverter implements AttributeConverter<SimpleObject, String> {
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(SimpleObject attribute) {
        try {
            return mapper.writeValueAsString(attribute);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SimpleObject convertToEntityAttribute(String dbData) {
        try {
            return mapper.readValue(dbData, SimpleObject.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
