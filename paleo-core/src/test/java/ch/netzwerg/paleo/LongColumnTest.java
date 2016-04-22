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

import static ch.netzwerg.paleo.ColumnIds.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class LongColumnTest extends AbstractBaseColumnTest<Long, LongColumn> {

    private static final LongColumnId ID = LongColumnId.of("test");

    @Override
    protected LongColumn.Builder builder() {
        return LongColumn.builder(ID);
    }

    @Test
    public void valueTypeSpecificBuilding() {
        LongColumn column = builder().add(42).addAll(33, 69).add(99).add(7093740276L).build();
        assertEquals(ID, column.getId());
        assertEquals(5, column.getRowCount());
        assertEquals(42, column.getValueAt(0));
        assertEquals(99, column.getValueAt(column.getRowCount() - 2));
        assertArrayEquals(new long[]{42L, 33L, 69L, 99L, 7093740276L}, column.valueStream().toArray());
    }

}
