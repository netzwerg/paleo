package ch.netzwerg.paleo;


import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by pieter on 20/04/16
 * Copyright Genomics Core
 */
public class LongColumnTest extends AbstractBaseColumnTest<Long, LongColumn> {
    private static final ColumnIds.LongColumnId ID = ColumnIds.LongColumnId.of("test");

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
