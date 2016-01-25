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

import javaslang.collection.Array;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BooleanColumnTest extends AbstractBaseColumnTest<Boolean, BooleanColumn> {

    private static final ColumnIds.BooleanColumnId ID = ColumnIds.BooleanColumnId.of("test");

    @Override
    protected BooleanColumn.Builder builder() {
        return BooleanColumn.builder(ID);
    }

    @Test
    public void valueTypeSpecificBuilding() {
        BooleanColumn column = builder().add(true).addAll(false, false, true).add(false).build();
        assertEquals(ID, column.getId());
        assertEquals(5, column.getRowCount());
        assertEquals(true, column.getValueAt(0));
        assertEquals(false, column.getValueAt(column.getRowCount() - 1));
        assertEquals(Array.of(true, false, false, true, false), column.getValues().toArray());
    }

}