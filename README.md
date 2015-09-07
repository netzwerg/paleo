Paleo
=====
Java 8 data frames with typed columns. The following column types are supported out-of-the-box:

* **Int**: Primitive `int` values
* **Double**: Primitive `double` values
* **Boolean**: Primitive `double` values
* **String**: `java.lang.String` values
* **Timestamp**: `java.time.Instant` values
* **Category**: Categorical `String` values (aka Factors)

All columns offer type-safe value access, use efficient data structures to store their values, and support a
builder API for convenient creation.

Hello Paleo
===========
The `paleo-core` module provides all classes to identify, create, and structure typed columns: 

```java
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
```

Parsing From Tab-Delimited Text
===============================
The `paleo-io` module parses data frames from tab-delimited text representations:

```java
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
```