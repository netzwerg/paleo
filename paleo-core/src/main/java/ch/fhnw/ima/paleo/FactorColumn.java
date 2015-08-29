package ch.fhnw.ima.paleo;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.*;
import java.util.stream.IntStream;

import static ch.fhnw.ima.paleo.ColumnIds.FactorColumnId;

public final class FactorColumn implements Column<FactorColumnId> {

    public interface Lookup {
        int getFactorIndex(int rowIndex);
    }

    private final FactorColumnId id;
    private final int rowCount;
    private final List<String> factors;
    private final Lookup lookup;

    public FactorColumn(FactorColumnId id, int rowCount, List<String> factors, Lookup lookup) {
        this.id = id;
        this.rowCount = rowCount;
        this.factors = ImmutableList.copyOf(factors);
        this.lookup = lookup;
    }

    public static Builder builder(FactorColumnId id) {
        return new Builder(id);
    }

    @Override
    public FactorColumnId getColumnId() {
        return this.id;
    }

    @Override
    public int getRowCount() {
        return this.rowCount;
    }

    public String getValueAt(int rowIndex) {
        int factorIndex = this.lookup.getFactorIndex(rowIndex);
        return factors.get(factorIndex);
    }

    public Set<String> getFactors() {
        return ImmutableSet.copyOf(this.factors);
    }

    public static final class Builder {

        private final FactorColumnId id;
        private final IntStream.Builder factorIndexByRowIndexBuilder;
        private final List<String> factors;
        private final HashMap<String, Integer> factorIndexByFactor;

        private Builder(FactorColumnId id) {
            this.id = id;
            this.factorIndexByRowIndexBuilder = IntStream.builder();
            this.factors = new ArrayList<>();
            this.factorIndexByFactor = new HashMap<>();
        }

        public Builder addAll(String... values) {
            Arrays.asList(values).forEach(this::add);
            return this;
        }

        public Builder add(String value) {
            Integer factorIndex = this.factorIndexByFactor.get(value);
            if (factorIndex == null) {
                factorIndex = this.factors.size();
                this.factorIndexByFactor.put(value, factorIndex);
                this.factors.add(value);
            }
            this.factorIndexByRowIndexBuilder.add(factorIndex);
            return this;
        }

        public FactorColumn build() {
            int[] lookupTable = this.factorIndexByRowIndexBuilder.build().toArray();
            FactorColumn.Lookup lookup = rowIndex -> lookupTable[rowIndex];
            return new FactorColumn(this.id, lookupTable.length, this.factors, lookup);
        }

    }

}