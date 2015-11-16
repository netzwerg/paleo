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

import javaslang.collection.Array;
import javaslang.collection.IndexedSeq;

import static ch.netzwerg.paleo.ColumnIds.GenericColumnId;

public class GenericColumn<V, I extends GenericColumnId> implements Column<I> {

    private final I id;
    private final IndexedSeq<V> values;

    protected GenericColumn(I id, IndexedSeq<V> values) {
        this.id = id;
        this.values = values;
    }

    public static <V, I extends GenericColumnId> GenericColumn<V, I> of(I id, V value) {
        return new GenericColumn<>(id, Array.of(value));
    }

    @SafeVarargs
    public static <V, I extends GenericColumnId> GenericColumn<V, I> ofAll(I id, V... values) {
        return new GenericColumn<>(id, Array.ofAll(values));
    }

    public static <V, I extends GenericColumnId> GenericColumn<V, I> ofAll(I id, Iterable<V> values) {
        return new GenericColumn<>(id, Array.ofAll(values));
    }

    public static <V, I extends GenericColumnId> GenericColumn<V, I> ofAll(I id, IndexedSeq<V> values) {
        return new GenericColumn<>(id, values);
    }

    @Override
    public I getId() {
        return id;
    }

    @Override
    public int getRowCount() {
        return values.length();
    }

    public V getValueAt(int index) {
        return values.get(index);
    }

    public IndexedSeq<V> getValues() {
        return values;
    }

}