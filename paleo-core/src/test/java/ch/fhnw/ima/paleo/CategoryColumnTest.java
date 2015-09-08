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

import ch.netzwerg.chabis.WordGenerator;
import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Random;

import static ch.fhnw.ima.paleo.ColumnIds.CategoryColumnId;
import static ch.fhnw.ima.paleo.ColumnIds.categoryCol;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

public class CategoryColumnTest {

    private WordGenerator wordGenerator;

    @Before
    public void before() {
        this.wordGenerator = new WordGenerator(new Random(42));
    }

    @Test
    public void builder() {
        CategoryColumnId id = categoryCol("test" );
        CategoryColumn.Builder builder = CategoryColumn.builder(id);
        builder.add("foo" ).add("bar" ).addAll("foo", "baz", "bar" ).add("foo" );
        CategoryColumn column = builder.build();
        assertEquals(id, column.getId());
        assertEquals(6, column.getRowCount());
        assertEquals(ImmutableSet.of("foo", "bar", "baz" ), column.getCategories());
        assertEquals("foo", column.getValueAt(0));
        assertEquals("bar", column.getValueAt(1));
    }

    @Test
    public void createValues() {
        CategoryColumnId id = categoryCol("test" );
        CategoryColumn.Builder builder = CategoryColumn.builder(id);
        List<String> values = this.wordGenerator.randomWords(100);
        CategoryColumn column = builder.addAll(values).build();
        assertEquals(93, column.getCategories().size());
        assertEquals(values, column.createValues().collect(toList()));
    }

}