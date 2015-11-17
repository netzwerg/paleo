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

import java.io.File;

import static org.junit.Assert.assertEquals;

public class GenericColumnTest {

    private static final File FILE_A = new File("/path/to/a.txt");
    private static final File FILE_B = new File("/path/to/b.txt");

    @Test
    public void builder() {
        ColumnType<ColumnIds.GenericColumnId> type = new ColumnType<>("CUSTOM", ColumnIds.GenericColumnId.class);
        ColumnIds.GenericColumnId id = ColumnIds.genericCol("test", type);
        GenericColumn<File, ColumnIds.GenericColumnId> column = GenericColumn.ofAll(id, FILE_A, FILE_B, FILE_A, FILE_A);
        assertEquals(id, column.getId());
        assertEquals(4, column.getRowCount());
        assertEquals(FILE_B, column.getValueAt(1));
        assertEquals(Array.ofAll(FILE_A, FILE_B, FILE_A, FILE_A), column.getValues().toArray());
    }

}