package ch.fhnw.ima.paleo;

import java.util.Arrays;
import java.util.stream.DoubleStream;

import static ch.fhnw.ima.paleo.ColumnIds.DoubleColumnId;

public final class DoubleColumn implements Column<DoubleColumnId> {

    private final DoubleColumnId id;
    private final double[] values;

    public DoubleColumn(DoubleColumnId id, DoubleStream values) {
        this.id = id;
        this.values = values.toArray();
    }

    public static Builder builder(DoubleColumnId id) {
        return new Builder(id);
    }

    @Override
    public DoubleColumnId getColumnId() {
        return id;
    }

    @Override
    public int getRowCount() {
        return this.values.length;
    }

    public double getValueAt(int index) {
        return this.values[index];
    }

    public DoubleStream getValues() {
        return Arrays.stream(this.values);
    }

    public static final class Builder implements Column.Builder<DoubleColumn> {

        private final DoubleColumnId id;
        private final DoubleStream.Builder valueBuilder;

        public Builder(DoubleColumnId id) {
            this.id = id;
            this.valueBuilder = DoubleStream.builder();
        }

        public Builder add(double ... values) {
            for (double value: values) {
                add(value);
            }
            return this;
        }

        public Builder add(double value) {
            this.valueBuilder.add(value);
            return this;
        }

        @Override
        public DoubleColumn build() {
            return new DoubleColumn(this.id, this.valueBuilder.build());
        }

    }

}