package ch.fhnw.ima.paleo;

import java.util.List;

public final class StringColumn extends GenericColumn<String> {

    public StringColumn(ColumnIds.StringColumnId id, List<String> values) {
        super(id, values);
    }

}