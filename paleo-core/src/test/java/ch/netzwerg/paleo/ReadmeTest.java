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

import javaslang.collection.Set;
import org.junit.Test;

import java.util.stream.DoubleStream;

import static ch.netzwerg.paleo.ColumnIds.*;

@SuppressWarnings("unused")
public class ReadmeTest {

    @Test
    public void demo() {

        // Type-safe column identifiers
        final StringColumnId NAME = StringColumnId.of("Name");
        final CategoryColumnId COLOR = CategoryColumnId.of("Color");
        final DoubleColumnId SERVING_SIZE = DoubleColumnId.of("Serving Size (g)");

        // Convenient column creation
        StringColumn nameColumn = StringColumn.ofAll(NAME, "Banana", "Blueberry", "Lemon", "Apple");
        CategoryColumn colorColumn = CategoryColumn.ofAll(COLOR, "Yellow", "Blue", "Yellow", "Green");
        DoubleColumn servingSizeColumn = DoubleColumn.ofAll(SERVING_SIZE, 118, 148, 83, 182);

        // Grouping columns into a data frame
        DataFrame dataFrame = DataFrame.ofAll(nameColumn, colorColumn, servingSizeColumn);

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
