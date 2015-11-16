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

package ch.netzwerg.paleo;

import javaslang.collection.Array;
import org.junit.Test;

import java.time.Instant;

import static ch.netzwerg.paleo.ColumnIds.TimestampColumnId;
import static ch.netzwerg.paleo.ColumnIds.timestampCol;
import static org.junit.Assert.assertEquals;

public class TimestampColumnTest {

    private static final Instant AUG_26_1975 = Instant.parse("1975-08-26T12:08:30.00Z");
    private static final Instant JAN_08_2008 = Instant.parse("2006-01-08T23:43:30.00Z");
    private static final Instant OCT_26_1947 = Instant.parse("1947-10-26T03:23:36.00Z");

    @Test
    public void builder() {
        TimestampColumnId id = timestampCol("test");
        TimestampColumn column = TimestampColumn.builder(id).add(AUG_26_1975).addAll(JAN_08_2008, OCT_26_1947).build();
        assertEquals(id, column.getId());
        assertEquals(3, column.getRowCount());
        assertEquals(JAN_08_2008, column.getValueAt(1));
        assertEquals(Array.ofAll(AUG_26_1975, JAN_08_2008, OCT_26_1947), column.getValues());
    }

}