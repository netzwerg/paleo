package ch.fhnw.ima.paleo;

import org.junit.Test;

import java.time.Instant;
import java.util.Arrays;

import static ch.fhnw.ima.paleo.ColumnIds.TimestampColumnId;
import static ch.fhnw.ima.paleo.ColumnIds.timestampCol;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

public class TimestampColumnTest {

    private static final Instant AUG_26_1975 = Instant.parse("1975-08-26T12:08:30.00Z");
    private static final Instant JAN_08_2008 = Instant.parse("2006-01-08T23:43:30.00Z");
    private static final Instant OCT_26_1947 = Instant.parse("1947-10-26T03:23:36.00Z");

    @Test
    public void builder() {
        TimestampColumnId id = timestampCol("test");
        TimestampColumn column = TimestampColumn.builder(id).add(AUG_26_1975).addAll(JAN_08_2008, OCT_26_1947).build();
        assertEquals(id, column.getId());
        assertEquals(3, column.getRowCount());
        assertEquals(JAN_08_2008, column.getValueAt(1));
        assertEquals(Arrays.asList(AUG_26_1975, JAN_08_2008, OCT_26_1947), column.getValues().collect(toList()));
    }

}