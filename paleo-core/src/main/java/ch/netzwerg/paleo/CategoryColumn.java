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
import com.google.common.collect.ImmutableSet;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class CategoryColumn implements Column<ColumnIds.CategoryColumnId> {

    public interface Lookup {
        int getCategoryIndex(int rowIndex);
    }

    private final ColumnIds.CategoryColumnId id;
    private final int rowCount;
    private final List<String> categories;
    private final Lookup lookup;
    private final ImmutableMap<String, String> metaData;

    public CategoryColumn(ColumnIds.CategoryColumnId id, int rowCount, List<String> categories, Lookup lookup) {
        this(id, rowCount, categories, lookup, Collections.emptyMap());
    }

    public CategoryColumn(ColumnIds.CategoryColumnId id, int rowCount, List<String> categories, Lookup lookup, Map<String, String> metaData) {
        this.id = id;
        this.rowCount = rowCount;
        this.categories = ImmutableList.copyOf(categories);
        this.lookup = lookup;
        this.metaData = ImmutableMap.copyOf(metaData);
    }

    public static Builder builder(ColumnIds.CategoryColumnId id) {
        return new Builder(id);
    }

    @Override
    public ColumnIds.CategoryColumnId getId() {
        return this.id;
    }

    @Override
    public int getRowCount() {
        return this.rowCount;
    }

    public String getValueAt(int rowIndex) {
        int categoryIndex = this.lookup.getCategoryIndex(rowIndex);
        return categories.get(categoryIndex);
    }

    public Set<String> getCategories() {
        return ImmutableSet.copyOf(this.categories);
    }

    /**
     * Creates a stream of individual row values (i.e. "explodes" categories).
     */
    public Stream<String> createValues() {
        return IntStream.range(0, this.rowCount).mapToObj(this::getValueAt);
    }

    @Override
    public Map<String, String> getMetaData() {
        return metaData;
    }

    public static final class Builder implements Column.Builder<CategoryColumn> {

        private final ColumnIds.CategoryColumnId id;
        private final IntStream.Builder categoryIndexByRowIndexBuilder;
        private final List<String> categories;
        private final Map<String, Integer> categoryIndexByCategory;
        private final ImmutableMap.Builder<String, String> metaDataBuilder;

        private Builder(ColumnIds.CategoryColumnId id) {
            this.id = id;
            this.categoryIndexByRowIndexBuilder = IntStream.builder();
            this.categories = new ArrayList<>();
            this.categoryIndexByCategory = new HashMap<>();
            this.metaDataBuilder = ImmutableMap.builder();
        }

        public Builder addAll(Iterable<String> values) {
            values.forEach(this::add);
            return this;
        }

        public Builder addAll(String... values) {
            return addAll(Arrays.asList(values));
        }

        public Builder add(String value) {
            Integer categoryIndex = this.categoryIndexByCategory.get(value);
            if (categoryIndex == null) {
                categoryIndex = this.categories.size();
                this.categoryIndexByCategory.put(value, categoryIndex);
                this.categories.add(value);
            }
            this.categoryIndexByRowIndexBuilder.add(categoryIndex);
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

        public CategoryColumn build() {
            int[] categoryIndexByRowIndex = categoryIndexByRowIndexBuilder.build().toArray();
            CategoryColumn.Lookup lookup = rowIndex -> categoryIndexByRowIndex[rowIndex];
            return new CategoryColumn(id, categoryIndexByRowIndex.length, categories, lookup, metaDataBuilder.build());
        }

    }

}