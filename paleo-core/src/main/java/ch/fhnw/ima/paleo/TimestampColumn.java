package ch.fhnw.ima.paleo;

import java.time.Instant;
import java.util.List;

import static ch.fhnw.ima.paleo.ColumnIds.TimestampColumnId;

public final class TimestampColumn extends GenericColumn<Instant, TimestampColumnId> {

    public TimestampColumn(TimestampColumnId id, List<Instant> values) {
        super(id, values);
    }

    public static Builder builder(TimestampColumnId id) {
        return new Builder(id);
    }

    public static final class Builder extends GenericColumn.Builder<Instant, TimestampColumnId, TimestampColumn> {

        public Builder(TimestampColumnId id) {
            super(id);
        }

        @Override
        public TimestampColumn build() {
            return new TimestampColumn(this.id, this.valueBuilder.build());
        }

    }

}
