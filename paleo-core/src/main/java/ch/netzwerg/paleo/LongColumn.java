package ch.netzwerg.paleo;

import ch.netzwerg.paleo.impl.MetaDataBuilder;
import javaslang.collection.Map;
import javaslang.collection.Stream;

import java.util.Arrays;
import java.util.stream.LongStream;

/**
 * Created by pieter on 20/04/16
 * Copyright Genomics Core
 */
public class LongColumn  implements Column<ColumnIds.LongColumnId> {

    private final ColumnIds.LongColumnId id;
    private final long[] values;
    private final Map<String, String> metaData;

    private LongColumn(ColumnIds.LongColumnId id, LongStream values, Map<String, String> metaData) {
        this.id = id;
        this.values = values.toArray();
        this.metaData = metaData;
    }


    public static LongColumn of(ColumnIds.LongColumnId id, long value) {
        return builder(id).add(value).build();
    }

    public static LongColumn ofAll(ColumnIds.LongColumnId id, long... values) {
        return builder(id).addAll(values).build();
    }

    public static LongColumn ofAll(ColumnIds.LongColumnId id, LongStream values) {
        return builder(id).addAll(values).build();
    }





    @Override
    public ColumnIds.LongColumnId getId() {
        return id;
    }

    @Override
    public int getRowCount() {
        return values.length;
    }

    @Override
    public Map<String, String> getMetaData() {
        return metaData;
    }

    public long getValueAt(int index) {
        return values[index];
    }

    public LongStream valueStream() {
        return Arrays.stream(values);
    }

    public static Builder builder(ColumnIds.LongColumnId id) {
        return new Builder(id);
    }



    public static final class Builder implements Column.Builder<Long, LongColumn> {

        private final ColumnIds.LongColumnId id;
        private final LongStream.Builder valueBuilder;
        private final MetaDataBuilder metaDataBuilder;

        private Builder(ColumnIds.LongColumnId id) {
            this.id = id;
            this.valueBuilder = LongStream.builder();
            this.metaDataBuilder = new MetaDataBuilder();
        }

        @Override
        public Builder add(Long value) {
            valueBuilder.add(value);
            return this;
        }

        public Builder add(Integer value) {
            valueBuilder.add(value);
            return this;
        }

        public Builder addAll(long... values) {
            return addAll(Arrays.stream(values));
        }

        public Builder addAll(Iterable<Long> values){
            for (Long value : values) {
                this.valueBuilder.add(value);
            }
            return this;
        }

        public Builder addAll(LongStream values) {
            values.forEachOrdered(this::add);
            return this;
        }

        @Override
        public Builder putMetaData(String key, String value) {
            metaDataBuilder.putMetaData(key, value);
            return this;
        }

        @Override
        public Builder putAllMetaData(Map<String, String> metaData) {
            metaDataBuilder.putAllMetaData(metaData);
            return this;
        }

        @Override
        public LongColumn build() {
            return new LongColumn(id, valueBuilder.build(), metaDataBuilder.build());
        }

    }

}
