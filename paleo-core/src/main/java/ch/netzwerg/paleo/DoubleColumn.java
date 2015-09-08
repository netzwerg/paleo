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

import java.util.Arrays;
import java.util.stream.DoubleStream;

import static ch.netzwerg.paleo.ColumnIds.DoubleColumnId;

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
    public DoubleColumnId getId() {
        return this.id;
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

        public Builder addAll(double ... values) {
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