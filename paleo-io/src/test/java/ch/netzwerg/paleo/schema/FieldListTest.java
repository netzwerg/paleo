/*
 * Copyright 2016 Rahel Lüthy
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
import javaslang.control.Option;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class FieldListTest {

    private static final Field F1 = createTestField("test-field-1");
    private static final Field F2 = createTestField("test-field-2");

    @Test
    public void constructFromEmpty() {
        FieldList fields = new FieldList();
        fields.add(F1);
        fields.add(F2);
        assertListState(fields);
    }

    @Test
    public void constructFromList() {
        FieldList fields = new FieldList(Arrays.asList(F1, F2));
        assertListState(fields);
    }

    private static Field createTestField(String name) {
        return new Field(name, ColumnType.BOOLEAN, Option.none());
    }

    private static void assertListState(FieldList fields) {
        assertEquals(2, fields.size());
        assertEquals(F1, fields.get(0));
    }

}