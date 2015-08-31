package ch.fhnw.ima.paleo;

import java.time.Instant;
import java.util.List;

public class TimestampColumn extends GenericColumn<Instant> {
    public TimestampColumn(ColumnIds.GenericColumnId id, List<Instant> values) {
        super(id, values);
    }
}
