package ch.fhnw.ima.paleo;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CategoryColumnTest {

    @Test
    public void builder() {
        ColumnIds.CategoryColumnId id = ColumnIds.categoryCol("test");
        CategoryColumn.Builder builder = CategoryColumn.builder(id);
        builder.add("foo").add("bar").addAll("foo", "baz", "bar").add("foo");
        CategoryColumn column = builder.build();
        assertEquals(id, column.getColumnId());
        assertEquals(6, column.getRowCount());
        assertEquals(ImmutableSet.of("foo", "bar", "baz"), column.getCategories());
        assertEquals("foo", column.getValueAt(0));
        assertEquals("bar", column.getValueAt(1));

    }


}