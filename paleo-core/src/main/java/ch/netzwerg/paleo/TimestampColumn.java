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

import javaslang.collection.Array;
import javaslang.collection.List;
import javaslang.collection.Stream;

import java.time.Instant;

import static ch.netzwerg.paleo.ColumnIds.TimestampColumnId;

public final class TimestampColumn extends AbstractColumn<Instant, TimestampColumnId> {

    private TimestampColumn(TimestampColumnId id, Array<Instant> values) {
        super(id, values);
    }

    public static TimestampColumn of(TimestampColumnId id, Instant value) {
        return builder(id).add(value).build();
    }

    public static TimestampColumn ofAll(TimestampColumnId id, Instant... values) {
        return builder(id).addAll(values).build();
    }

    public static TimestampColumn ofAll(TimestampColumnId id, Iterable<Instant> values) {
        return builder(id).addAll(values).build();
    }

    public static Builder builder(TimestampColumnId id) {
        return new Builder(id, List.empty());
    }

    public static final class Builder implements Column.Builder<Instant, TimestampColumn> {

        private final TimestampColumnId id;
        private final List<Instant> acc;

        private Builder(TimestampColumnId id, List<Instant> acc) {
            this.id = id;
            this.acc = acc;
        }

        @Override
        public Builder add(Instant value) {
            return new Builder(id, acc.prepend(value));
        }

        public Builder addAll(Instant... values) {
            return addAll(Stream.of(values));
        }

        public Builder addAll(Iterable<Instant> values) {
            return new Builder(id, acc.prependAll(List.ofAll(values).reverse()));
        }

        @Override
        public TimestampColumn build() {
            return new TimestampColumn(this.id, this.acc.toStream().reverse().toArray());
        }

    }

}
