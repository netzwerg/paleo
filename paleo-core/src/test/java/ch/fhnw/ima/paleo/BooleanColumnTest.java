package ch.fhnw.ima.paleo;

import org.junit.Test;

import static ch.fhnw.ima.paleo.ColumnIds.booleanCol;
import static org.junit.Assert.*;

public class BooleanColumnTest {

    @Test
    public void builder() {
        ColumnIds.BooleanColumnId id = booleanCol("test" );
        BooleanColumn column = BooleanColumn.builder(id).add(true).addAll(false, false, true).add(false).build();
        assertEquals(id, column.getId());
        assertEquals(5, column.getRowCount());
        assertEquals(true, column.getValueAt(0));
        assertEquals(false, column.getValueAt(column.getRowCount() - 1));
        assertArrayEquals(new Boolean[]{true, false, false, true, false}, column.getValues().toArray());
    }

}