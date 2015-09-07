package ch.fhnw.ima.paleo.csv;

import ch.fhnw.ima.paleo.*;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.stream.Stream;

import static ch.fhnw.ima.paleo.ColumnIds.*;

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
