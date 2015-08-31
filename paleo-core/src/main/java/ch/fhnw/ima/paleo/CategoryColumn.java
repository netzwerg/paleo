package ch.fhnw.ima.paleo;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.*;
import java.util.stream.IntStream;

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
    public CategoryColumnId getColumnId() {
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

    public static final class Builder {

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

        public Builder addAll(String... values) {
            Arrays.asList(values).forEach(this::add);
            return this;
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