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
        ColumnType type = new ColumnType("CUSTOM");
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