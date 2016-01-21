/*
 * Copyright 2016 Rahel Lüthy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.netzwerg.paleo;

import com.google.common.collect.ImmutableMap;

import java.util.BitSet;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class BooleanColumn implements Column<ColumnIds.BooleanColumnId> {

    private final ColumnIds.BooleanColumnId id;
    private final int rowCount;
    private final BitSet values;
    private final ImmutableMap<String, String> metaData;

    public BooleanColumn(ColumnIds.BooleanColumnId id, int rowCount, BitSet values) {
        this(id, rowCount, values, Collections.emptyMap());
    }

    public BooleanColumn(ColumnIds.BooleanColumnId id, int rowCount, BitSet values, Map<String, String> metaData) {
        this.id = id;
        this.rowCount = rowCount;
        this.values = (BitSet) values.clone();
        this.metaData = ImmutableMap.copyOf(metaData);
    }

    @Override
    public ColumnIds.BooleanColumnId getId() {
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

    @Override
    public ImmutableMap<String, String> getMetaData() {
        return metaData;
    }

    public static Builder builder(ColumnIds.BooleanColumnId id) {
        return new Builder(id);
    }

    public static final class Builder implements Column.Builder<BooleanColumn> {

        private final ColumnIds.BooleanColumnId id;
        private final AtomicInteger rowIndex;
        private final BitSet values;
        private final ImmutableMap.Builder<String, String> metaDataBuilder;

        public Builder(ColumnIds.BooleanColumnId id) {
            this.id = id;
            this.rowIndex = new AtomicInteger();
            this.values = new BitSet();
            this.metaDataBuilder = ImmutableMap.builder();
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
        public Builder putMetaData(String key, String value) {
            metaDataBuilder.put(key, value);
            return this;
        }

        @Override
        public Builder putAllMetaData(Map<String, String> metaData) {
            metaDataBuilder.putAll(metaData);
            return this;
        }

        @Override
        public BooleanColumn build() {
            return new BooleanColumn(id, rowIndex.get(), values, metaDataBuilder.build());
        }

    }

}
