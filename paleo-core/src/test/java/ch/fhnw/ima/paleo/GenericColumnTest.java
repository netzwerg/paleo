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

package ch.fhnw.ima.paleo;

import org.junit.Test;

import java.io.File;
import java.util.Arrays;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

public class GenericColumnTest {

    private static final File FILE_A = new File("/path/to/a.txt");
    private static final File FILE_B = new File("/path/to/b.txt");

    @Test
    public void builder() {
        ColumnType<ColumnIds.GenericColumnId> type = new ColumnType<>("CUSTOM", ColumnIds.GenericColumnId.class);
        ColumnIds.GenericColumnId id = ColumnIds.genericCol("test", type);
        GenericColumn.Builder<File, ColumnIds.GenericColumnId, GenericColumn<File, ColumnIds.GenericColumnId>> builder =
                new GenericColumn.Builder<File, ColumnIds.GenericColumnId, GenericColumn<File, ColumnIds.GenericColumnId>>(id) {
                    @Override
                    public GenericColumn<File, ColumnIds.GenericColumnId> build() {
                        return new GenericColumn<>(this.id, this.valueBuilder.build());
                    }
                };


        GenericColumn<File, ColumnIds.GenericColumnId> column = builder.add(FILE_A).add(FILE_B).addAll(FILE_A, FILE_A).build();
        assertEquals(id, column.getId());
        assertEquals(4, column.getRowCount());
        assertEquals(FILE_B, column.getValueAt(1));
        assertEquals(Arrays.asList(FILE_A, FILE_B, FILE_A, FILE_A), column.getValues().collect(toList()));
    }

}