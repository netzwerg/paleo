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

import org.junit.Test;

import java.util.stream.DoubleStream;

import static ch.netzwerg.paleo.ColumnIds.DoubleColumnId;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class DoubleColumnTest extends AbstractBaseColumnTest<Double, DoubleColumn> {

    private static final DoubleColumnId ID = DoubleColumnId.of("test");
    private static final double DELTA = 0.0001;

    @Override
    protected DoubleColumn.Builder builder() {
        return DoubleColumn.builder(ID);
    }

    @Test
    public void valueTypeSpecificBuilding() {
        DoubleColumn column = builder().add(1d).addAll(2, 9).add(0d).build();
        assertMultipleValues(column);
    }

    @Test
    public void of() {
        DoubleColumn column = DoubleColumn.of(ID, 42d);
        assertEquals(ID, column.getId());
        assertEquals(1, column.getRowCount());
        assertEquals(42d, column.getValueAt(0), DELTA);
        assertArrayEquals(new double[]{42}, column.valueStream().toArray(), DELTA);
    }

    @Test
    public void ofAllVarArgs() {
        DoubleColumn column = DoubleColumn.ofAll(ID, 1, 2, 9, 0);
        assertMultipleValues(column);
    }

    @Test
    public void ofAllStream() {
        DoubleStream stream = DoubleStream.of(1, 2, 9, 0);
        DoubleColumn column = DoubleColumn.ofAll(ID, stream);
        assertMultipleValues(column);
    }

    private static void assertMultipleValues(DoubleColumn column) {
        assertEquals(ID, column.getId());
        assertEquals(4, column.getRowCount());
        assertEquals(1, column.getValueAt(0), DELTA);
        assertEquals(0, column.getValueAt(column.getRowCount() - 1), DELTA);
        assertArrayEquals(new double[]{1, 2, 9, 0}, column.valueStream().toArray(), DELTA);
    }

}