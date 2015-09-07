package ch.fhnw.ima.paleo;

import ch.netzwerg.chabis.WordGenerator;
import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Random;

import static ch.fhnw.ima.paleo.ColumnIds.CategoryColumnId;
import static ch.fhnw.ima.paleo.ColumnIds.categoryCol;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

public class CategoryColumnTest {

    private WordGenerator wordGenerator;

    @Before
    public void before() {
        this.wordGenerator = new WordGenerator(new Random(42));
    }

    @Test
    public void builder() {
        CategoryColumnId id = categoryCol("test" );
        CategoryColumn.Builder builder = CategoryColumn.builder(id);
        builder.add("foo" ).add("bar" ).addAll("foo", "baz", "bar" ).add("foo" );
        CategoryColumn column = builder.build();
        assertEquals(id, column.getId());
        assertEquals(6, column.getRowCount());
        assertEquals(ImmutableSet.of("foo", "bar", "baz" ), column.getCategories());
        assertEquals("foo", column.getValueAt(0));
        assertEquals("bar", column.getValueAt(1));
    }

    @Test
    public void createValues() {
        CategoryColumnId id = categoryCol("test" );
        CategoryColumn.Builder builder = CategoryColumn.builder(id);
        List<String> values = this.wordGenerator.randomWords(100);
        CategoryColumn column = builder.addAll(values).build();
        assertEquals(93, column.getCategories().size());
        assertEquals(values, column.createValues().collect(toList()));
    }

}