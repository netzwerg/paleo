/*
 * Copyright 2015 Rahel Lüthy
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

import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class BooleanColumn implements Column<ColumnIds.BooleanColumnId> {

    private final ColumnIds.BooleanColumnId id;
    private final int rowCount;
    private final BitSet values;

    public BooleanColumn(ColumnIds.BooleanColumnId id, int rowCount, BitSet values) {
        this.id = id;
        this.rowCount = rowCount;
        this.values = (BitSet) values.clone();
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

    public static Builder builder(ColumnIds.BooleanColumnId id) {
        return new Builder(id);
    }

    public static final class Builder implements Column.Builder<BooleanColumn> {

        private final ColumnIds.BooleanColumnId id;
        private final AtomicInteger rowIndex;
        private final BitSet values;

        public Builder(ColumnIds.BooleanColumnId id) {
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
