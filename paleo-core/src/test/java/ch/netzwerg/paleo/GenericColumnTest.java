/*
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

import ch.netzwerg.paleo.ColumnIds.GenericColumnId;
import io.vavr.collection.Array;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class GenericColumnTest {

    private static final ColumnType<GenericColumnId> TYPE = new ColumnType<>("CUSTOM", GenericColumnId.class);
    public static final GenericColumnId ID = GenericColumnId.of("test", TYPE);

    private static final File FILE_A = new File("/path/to/a.txt");
    private static final File FILE_B = new File("/path/to/b.txt");

    @Test
    public void of() {
        GenericColumn<File, GenericColumnId> column = GenericColumn.of(ID, FILE_A);
        assertEquals(ID, column.getId());
        assertEquals(1, column.getRowCount());
        assertEquals(FILE_A, column.getValueAt(0));
        assertEquals(Array.of(FILE_A), column.getValues().toArray());
    }

    @Test
    public void ofAllVarArgs() {
        GenericColumn<File, GenericColumnId> column = GenericColumn.ofAll(ID, FILE_A, FILE_B, FILE_A, FILE_A);
        assertMultipleValues(column);
    }

    @Test
    public void ofAllIterable() {
        Iterable<File> iterable = () -> Array.of(FILE_A, FILE_B, FILE_A, FILE_A).iterator();
        GenericColumn<File, GenericColumnId> column = GenericColumn.ofAll(ID, iterable);
        assertMultipleValues(column);
    }

    private static void assertMultipleValues(GenericColumn<File, GenericColumnId> column) {
        assertEquals(ID, column.getId());
        assertEquals(4, column.getRowCount());
        assertEquals(FILE_B, column.getValueAt(1));
        assertEquals(Array.of(FILE_A, FILE_B, FILE_A, FILE_A), column.getValues().toArray());
    }

}