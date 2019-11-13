package org.openmrs.module.sync2.api.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @uthor Willa Mhawila<a.mhawila@gmail.com> on 11/7/19.
 */
public class ClassConverterTest {
    private ClassConverter classConverter = new ClassConverter();
    @Test
    public void shouldConvertClassRepresentationToString() {
        assertEquals(String.class.getName(), classConverter.convertToDatabaseColumn(String.class));
    }

    @Test
    public void shouldConvertClassStringNameToClass() {
        assertTrue(Class.class.isInstance(classConverter.convertToEntityAttribute(String.class.getName())));
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowIfStuffDoesNotMakeAnySenseWhatsoever() {
        classConverter.convertToEntityAttribute("This obviously won't work!");
    }
}
