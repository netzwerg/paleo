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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static ch.netzwerg.paleo.ColumnIds.StringColumnId;

public final class StringColumn extends GenericColumn<String, StringColumnId> {

    public StringColumn(StringColumnId id, List<String> values) {
        this(id, values, Collections.emptyMap());
    }

    public StringColumn(StringColumnId id, List<String> values, Map<String, String> metaData) {
        super(id, values, metaData);
    }

    public static Builder builder(StringColumnId id) {
        return new Builder(id);
    }

    public static final class Builder extends GenericColumn.Builder<String, StringColumnId, StringColumn> {

        public Builder(StringColumnId id) {
            super(id);
        }

        @Override
        public StringColumn build() {
            return new StringColumn(id, valueBuilder.build(), metaDataBuilder.build());
        }

    }

}