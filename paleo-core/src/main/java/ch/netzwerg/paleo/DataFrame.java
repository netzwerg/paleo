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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static ch.netzwerg.paleo.ColumnIds.*;
import static java.util.stream.Collectors.toList;

public final class DataFrame {

    private final Map<ColumnId, Column<?>> columns;
    private final int rowCount;

    public DataFrame(Column<?>... columns) {
        this(Arrays.asList(columns));
    }

    public DataFrame(List<Column<?>> columns) {
        this.rowCount = inferRowCount(columns);
        this.columns = createImmutableMap(columns);
    }

    private static int inferRowCount(List<Column<?>> columns) {
        if (columns.isEmpty()) {
            return 0;
        } else {
            Set<Integer> rowCounts = columns.stream().map(Column::getRowCount).distinct().collect(Collectors.<Integer>toSet());
            if (rowCounts.size() > 1) {
                throw new IllegalArgumentException("Differing number of rows (i.e. column sizes)");
            } else {
                return rowCounts.iterator().next();
            }
        }
    }

    private static Map<ColumnId, Column<?>> createImmutableMap(List<Column<?>> columns) {
        ImmutableMap.Builder<ColumnId, Column<?>> mapBuilder = ImmutableMap.builder();
        columns.forEach(c -> mapBuilder.put(c.getId(), c));
        return mapBuilder.build();
    }

    public int getRowCount() {
        return this.rowCount;
    }

    public int getColumnCount() {
        return this.columns.size();
    }

    public List<ColumnId> getColumnIds() {
        return ImmutableList.copyOf(this.columns.keySet());
    }

    public List<Column<?>> getColumns() {
        return ImmutableList.copyOf(this.columns.values());
    }

    public List<String> getColumnNames() {
        return this.columns.keySet().stream().map(ColumnId::getName).collect(toList());
    }

    public <C extends ColumnId> C getColumnId(int columnIndex, ColumnType<C> type) {
        Class<C> idType = type.getIdType();
        return idType.cast(ImmutableList.copyOf(this.columns.keySet()).get(columnIndex));
    }

    public IntColumn getColumn(IntColumnId columnId) {
        return getTypedColumn(columnId);
    }

    public DoubleColumn getColumn(DoubleColumnId columnId) {
        return getTypedColumn(columnId);
    }

    public BooleanColumn getColumn(BooleanColumnId columnId) {
        return getTypedColumn(columnId);
    }

    public StringColumn getColumn(StringColumnId columnId) {
        return getTypedColumn(columnId);
    }

    public TimestampColumn getColumn(TimestampColumnId columnId) {
        return getTypedColumn(columnId);
    }

    public CategoryColumn getColumn(CategoryColumnId columnId) {
        return getTypedColumn(columnId);
    }

    public <V, I extends GenericColumnId> GenericColumn<V, I> getColumn(I columnId) {
        return getTypedColumn(columnId);
    }

    public int getValueAt(int rowIndex, IntColumnId columnId) {
        IntColumn column = getTypedColumn(columnId);
        return column.getValueAt(rowIndex);
    }

    public double getValueAt(int rowIndex, DoubleColumnId columnId) {
        DoubleColumn column = getTypedColumn(columnId);
        return column.getValueAt(rowIndex);
    }

    public boolean getValueAt(int rowIndex, BooleanColumnId columnId) {
        BooleanColumn column = getTypedColumn(columnId);
        return column.getValueAt(rowIndex);
    }

    public String getValueAt(int rowIndex, StringColumnId columnId) {
        StringColumn column = getTypedColumn(columnId);
        return column.getValueAt(rowIndex);
    }

    public Instant getValueAt(int rowIndex, TimestampColumnId columnId) {
        TimestampColumn column = getTypedColumn(columnId);
        return column.getValueAt(rowIndex);
    }

    public String getValueAt(int rowIndex, CategoryColumnId columnId) {
        CategoryColumn column = getTypedColumn(columnId);
        return column.getValueAt(rowIndex);
    }

    public <V, I extends GenericColumnId> V getValueAt(int rowIndex, I columnId) {
        GenericColumn<V, I> column = getTypedColumn(columnId);
        return column.getValueAt(rowIndex);
    }

    @SuppressWarnings("unchecked")
    private <T extends Column<?>> T getTypedColumn(ColumnId columnId) {
        return (T) this.columns.get(columnId);
    }

}