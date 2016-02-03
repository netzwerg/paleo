/*
 * Copyright 2015 Rahel Lüthy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.netzwerg.paleo;

import javaslang.collection.*;
import javaslang.control.Option;
import org.junit.Test;

import java.io.File;
import java.time.Instant;
import java.util.Arrays;

import static ch.netzwerg.paleo.ColumnIds.*;
import static org.junit.Assert.*;

public class DataFrameTest {

    private static final StringColumnId NAME = StringColumnId.of("Name");
    private static final IntColumnId AGE = IntColumnId.of("Age");
    private static final DoubleColumnId HEIGHT = DoubleColumnId.of("Height");
    private static final BooleanColumnId VEGETARIAN = BooleanColumnId.of("Vegetarian");
    private static final TimestampColumnId DATE_OF_BIRTH = TimestampColumnId.of("Date Of Birth");
    private static final CategoryColumnId GENDER = CategoryColumnId.of("Gender");

    private static final Instant AUG_26_1975 = Instant.parse("1975-08-26T12:08:30.00Z");
    private static final Instant JAN_08_2006 = Instant.parse("2006-01-08T23:43:30.00Z");
    private static final Instant OCT_26_1947 = Instant.parse("1947-10-26T03:23:36.00Z");

    @Test
    public void empty() {
        DataFrame dataFrame = DataFrame.empty();
        assertEquals(0, dataFrame.getRowCount());
        assertEquals(0, dataFrame.getColumnCount());
        assertTrue(dataFrame.getColumnIds().isEmpty());
        assertTrue(dataFrame.getColumnNames().isEmpty());
        assertTrue(dataFrame.getColumns().isEmpty());
    }

    @Test
    public void defaultColumnTypes() {

        StringColumn nameColumn = StringColumn.builder(NAME).addAll("Ada", "Homer", "Hillary").putMetaData("meta-data", "rocks").build();
        IntColumn ageColumn = IntColumn.ofAll(AGE, 42, 99, 67);
        DoubleColumn heightColumn = DoubleColumn.ofAll(HEIGHT, 1.74, 1.20, 1.70);
        BooleanColumn vegetarianColumn = BooleanColumn.ofAll(VEGETARIAN, true, false, false);
        TimestampColumn dateOfBirthColumn = TimestampColumn.ofAll(DATE_OF_BIRTH, AUG_26_1975, JAN_08_2006, OCT_26_1947);
        CategoryColumn genderColumn = CategoryColumn.ofAll(GENDER, "Female", "Male", "Female");

        DataFrame df = DataFrame.ofAll(nameColumn, ageColumn, heightColumn, vegetarianColumn, dateOfBirthColumn, genderColumn);

        assertEquals(3, df.getRowCount());
        assertEquals(6, df.getColumnCount());

        assertEquals(Array.of(NAME, AGE, HEIGHT, VEGETARIAN, DATE_OF_BIRTH, GENDER), df.getColumnIds());
        assertEquals(Array.of(nameColumn, ageColumn, heightColumn, vegetarianColumn, dateOfBirthColumn, genderColumn), df.getColumns());
        assertEquals(Array.of("Name", "Age", "Height", "Vegetarian", "Date Of Birth", "Gender"), df.getColumnNames());

        assertEquals("String", NAME.getType().getDescription());
        assertEquals(NAME, df.getColumnId(0, ColumnType.STRING));
        assertEquals(nameColumn, df.getColumn(NAME));
        assertEquals(Array.of("Ada", "Homer", "Hillary"), nameColumn.getValues());

        assertEquals("Int", AGE.getType().getDescription());
        assertEquals(AGE, df.getColumnId(1, ColumnType.INT));
        assertEquals(ageColumn, df.getColumn(AGE));
        assertArrayEquals(new int[]{42, 99, 67}, ageColumn.valueStream().toArray());

        assertEquals("Double", HEIGHT.getType().getDescription());
        assertEquals(HEIGHT, df.getColumnId(2, ColumnType.DOUBLE));
        assertEquals(heightColumn, df.getColumn(HEIGHT));
        assertArrayEquals(new double[]{1.74, 1.20, 1.70}, heightColumn.valueStream().toArray(), 0.01);

        assertEquals("Boolean", VEGETARIAN.getType().getDescription());
        assertEquals(VEGETARIAN, df.getColumnId(3, ColumnType.BOOLEAN));
        assertEquals(vegetarianColumn, df.getColumn(VEGETARIAN));
        assertEquals(Array.of(true, false, false), vegetarianColumn.valueStream().toArray());

        assertEquals("Timestamp", DATE_OF_BIRTH.getType().getDescription());
        assertEquals(DATE_OF_BIRTH, df.getColumnId(4, ColumnType.TIMESTAMP));
        assertEquals(dateOfBirthColumn, df.getColumn(DATE_OF_BIRTH));
        assertEquals(Array.of(AUG_26_1975, JAN_08_2006, OCT_26_1947), dateOfBirthColumn.getValues());

        assertEquals("Category", GENDER.getType().getDescription());
        assertEquals(GENDER, df.getColumnId(5, ColumnType.CATEGORY));
        assertEquals(genderColumn, df.getColumn(GENDER));
        assertEquals(HashSet.of("Female", "Male"), genderColumn.getCategories());

        // Column access via ColumnId interface (i.e. non type-specific)
        ColumnId nonSpecificId = nameColumn.getId();
        Column<?> column = df.getColumn(nonSpecificId);
        assertNotNull(column);
        assertEquals(Option.some("rocks"), column.getMetaData().get("meta-data"));

        // typed random access for String values
        String stringValue = df.getValueAt(0, NAME);
        assertEquals("Ada", stringValue);

        // typed random access for primitive ints
        int intValue = df.getValueAt(1, AGE);
        assertEquals(99, intValue);

        // typed random access for primitive doubles
        double doubleValue = df.getValueAt(0, HEIGHT);
        assertEquals(1.74, doubleValue, 0.01);

        // typed random access for booleans
        boolean booleanValue = df.getValueAt(2, VEGETARIAN);
        assertFalse(booleanValue);

        // typed random access for timestamps
        Instant timestampValue = df.getValueAt(1, DATE_OF_BIRTH);
        assertEquals(JAN_08_2006, timestampValue);

        // Typed random access for categories
        String categoryValue = df.getValueAt(2, GENDER);
        assertEquals("Female", categoryValue);
    }

    @Test
    public void customColumnTypes() {
        GenericColumnId fileColumnId = new GenericColumnId("File", new ColumnType<>("File", GenericColumnId.class));
        File fileA = new File("/path/to/a.txt");
        File fileB = new File("/path/to/b.txt");
        GenericColumn<File, GenericColumnId> fileColumn = GenericColumn.ofAll(fileColumnId, fileA, fileB);

        DataFrame df = DataFrame.of(fileColumn);
        assertEquals(2, df.getRowCount());
        assertEquals(1, df.getColumnCount());

        GenericColumn<File, GenericColumnId> column = df.getColumn(fileColumnId);
        assertEquals(fileColumn, column);

        File fileValue = df.getValueAt(1, fileColumnId);
        assertEquals(fileB, fileValue);
    }

    @Test(expected = IllegalArgumentException.class)
    public void failOnDifferingColumnSizes() {
        StringColumn oneRowColumn = StringColumn.builder(NAME).add("foo").build();
        IntColumn threeRowColumn = IntColumn.builder(AGE).addAll(1, 2, 3).build();
        DataFrame.ofAll(oneRowColumn, threeRowColumn);
    }

    @Test
    public void ofAll() {
        StringColumn stringColumn = StringColumn.builder(NAME).build();
        IntColumn intColumn = IntColumn.builder(AGE).build();

        // enum variant
        assertEquals(2, DataFrame.ofAll(stringColumn, intColumn).getColumnCount());

        // iterable variant
        Iterable<Column<?>> columns = Arrays.asList(stringColumn, intColumn);
        assertEquals(2, DataFrame.ofAll(columns).getColumnCount());
    }

    @Test
    public void iterator() {
        StringColumn stringColumn = StringColumn.builder(NAME).build();
        IntColumn intColumn = IntColumn.builder(AGE).build();

        Iterator<Column<?>> iterator = DataFrame.ofAll(stringColumn, intColumn).iterator();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertFalse(iterator.hasNext());
    }

    @Test
    public void withMetaData() {
        StringColumn stringColumn = StringColumn.builder(NAME).build();
        IntColumn intColumn = IntColumn.builder(AGE).build();
        DataFrame df = DataFrame.ofAll(stringColumn, intColumn);
        Map<String, String> metaData = LinkedHashMap.of("foo", "bar");
        DataFrame withMetaData = df.withMetaData(metaData);
        assertEquals(metaData, withMetaData.getMetaData());
        assertEquals(2, withMetaData.getColumnCount());
    }

}