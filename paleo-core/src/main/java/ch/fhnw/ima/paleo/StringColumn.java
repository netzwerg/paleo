package ch.fhnw.ima.paleo;

import java.util.List;

import static ch.fhnw.ima.paleo.ColumnIds.*;

public final class StringColumn extends GenericColumn<String, StringColumnId> {

    public StringColumn(StringColumnId id, List<String> values) {
        super(id, values);
    }

    public static Builder builder(StringColumnId id) {
        return new Builder(id);
    }

    public static class Builder extends GenericColumn.Builder<String, StringColumnId, StringColumn> {

        public Builder(StringColumnId id) {
            super(id);
        }

        @Override
        public StringColumn build() {
            return new StringColumn(this.id, this.valueBuilder.build());
        }

    }

}