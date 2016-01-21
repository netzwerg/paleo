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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static ch.netzwerg.paleo.ColumnIds.GenericColumnId;

public class GenericColumn<V, I extends GenericColumnId> implements Column<I> {

    private final I id;
    private final ImmutableList<V> values;
    private final ImmutableMap<String, String> metaData;

    protected GenericColumn(I id, List<V> values) {
        this(id, values, Collections.emptyMap());
    }

    protected GenericColumn(I id, List<V> values, Map<String, String> metaData) {
        this.id = id;
        this.values = ImmutableList.copyOf(values);
        this.metaData = ImmutableMap.copyOf(metaData);
    }

    @Override
    public I getId() {
        return this.id;
    }

    @Override
    public int getRowCount() {
        return this.values.size();
    }

    public V getValueAt(int index) {
        return this.values.get(index);
    }

    public List<V> getValues() {
        return this.values;
    }

    @Override
    public Map<String, String> getMetaData() {
        return metaData;
    }

    public static abstract class Builder<V, I extends GenericColumnId, C extends GenericColumn<V, I>> implements Column.Builder<C> {

        protected final I id;
        protected final ImmutableList.Builder<V> valueBuilder;
        protected final ImmutableMap.Builder<String, String> metaDataBuilder;

        public Builder(I id) {
            this.id = id;
            this.valueBuilder = ImmutableList.builder();
            this.metaDataBuilder = ImmutableMap.builder();
        }

        public final Builder<V, I, C> addAll(Iterable<V> values) {
            values.forEach(this::add);
            return this;
        }

        @SafeVarargs
        public final Builder<V, I, C> addAll(V... values) {
            return addAll(Arrays.asList(values));
        }

        public final Builder<V, I, C> add(V value) {
            this.valueBuilder.add(value);
            return this;
        }

        @Override
        public final Builder<V, I, C> putMetaData(String key, String value) {
            metaDataBuilder.put(key, value);
            return this;
        }

        public final Builder<V, I, C> putAllMetaData(Map<String, String> metaData) {
            metaDataBuilder.putAll(metaData);
            return this;
        }

    }

}