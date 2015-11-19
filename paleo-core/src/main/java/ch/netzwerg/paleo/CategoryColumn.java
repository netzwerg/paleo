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

import ch.netzwerg.paleo.ColumnIds.CategoryColumnId;
import javaslang.collection.Array;
import javaslang.collection.List;
import javaslang.collection.Set;
import javaslang.collection.Stream;

public final class CategoryColumn implements Column<CategoryColumnId> {

    private final CategoryColumnId id;
    private final Array<String> categories;
    private final Array<Integer> categoryIndexPerRowIndex;

    private CategoryColumn(CategoryColumnId id, Array<String> categories, Array<Integer> categoryIndexPerRowIndex) {
        this.id = id;
        this.categories = categories;
        this.categoryIndexPerRowIndex = categoryIndexPerRowIndex;
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
        return new Builder(id, Array.empty(), List.empty());
    }

    @Override
    public CategoryColumnId getId() {
        return id;
    }

    @Override
    public int getRowCount() {
        return categoryIndexPerRowIndex.length();
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
        private final Array<String> categories;
        private final List<Integer> categoryIndexPerRowIndex;

        private Builder(CategoryColumnId id, Array<String> categories, List<Integer> categoryIndexPerRowIndex) {
            this.id = id;
            this.categories = categories;
            this.categoryIndexPerRowIndex = categoryIndexPerRowIndex;
        }

        @Override
        public Builder add(String value) {
            int categoryIndex = categories.indexOf(value);
            Array<String> newCategories = categories;
            if (categoryIndex < 0) {
                newCategories = categories.append(value);
                categoryIndex = newCategories.length() - 1;
            }
            List<Integer> newRowIndicesPerCategory = categoryIndexPerRowIndex.prepend(categoryIndex);
            return new Builder(id, newCategories, newRowIndicesPerCategory);
        }

        public Builder addAll(String... values) {
            return addAll(Stream.ofAll(values));
        }

        public Builder addAll(Iterable<String> values) {
            return Stream.ofAll(values).foldLeft(this, Builder::add);
        }


        public CategoryColumn build() {
            return new CategoryColumn(id, categories, categoryIndexPerRowIndex.toStream().reverse().toArray());
        }

    }

}