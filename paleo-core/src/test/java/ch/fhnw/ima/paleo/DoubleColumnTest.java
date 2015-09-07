package ch.fhnw.ima.paleo;

import org.junit.Test;

import static ch.fhnw.ima.paleo.ColumnIds.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class DoubleColumnTest {

    @Test
    public void builder() {
        DoubleColumnId id = doubleCol("test");
        DoubleColumn column = DoubleColumn.builder(id).add(1).addAll(2, 9).add(0).build();
        assertEquals(id, column.getId());
        assertEquals(4, column.getRowCount());
        assertEquals(1, column.getValueAt(0), 0.01);
        assertEquals(0, column.getValueAt(column.getRowCount() - 1), 0.01);
        assertArrayEquals(new double[]{1, 2, 9, 0}, column.getValues().toArray(), 0.01);
    }

}