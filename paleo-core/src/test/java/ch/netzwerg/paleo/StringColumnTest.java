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

package ch.netzwerg.paleo;

import org.junit.Test;

import static ch.netzwerg.paleo.ColumnIds.StringColumnId;
import static org.junit.Assert.assertEquals;

public class StringColumnTest extends AbstractColumnTest<String, StringColumn> {

    private static final StringColumnId ID = StringColumnId.of("test");

    @Override
    String generateValue() {
        return String.valueOf(System.currentTimeMillis());
    }

    @Override
    protected StringColumn.Builder builder() {
        return StringColumn.builder(ID);
    }

    @Test
    public void valueTypeSpecificBuilding() {
        StringColumn column = builder().add("bli").addAll("bla", "blu").add("zzz").build();
        assertEquals(ID, column.getId());
        assertEquals(4, column.getRowCount());
        assertEquals("bli", column.getValueAt(0));
        assertEquals("zzz", column.getValueAt(column.getRowCount() - 1));
    }

}