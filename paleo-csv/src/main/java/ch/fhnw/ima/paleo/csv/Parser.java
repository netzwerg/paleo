package ch.fhnw.ima.paleo.csv;

import ch.fhnw.ima.paleo.*;
import com.google.common.collect.ImmutableList;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.DoubleStream;

import static ch.fhnw.ima.paleo.ColumnIds.*;

public final class Parser {

    public static DataFrame parseTabDelimited(Reader in) throws IOException {
        return parseTabDelimited(in, Optional.empty(), Collections.emptyMap());
    }

    public static DataFrame parseTabDelimited(Reader in, String timestampPattern) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timestampPattern);
        return parseTabDelimited(in, Optional.of(formatter), Collections.emptyMap());
    }

    public static DataFrame parseTabDelimited(Reader in, Optional<DateTimeFormatter> formatter, Map<String, ColumnBuilderFactory> columnBuilderFactories) throws IOException {
        Iterator<CSVRecord> it = CSVFormat.TDF.parse(in).iterator();

        CSVRecord columnNames = it.next();
        CSVRecord columnTypes = it.next();

        List<ColumnBuilder> columnBuilders = createColumnBuilders(columnNames, columnTypes, formatter, columnBuilderFactories);

        int rowCount = 0;
        while (it.hasNext()) {
            rowCount++;
            CSVRecord row = it.next();

            Iterator<String> valueIt = row.iterator();
            Iterator<ColumnBuilder> columnBuildersIt = columnBuilders.iterator();
            while (valueIt.hasNext()) {
                columnBuildersIt.next().add(valueIt.next());
            }
        }

        ImmutableList.Builder<Column<?>> columns = ImmutableList.builder();
        columnBuilders.forEach(columnBuilder -> columns.add(columnBuilder.build()));
        return new DataFrame(rowCount, columns.build());
    }

    private static List<ColumnBuilder> createColumnBuilders(CSVRecord columnNames, CSVRecord columnTypes, Optional<DateTimeFormatter> formatter, Map<String, ColumnBuilderFactory> columnBuilderFactories) {

        if (columnNames.size() != columnTypes.size()) {
            String msg = String.format(
                    "Number of column names (%s) must match number of column types (%s)",
                    columnNames.size(),
                    columnTypes.size());
            throw new IllegalArgumentException(msg);
        }

        ImmutableList.Builder<ColumnBuilder> resultBuilder = ImmutableList.builder();

        Iterator<String> nameIt = columnNames.iterator();
        Iterator<String> typeIt = columnTypes.iterator();
        while (nameIt.hasNext()) {
            String name = nameIt.next();
            String type = typeIt.next();

            resultBuilder.add(createColumnBuilder(name, type, formatter, columnBuilderFactories));
        }
        return resultBuilder.build();
    }

    private static ColumnBuilder<?> createColumnBuilder(String name, String type, Optional<DateTimeFormatter> formatter, Map<String, ColumnBuilderFactory> columnBuilderFactories) {
        if (columnBuilderFactories.containsKey(type)) {
            return columnBuilderFactories.get(type).create(name, type);
        } else if (ColumnType.PRIMITIVE_INT.getDescription().equalsIgnoreCase(type)) {
            return new IntColumnBuilder(name);
        } else if (ColumnType.PRIMITIVE_DOUBLE.getDescription().equalsIgnoreCase(type)) {
            return new DoubleColumnBuilder(name);
        } else if (ColumnType.TIMESTAMP.getDescription().equalsIgnoreCase(type)) {
            return new TimestampColumnBuilder(name, formatter);
        } else if (ColumnType.CATEGORY.getDescription().equalsIgnoreCase(type)) {
            return new CategoryColumnBuilder(name);
        } else {
            return new StringColumnBuilder(name);
        }
    }

    public interface ColumnBuilder<C extends Column<?>> {
        ColumnBuilder<C> add(String stringValue);

        C build();
    }

    public interface ColumnBuilderFactory {
        ColumnBuilder<?> create(String name, String typeDescription);
    }

    private static final class IntColumnBuilder implements ColumnBuilder<IntColumn> {

        private final IntColumn.Builder delegate;

        private IntColumnBuilder(String name) {
            this.delegate = IntColumn.builder(ColumnIds.intCol(name));
        }

        @Override
        public IntColumnBuilder add(String stringValue) {
            this.delegate.add(Integer.valueOf(stringValue));
            return this;
        }

        @Override
        public IntColumn build() {
            return this.delegate.build();
        }

    }

    private static final class DoubleColumnBuilder implements ColumnBuilder<DoubleColumn> {

        private final DoubleColumnId id;
        private final DoubleStream.Builder valueStreamBuilder;

        private DoubleColumnBuilder(String name) {
            this.id = ColumnIds.doubleCol(name);
            this.valueStreamBuilder = DoubleStream.builder();
        }

        @Override
        public DoubleColumnBuilder add(String stringValue) {
            this.valueStreamBuilder.add(Double.valueOf(stringValue));
            return this;
        }

        @Override
        public DoubleColumn build() {
            return new DoubleColumn(this.id, valueStreamBuilder.build());
        }

    }

    private static final class StringColumnBuilder implements ColumnBuilder<StringColumn> {

        private final StringColumnId id;
        private final ImmutableList.Builder<String> valueListBuilder;

        private StringColumnBuilder(String name) {
            this.id = ColumnIds.stringCol(name);
            this.valueListBuilder = ImmutableList.builder();
        }

        @Override
        public StringColumnBuilder add(String stringValue) {
            this.valueListBuilder.add(stringValue);
            return this;
        }

        @Override
        public StringColumn build() {
            return new StringColumn(this.id, this.valueListBuilder.build());
        }
    }

    private static final class TimestampColumnBuilder implements ColumnBuilder<TimestampColumn> {

        private final Optional<DateTimeFormatter> formatter;
        private final GenericColumnId id;
        private final ImmutableList.Builder<Instant> valueListBuilder;

        private TimestampColumnBuilder(String name, Optional<DateTimeFormatter> formatter) {
            this.id = ColumnIds.timestampCol(name);
            this.formatter = formatter;
            this.valueListBuilder = ImmutableList.builder();
        }

        @Override
        public TimestampColumnBuilder add(String stringValue) {
            Instant instant;
            if (this.formatter.isPresent()) {
                LocalDateTime dateTime = LocalDateTime.from(this.formatter.get().parse(stringValue));
                instant = dateTime.atZone(ZoneId.systemDefault()).toInstant();
            } else {
                instant = Instant.parse(stringValue);
            }
            this.valueListBuilder.add(instant);
            return this;
        }

        @Override
        public TimestampColumn build() {
            return new TimestampColumn(this.id, this.valueListBuilder.build());
        }

    }

    private static final class CategoryColumnBuilder implements ColumnBuilder<CategoryColumn> {

        private final CategoryColumn.Builder delegate;

        private CategoryColumnBuilder(String name) {
            this.delegate = CategoryColumn.builder(ColumnIds.categoryCol(name));
        }

        @Override
        public CategoryColumnBuilder add(String stringValue) {
            this.delegate.add(stringValue);
            return this;
        }

        @Override
        public CategoryColumn build() {
            return this.delegate.build();
        }

    }

}