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

package ch.netzwerg.paleo.io;

import ch.netzwerg.paleo.ColumnType;
import ch.netzwerg.paleo.DataFrame;
import ch.netzwerg.paleo.StringColumn;
import ch.netzwerg.paleo.io.Parser;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.stream.Stream;

import static ch.netzwerg.paleo.ColumnIds.*;

@SuppressWarnings("unused")
public class ReadmeTest {

    @Test
    public void demo() throws IOException {

        final String EXAMPLE =
                "Name\tColor\tServing Size (g)\n" +
                        "String\tCategory\tDouble\n" +
                        "Banana\tYellow\t118\n" +
                        "Blueberry\tBlue\t148\n" +
                        "Lemon\tYellow\t83\n" +
                        "Apple\tGreen\t182";


        DataFrame dataFrame = Parser.parseTabDelimited(new StringReader(EXAMPLE));

        // Lookup typed identifiers by column index
        final StringColumnId NAME = dataFrame.getColumnId(0, ColumnType.STRING);
        final CategoryColumnId COLOR = dataFrame.getColumnId(1, ColumnType.CATEGORY);
        final DoubleColumnId SERVING_SIZE = dataFrame.getColumnId(2, ColumnType.DOUBLE);

        // Use identifier to access columns & values
        StringColumn nameColumn = dataFrame.getColumn(NAME);
        Stream<String> nameValues = nameColumn.getValues();

        // ... or access individual values via row index / column id
        String yellow = dataFrame.getValueAt(2, COLOR);

    }

}
