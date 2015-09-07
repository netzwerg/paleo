package ch.fhnw.ima.paleo;

import org.junit.Test;

import java.util.Set;
import java.util.stream.DoubleStream;

import static ch.fhnw.ima.paleo.ColumnIds.*;

@SuppressWarnings("unused")
public class ReadmeTest {

    @Test
    public void demo() {

        // Type-safe column identifiers
        final StringColumnId NAME = stringCol("Name");
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
