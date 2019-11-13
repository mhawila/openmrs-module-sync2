package org.openmrs.module.sync2.api.model;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * @uthor Willa Mhawila<a.mhawila@gmail.com> on 11/7/19.
 */
@Converter
public class ClassConverter implements AttributeConverter<Class, String> {
    @Override
    public String convertToDatabaseColumn(Class aClass) {
        return aClass.getName();
    }

    @Override
    public Class convertToEntityAttribute(String databaseValue) {
        try {
            return Class.forName(databaseValue);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
