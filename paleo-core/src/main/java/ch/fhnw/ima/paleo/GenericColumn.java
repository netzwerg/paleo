package ch.fhnw.ima.paleo;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.stream.Stream;

import static ch.fhnw.ima.paleo.ColumnIds.GenericColumnId;

public class GenericColumn<V, I extends GenericColumnId> implements Column<I> {

    private final I id;
    private final ImmutableList<V> values;

    protected GenericColumn(I id, List<V> values) {
        this.id = id;
        this.values = ImmutableList.copyOf(values);
    }

    @Override
    public I getColumnId() {
        return this.id;
    }

    @Override
    public int getRowCount() {
        return this.values.size();
    }

    public V getValueAt(int index) {
        return this.values.get(index);
    }

    public Stream<V> getValues() {
        return this.values.stream();
    }

    protected static abstract class Builder<V, I extends GenericColumnId, C extends GenericColumn<V, I>> implements Column.Builder<C> {

        protected final I id;
        protected final ImmutableList.Builder<V> valueBuilder;

        public Builder(I id) {
            this.id = id;
            this.valueBuilder = ImmutableList.builder();
        }

        public Builder<V, I, C> addAll(V... values) {
            for (V value : values) {
                add(value);
            }
            return this;
        }

        public Builder<V, I, C> add(V value) {
            this.valueBuilder.add(value);
            return this;
        }

    }

}