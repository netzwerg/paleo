package ch.fhnw.ima.paleo;

import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static ch.fhnw.ima.paleo.ColumnIds.BooleanColumnId;

public final class BooleanColumn implements Column<BooleanColumnId> {

    private final BooleanColumnId id;
    private final int rowCount;
    private final BitSet values;

    public BooleanColumn(BooleanColumnId id, int rowCount, BitSet values) {
        this.id = id;
        this.rowCount = rowCount;
        this.values = (BitSet) values.clone();
    }

    @Override
    public BooleanColumnId getId() {
        return this.id;
    }

    @Override
    public int getRowCount() {
        return this.rowCount;
    }
    
    public boolean getValueAt(int rowIndex) {
        return this.values.get(rowIndex);
    }

    public Stream<Boolean> getValues() {
        IntStream rowIndexStream = IntStream.range(0, this.rowCount);
        return rowIndexStream.mapToObj(this.values::get);
    }

    public static Builder builder(BooleanColumnId id) {
        return new Builder(id);
    }

    public static final class Builder implements Column.Builder<BooleanColumn> {

        private final BooleanColumnId id;
        private final AtomicInteger rowIndex;
        private final BitSet values;

        public Builder(BooleanColumnId id) {
            this.id = id;
            this.rowIndex = new AtomicInteger();
            this.values = new BitSet();
        }

        public Builder addAll(boolean... values) {
            for (boolean value : values) {
                add(value);
            }
            return this;
        }

        public Builder add(boolean value) {
            this.values.set(this.rowIndex.getAndIncrement(), value);
            return this;
        }

        @Override
        public BooleanColumn build() {
            return new BooleanColumn(this.id, this.rowIndex.get(), this.values);
        }

    }

}
