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

import org.junit.Test;

import java.util.Set;
import java.util.stream.DoubleStream;

import static ch.netzwerg.paleo.ColumnIds.*;

@SuppressWarnings("unused")
public class ReadmeTest {

    @Test
    public void demo() {

        // Type-safe column identifiers
        final StringColumnId NAME = ColumnIds.stringCol("Name");
        final CategoryColumnId COLOR = categoryCol("Color");
        final DoubleColumnId SERVING_SIZE = doubleCol("Serving Size (g)");

        // Builder API for convenient creation
        StringColumn nameColumn = StringColumn.builder(NAME).addAll("Banana", "Blueberry", "Lemon", "Apple").build();
        CategoryColumn colorColumn = CategoryColumn.builder(COLOR).addAll("Yellow", "Blue", "Yellow", "Green").build();
        DoubleColumn servingSizeColumn = DoubleColumn.builder(SERVING_SIZE).addAll(118, 148, 83, 182).build();

        // Creating a data frame by enumerating its columns
        DataFrame dataFrame = new DataFrame(4, nameColumn, colorColumn, servingSizeColumn);

        // Typed random access to individual values (based on rowIndex / columnId)
        String lemon = dataFrame.getValueAt(2, NAME);
        double appleServingSize = dataFrame.getValueAt(3, SERVING_SIZE);

        // Typed stream-based access to all values
        DoubleStream servingSizes = servingSizeColumn.getValues();
        double maxServingSize = servingSizes.summaryStatistics().getMax();

        // Smart column implementations
        Set<String> colors = colorColumn.getCategories();

    }

}
