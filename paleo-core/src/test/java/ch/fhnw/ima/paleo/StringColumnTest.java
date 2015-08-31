package ch.fhnw.ima.paleo;

import org.junit.Test;

import static ch.fhnw.ima.paleo.ColumnIds.*;
import static org.junit.Assert.*;

public class StringColumnTest {

    @Test
    public void build() {
        StringColumnId id = stringCol("test");
        StringColumn.Builder builder = StringColumn.builder(id);
        builder.add("bli").addAll("bla", "blu").add("zzz");
        StringColumn column = builder.build();
        assertEquals(id, column.getId());
        assertEquals(4, column.getRowCount());
        assertEquals("bli", column.getValueAt(0));
        assertEquals("zzz", column.getValueAt(column.getRowCount() - 1));
    }

}