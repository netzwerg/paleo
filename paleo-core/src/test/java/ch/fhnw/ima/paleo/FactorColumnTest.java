package ch.fhnw.ima.paleo;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FactorColumnTest {

    @Test
    public void builder() {
        ColumnIds.FactorColumnId id = ColumnIds.factorCol("test");
        FactorColumn.Builder builder = FactorColumn.builder(id);
        builder.add("foo").add("bar").addAll("foo", "baz", "bar").add("foo");
        FactorColumn column = builder.build();
        assertEquals(id, column.getColumnId());
        assertEquals(6, column.getRowCount());
        assertEquals(ImmutableSet.of("foo", "bar", "baz"), column.getFactors());
        assertEquals("foo", column.getValueAt(0));
        assertEquals("bar", column.getValueAt(1));

    }


}