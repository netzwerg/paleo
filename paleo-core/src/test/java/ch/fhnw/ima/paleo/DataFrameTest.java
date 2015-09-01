package ch.fhnw.ima.paleo;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;

import java.io.File;
import java.time.Instant;
import java.util.Arrays;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import static ch.fhnw.ima.paleo.ColumnIds.*;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class DataFrameTest {

    private static final StringColumnId NAME = ColumnIds.stringCol("Name");
    private static final IntColumnId AGE = ColumnIds.intCol("Age");
    private static final DoubleColumnId HEIGHT = ColumnIds.doubleCol("Height");
    private static final TimestampColumnId DATE_OF_BIRTH = ColumnIds.timestampCol("Date Of Birth");
    private static final CategoryColumnId GENDER = ColumnIds.categoryCol("Gender");

    private static final Instant AUG_26_1975 = Instant.parse("1975-08-26T12:08:30.00Z");
    private static final Instant JAN_08_2008 = Instant.parse("2006-01-08T23:43:30.00Z");
    private static final Instant OCT_26_1947 = Instant.parse("1947-10-26T03:23:36.00Z");

    @Test
    public void defaultColumnTypes() {

        StringColumn nameColumn = new StringColumn(NAME, asList("Ada", "Homer", "Hillary"));
        IntColumn ageColumn = new IntColumn(AGE, IntStream.of(42, 99, 67));
        DoubleColumn heightColumn = new DoubleColumn(HEIGHT, DoubleStream.of(1.74, 1.20, 1.70));
        TimestampColumn dateOfBirthColumn = new TimestampColumn(DATE_OF_BIRTH, asList(AUG_26_1975, JAN_08_2008, OCT_26_1947));
        CategoryColumn genderColumn = CategoryColumn.builder(GENDER).addAll("Female", "Male", "Female").build();

        DataFrame df = new DataFrame(3, nameColumn, ageColumn, heightColumn, dateOfBirthColumn, genderColumn);

        assertEquals(3, df.getRowCount());
        assertEquals(5, df.getColumnCount());

        assertEquals(Arrays.asList(NAME, AGE, HEIGHT, DATE_OF_BIRTH, GENDER), df.getColumnIds());
        assertEquals(Arrays.asList("Name", "Age", "Height", "Date Of Birth", "Gender"), df.getColumnNames());

        assertEquals("String", NAME.getType().getDescription());
        assertEquals(NAME, df.getColumnId(0, StringColumnId.class));
        assertEquals(nameColumn, df.getColumn(NAME));
        assertEquals(asList("Ada", "Homer", "Hillary"), nameColumn.getValues().collect(toList()));

        assertEquals("Int", AGE.getType().getDescription());
        assertEquals(AGE, df.getColumnId(1, IntColumnId.class));
        assertEquals(ageColumn, df.getColumn(AGE));
        assertArrayEquals(new int[]{42, 99, 67}, ageColumn.getValues().toArray());

        assertEquals("Double", HEIGHT.getType().getDescription());
        assertEquals(HEIGHT, df.getColumnId(2, DoubleColumnId.class));
        assertEquals(heightColumn, df.getColumn(HEIGHT));
        assertArrayEquals(new double[]{1.74, 1.20, 1.70}, heightColumn.getValues().toArray(), 0.01);

        assertEquals("Timestamp", DATE_OF_BIRTH.getType().getDescription());
        assertEquals(DATE_OF_BIRTH, df.getColumnId(3, TimestampColumnId.class));
        assertEquals(dateOfBirthColumn, df.getColumn(DATE_OF_BIRTH));
        assertEquals(asList(AUG_26_1975, JAN_08_2008, OCT_26_1947), dateOfBirthColumn.getValues().collect(toList()));

        assertEquals("Category", GENDER.getType().getDescription());
        assertEquals(GENDER, df.getColumnId(4, CategoryColumnId.class));
        assertEquals(genderColumn, df.getColumn(GENDER));
        assertEquals(ImmutableSet.of("Female", "Male"), genderColumn.getCategories());

        // typed random access for e.g. String
        String stringValue = df.getValueAt(0, NAME);
        assertEquals("Ada", stringValue);

        // typed random access for primitive ints
        int intValue = df.getValueAt(1, AGE);
        assertEquals(99, intValue);

        // typed random access for primitive doubles
        double doubleValue = df.getValueAt(0, HEIGHT);
        assertEquals(1.74, doubleValue, 0.01);

        // typed random access for timestamps
        Instant timestampValue = df.getValueAt(1, DATE_OF_BIRTH);
        assertEquals(JAN_08_2008, timestampValue);

        // Typed random access for categories
        String categoryValue = df.getValueAt(2, GENDER);
        assertEquals("Female", categoryValue);

    }

    @Test
    public void customColumnTypes() {
        GenericColumnId fileColumnId = new GenericColumnId("File", new ColumnType("File"));
        File fileA = new File("/path/to/a.txt");
        File fileB = new File("/path/to/b.txt");
        GenericColumn<File,GenericColumnId> fileColumn = new GenericColumn<>(fileColumnId, Arrays.asList(fileA, fileB));

        DataFrame df = new DataFrame(2, fileColumn);
        assertEquals(2, df.getRowCount());
        assertEquals(1, df.getColumnCount());

        GenericColumn<File, GenericColumnId> column = df.getColumn(fileColumnId);
        assertEquals(fileColumn, column);

        File fileValue = df.getValueAt(1, fileColumnId);
        assertEquals(fileB, fileValue);
    }

}