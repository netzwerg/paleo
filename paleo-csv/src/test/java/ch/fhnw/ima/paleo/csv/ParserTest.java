package ch.fhnw.ima.paleo.csv;

import ch.fhnw.ima.paleo.*;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.time.*;
import java.util.Arrays;
import java.util.function.Function;

import static ch.fhnw.ima.paleo.ColumnIds.*;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;

public class ParserTest {

    public static final String CONTENTS = "Name\tAge\tHeight\tDate Of Birth\tGender\n" +
            "String\tInt\tDouble\tInstant\tFactor\n" +
            "Ada\t42\t1.74\t19750826050916\tFemale\n" +
            "Homer\t99\t1.20\t20060108050916\tMale\n" +
            "Hillary\t67\t1.70\t19471026050916\tFemale\n";

    @Test
    public void parseTabDelimited() throws IOException {
        StringReader reader = new StringReader(CONTENTS);

        DataFrame df = Parser.parseTabDelimited(reader, "yyyyMMddHHmmss");
        assertEquals(5, df.getColumnCount());
        assertEquals(Arrays.asList("Name", "Age", "Height", "Date Of Birth", "Gender"), df.getColumnNames());

        StringColumnId nameColumnId = df.getColumnId(0, StringColumnId.class);
        StringColumn nameColumn = df.getColumn(nameColumnId);
        assertEquals(asList("Ada", "Homer", "Hillary"), nameColumn.getValues().collect(toList()));

        IntColumnId ageColumnId = df.getColumnId(1, IntColumnId.class);
        IntColumn ageColumn = df.getColumn(ageColumnId);
        assertArrayEquals(new int[]{42, 99, 67}, ageColumn.getValues().toArray());

        DoubleColumnId heightColumnId = df.getColumnId(2, DoubleColumnId.class);
        DoubleColumn heightColumn = df.getColumn(heightColumnId);
        assertArrayEquals(new double[]{1.74, 1.20, 1.70}, heightColumn.getValues().toArray(), 0.01);

        InstantColumnId dateOfBirthColumnId = df.getColumnId(3, InstantColumnId.class);
        InstantColumn dateOfBirthColumn = df.getColumn(dateOfBirthColumnId);
        Function<? super Instant, Month> toMonth = instant -> instant.atZone(ZoneId.from(ZoneOffset.UTC)).getMonth();
        assertEquals(asList(Month.AUGUST, Month.JANUARY, Month.OCTOBER), dateOfBirthColumn.getValues().map(toMonth).collect(toList()));

        FactorColumnId genderColumnId = df.getColumnId(4, FactorColumnId.class);
        FactorColumn genderColumn = df.getColumn(genderColumnId);
        assertEquals(ImmutableSet.of("Female", "Male"), genderColumn.getFactors());

        // typed random access for e.g. String
        String stringValue = df.getValueAt(0, nameColumnId);
        assertEquals("Ada", stringValue);

        // typed random access for primitive ints
        int intValue = df.getValueAt(1, ageColumnId);
        assertEquals(99, intValue);

        // typed random access for primitive doubles
        double doubleValue = df.getValueAt(0, heightColumnId);
        assertEquals(1.74, doubleValue, 0.01);

        String factorValue = df.getValueAt(2, genderColumnId);
        assertEquals("Female", factorValue);
    }

}