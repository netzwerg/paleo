package ch.fhnw.ima.paleo;

import ch.fhnw.ima.paleo.ColumnIds.GenericColumnId;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.stream.Stream;

public class GenericColumn<T> implements Column<GenericColumnId> {

    private final GenericColumnId id;
    private final ImmutableList<T> values;

    public GenericColumn(GenericColumnId id, List<T> values) {
        this.id = id;
        this.values = ImmutableList.copyOf(values);
    }

    @Override
    public GenericColumnId getColumnId() {
        return this.id;
    }

    @Override
    public int getRowCount() {
        return this.values.size();
    }

    public T getValueAt(int index) {
        return this.values.get(index);
    }

    public Stream<T> getValues() {
        return this.values.stream();
    }

}