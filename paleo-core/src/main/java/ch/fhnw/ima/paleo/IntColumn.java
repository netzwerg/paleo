package ch.fhnw.ima.paleo;

import java.util.Arrays;
import java.util.stream.IntStream;

import static ch.fhnw.ima.paleo.ColumnIds.IntColumnId;

public final class IntColumn implements Column<IntColumnId> {

    private final IntColumnId id;
    private final int[] values;

    public IntColumn(IntColumnId id, IntStream values) {
        this.id = id;
        this.values = values.toArray();
    }

    @Override
    public IntColumnId getColumnId() {
        return id;
    }

    @Override
    public int getRowCount() {
        return this.values.length;
    }

    public int getValueAt(int index) {
        return this.values[index];
    }

    public IntStream getValues() {
        return Arrays.stream(this.values);
    }

}