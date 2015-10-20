/*
 * Copyright 2015 Rahel Lüthy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.netzwerg.paleo.schema;

import ch.netzwerg.paleo.ColumnType;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class SchemaTest {

    private static final String JSON = "{\n" +
            "  \"title\": \"Paleo Schema Title\",\n" +
            "  \"dataFileName\": \"data.txt\",\n" +
            "  \"fields\": [\n" +
            "    {\n" +
            "      \"name\": \"Foo\",\n" +
            "      \"type\": \"Int\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Bar\",\n" +
            "      \"type\": \"Timestamp\",\n" +
            "      \"format\": \"yyyyMMddHHmmss\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"\",\n" +
            "      \"type\": \"Category\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    @Test
    public void parse() throws IOException {
        Schema schema = Schema.parseJson(new StringReader(JSON));
        assertEquals("Paleo Schema Title", schema.getTitle());
        assertEquals("data.txt", schema.getDataFileName());
        assertEquals(3, schema.getFields().size());

        Field fooField = schema.getFields().get(0);
        assertEquals("Foo", fooField.getName());
        assertEquals(ColumnType.INT, fooField.getType());
        assertFalse(fooField.getFormat().isPresent());

        Field barField = schema.getFields().get(1);
        assertEquals("Bar", barField.getName());
        assertEquals(ColumnType.TIMESTAMP, barField.getType());
        assertEquals("yyyyMMddHHmmss", barField.getFormat().get());

        Field emptyField = schema.getFields().get(2);
        assertEquals(Field.DEFAULT_NAME, emptyField.getName());
        assertEquals(ColumnType.CATEGORY, emptyField.getType());
        assertFalse(emptyField.getFormat().isPresent());
    }

}
