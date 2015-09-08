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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static ch.fhnw.ima.paleo.ColumnIds.CategoryColumnId;

public final class CategoryColumn implements Column<CategoryColumnId> {

    public interface Lookup {
        int getCategoryIndex(int rowIndex);
    }

    private final CategoryColumnId id;
    private final int rowCount;
    private final List<String> categories;
    private final Lookup lookup;

    public CategoryColumn(CategoryColumnId id, int rowCount, List<String> categories, Lookup lookup) {
        this.id = id;
        this.rowCount = rowCount;
        this.categories = ImmutableList.copyOf(categories);
        this.lookup = lookup;
    }

    public static Builder builder(CategoryColumnId id) {
        return new Builder(id);
    }

    @Override
    public CategoryColumnId getId() {
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

    public static final class Builder implements Column.Builder<CategoryColumn> {

        private final CategoryColumnId id;
        private final IntStream.Builder categoryIndexByRowIndexBuilder;
        private final List<String> categories;
        private final HashMap<String, Integer> categoryIndexByCategory;

        private Builder(CategoryColumnId id) {
            this.id = id;
            this.categoryIndexByRowIndexBuilder = IntStream.builder();
            this.categories = new ArrayList<>();
            this.categoryIndexByCategory = new HashMap<>();
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

        public CategoryColumn build() {
            int[] categoryIndexByRowIndex = this.categoryIndexByRowIndexBuilder.build().toArray();
            CategoryColumn.Lookup lookup = rowIndex -> categoryIndexByRowIndex[rowIndex];
            return new CategoryColumn(this.id, categoryIndexByRowIndex.length, this.categories, lookup);
        }

    }

}