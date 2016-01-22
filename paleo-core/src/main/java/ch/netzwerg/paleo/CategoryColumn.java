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

import ch.netzwerg.paleo.ColumnIds.CategoryColumnId;
import javaslang.collection.*;

import java.util.ArrayList;
import java.util.Objects;

public final class CategoryColumn implements Column<CategoryColumnId> {

    private final CategoryColumnId id;
    private final Array<String> categories;
    private final Array<Integer> categoryIndexPerRowIndex;
    private final Map<String, String> metaData;

    private CategoryColumn(CategoryColumnId id, Array<String> categories, Array<Integer> categoryIndexPerRowIndex, Map<String, String> metaData) {
        this.id = id;
        this.categories = categories;
        this.categoryIndexPerRowIndex = categoryIndexPerRowIndex;
        this.metaData = metaData;
    }

    public static CategoryColumn of(CategoryColumnId id, String value) {
        return builder(id).add(value).build();
    }

    public static CategoryColumn ofAll(CategoryColumnId id, String... values) {
        return builder(id).addAll(values).build();
    }

    public static CategoryColumn ofAll(CategoryColumnId id, Iterable<String> values) {
        return builder(id).addAll(values).build();
    }

    public static Builder builder(CategoryColumnId id) {
        return new Builder(id);
    }

    @Override
    public CategoryColumnId getId() {
        return id;
    }

    @Override
    public int getRowCount() {
        return categoryIndexPerRowIndex.length();
    }

    @Override
    public Map<String, String> getMetaData() {
        return metaData;
    }

    public String getValueAt(int rowIndex) {
        return categories.get(categoryIndexPerRowIndex.get(rowIndex));
    }

    public Set<String> getCategories() {
        return categories.toSet();
    }

    /**
     * Creates a stream of individual row values (i.e. "explodes" categories).
     */
    public Stream<String> createValues() {
        return Stream.range(0, getRowCount()).map(this::getValueAt);
    }

    public static final class Builder implements Column.Builder<String, CategoryColumn> {

        private final CategoryColumnId id;
        private final java.util.List<String> categories;
        private final java.util.List<Integer> categoryIndexPerRowIndex;
        private Map<String, String> metaData;

        private Builder(CategoryColumnId id) {
            this.id = id;
            this.categories = new ArrayList<>();
            this.categoryIndexPerRowIndex = new ArrayList<>();
            this.metaData = LinkedHashMap.empty();
        }

        @Override
        public Builder add(String value) {
            int categoryIndex = categories.indexOf(value);
            if (categoryIndex < 0) {
                categories.add(value);
                categoryIndex = categories.size() - 1;
            }
            categoryIndexPerRowIndex.add(categoryIndex);
            return this;
        }

        public Builder addAll(String... values) {
            return addAll(Stream.of(values));
        }

        public Builder addAll(Iterable<String> values) {
            return Stream.ofAll(values).foldLeft(this, Builder::add);
        }

        @Override
        public Builder withMetaData(Map<String, String> metaData) {
            this.metaData = Objects.requireNonNull(metaData, "metaData is null");
            return this;
        }

        public CategoryColumn build() {
            return new CategoryColumn(id, Array.ofAll(categories), Array.ofAll(categoryIndexPerRowIndex), metaData);
        }

    }

}