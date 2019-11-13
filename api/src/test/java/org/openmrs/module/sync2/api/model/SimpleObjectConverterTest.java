package org.openmrs.module.sync2.api.model;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.webservices.rest.SimpleObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;


/**
 * @uthor Willa Mhawila<a.mhawila@gmail.com> on 11/7/19.
 */
public class SimpleObjectConverterTest {
    private SimpleObjectConverter converter = new SimpleObjectConverter();
    private SimpleObject object;
    private String json;

    @Before
    public void setup() {
        object = new SimpleObject().add("mtu", new LinkedHashMap<>()).add("umri", 20).add("hobbies", new ArrayList<String>());
        ((Map) object.get("mtu")).put("jina", "jitu bingwa");
        ((List) object.get("hobbies")).addAll(Arrays.asList("bangi", "vitombo", "bata"));

        json = "{\"mtu\": {\"jina\": \"jitu bingwa\" }, \"umri\": 20, \"hobbies\": [\"bangi\", \"vitombo\", \"bata\" ]}";
    }

    @Test
    public void shouldConvertSimpleObjectToJsonStringRepresentation() {
        String converted = converter.convertToDatabaseColumn(object);
        assertEquals(json.replaceAll("\\s", ""), converted.replaceAll("\\s", ""));
    }

    @Test
    public void shouldConvertJsonStringToSimpleObject() {
        assertEquals(converter.convertToEntityAttribute(json), object);
    }
}
