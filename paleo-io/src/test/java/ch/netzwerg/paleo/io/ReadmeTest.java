/*
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
import ch.netzwerg.paleo.schema.Schema;
import javaslang.collection.IndexedSeq;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

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
        IndexedSeq<String> nameValues = nameColumn.getValues();

        // ... or access individual values via row index / column id
        String yellow = dataFrame.getValueAt(2, COLOR);

    }

    @Test
    public void demoViaSchema() throws IOException {
        final String EXAMPLE_SCHEMA = "{\n" +
                "  \"title\": \"Example Schema\",\n" +
                "  \"dataFileName\": \"example-data.txt\",\n" +
                "  \"fields\": [\n" +
                "    {\n" +
                "      \"name\": \"Name\",\n" +
                "      \"type\": \"String\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"Color\",\n" +
                "      \"type\": \"Category\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"Serving Size\",\n" +
                "      \"type\": \"Double\",\n" +
                "      \"metaData\": {\"unit\": \"g\"}\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"Exemplary Date\",\n" +
                "      \"type\": \"Timestamp\",\n" +
                "      \"format\": \"yyyyMMddHHmmss\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        // some trickery to find physical location of resources folder...
        File baseDir = new File(ParserTest.class.getResource("/example-data.txt").getPath()).getParentFile();

        Schema schema = Schema.parseJson(new StringReader(EXAMPLE_SCHEMA));
        DataFrame dataFrame = Parser.parseTabDelimited(schema, baseDir);
    }

}
