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

}