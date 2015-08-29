package ch.fhnw.ima.paleo;

import java.time.Instant;
import java.util.List;

public class InstantColumn extends GenericColumn<Instant> {
    public InstantColumn(ColumnIds.GenericColumnId id, List<Instant> values) {
        super(id, values);
    }
}
