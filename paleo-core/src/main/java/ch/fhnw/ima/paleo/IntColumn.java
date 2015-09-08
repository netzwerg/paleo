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

    public static Builder builder(IntColumnId id) {
        return new Builder(id);
    }

    @Override
    public IntColumnId getId() {
        return this.id;
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

    public static final class Builder implements Column.Builder<IntColumn> {

        private final IntColumnId id;
        private final IntStream.Builder valueBuilder;

        public Builder(IntColumnId id) {
            this.id = id;
            this.valueBuilder = IntStream.builder();
        }

        public Builder addAll(int... values) {
            for (int value : values) {
                add(value);
            }
            return this;
        }

        public Builder add(int value) {
            this.valueBuilder.add(value);
            return this;
        }

        @Override
        public IntColumn build() {
            return new IntColumn(this.id, this.valueBuilder.build());
        }

    }

}