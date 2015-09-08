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

import java.time.Instant;
import java.util.List;

import static ch.fhnw.ima.paleo.ColumnIds.TimestampColumnId;

public final class TimestampColumn extends GenericColumn<Instant, TimestampColumnId> {

    public TimestampColumn(TimestampColumnId id, List<Instant> values) {
        super(id, values);
    }

    public static Builder builder(TimestampColumnId id) {
        return new Builder(id);
    }

    public static final class Builder extends GenericColumn.Builder<Instant, TimestampColumnId, TimestampColumn> {

        public Builder(TimestampColumnId id) {
            super(id);
        }

        @Override
        public TimestampColumn build() {
            return new TimestampColumn(this.id, this.valueBuilder.build());
        }

    }

}
