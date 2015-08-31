package ch.fhnw.ima.paleo;

import org.junit.Test;

import static ch.fhnw.ima.paleo.ColumnIds.*;
import static org.junit.Assert.*;

public class IntColumnTest {

    @Test
    public void builder() {
        IntColumnId id = intCol("test");
        IntColumn column = IntColumn.builder(id).add(42).addAll(33, 69).add(99).build();
        assertEquals(id, column.getColumnId());
        assertEquals(4, column.getRowCount());
        assertEquals(42, column.getValueAt(0));
        assertEquals(99, column.getValueAt(column.getRowCount() - 1));
        assertArrayEquals(new int[]{42, 33, 69, 99}, column.getValues().toArray());
    }

}