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

import ch.netzwerg.paleo.*;
import ch.netzwerg.paleo.schema.Field;
import ch.netzwerg.paleo.schema.Schema;
import javaslang.Tuple2;
import javaslang.collection.IndexedSeq;
import javaslang.collection.Stream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;

public final class Parser {

    public static DataFrame parseTabDelimited(Reader in) throws IOException {
        return parseTabDelimited(in, Optional.empty());
    }

    public static DataFrame parseTabDelimited(Reader in, String timestampPattern) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timestampPattern);
        return parseTabDelimited(in, Optional.of(formatter));
    }

    public static DataFrame parseTabDelimited(Reader in, Optional<DateTimeFormatter> formatter) throws IOException {
        Iterator<CSVRecord> it = CSVFormat.TDF.parse(in).iterator();

        CSVRecord columnNames = it.next();
        CSVRecord columnTypes = it.next();

        Stream<? extends Acc<?, ?>> columnAccumulators = createColumnAccumulators(columnNames, columnTypes, formatter);

        return parseDataFrame(() -> it, columnAccumulators, 2);
    }

    public static DataFrame parseTabDelimited(Schema schema, File parentDir) throws IOException {
        try (FileReader fileReader = new FileReader(new File(parentDir, schema.getDataFileName()))) {
            return parseTabDelimited(schema, fileReader);
        }
    }

    public static DataFrame parseTabDelimited(Schema schema) throws IOException {
        try (InputStream inputStream = Parser.class.getResourceAsStream(schema.getDataFileName());
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
            return parseTabDelimited(schema, inputStreamReader);
        }
    }

    private static DataFrame parseTabDelimited(Schema schema, Reader reader) throws IOException {
        Iterable<CSVRecord> rowRecords = CSVFormat.TDF.parse(reader);
        Stream<? extends Acc<?, ?>> columnAccumulators = createColumnAccumulators(schema.getFields());
        return parseDataFrame(rowRecords, columnAccumulators, 0);
    }

    private static DataFrame parseDataFrame(Iterable<CSVRecord> rowRecords, Stream<? extends Acc<?, ?>> accumulators, int rowOffset) {

        Stream<? extends Acc<?, ?>> fullAccumulators = Stream.ofAll(rowRecords).zipWithIndex().foldLeft(accumulators, (Stream<? extends Acc<?, ?>> accus, Tuple2<CSVRecord, Integer> rowWithIndex) -> {

            CSVRecord row = rowWithIndex._1;
            Integer rowIndex = rowWithIndex._2;
            return appendRow(row, rowIndex, rowOffset, accus);

        });

        Iterable<Column<?>> columns = fullAccumulators.map(accumulator -> accumulator.getColumn());
        return DataFrame.ofAll(columns);
    }

    private static Stream<? extends Acc<?, ?>> appendRow(CSVRecord values, int rowIndex, int rowOffset, Stream<? extends Acc<?, ?>> accumulators) {
        if (values.size() != accumulators.length()) {
            String msgFormat = "Row '%s' contains '%s' values (but should match column count '%s')";
            String msg = String.format(msgFormat, rowIndex + rowOffset, values.size(), accumulators.length());
            throw new IllegalArgumentException(msg);
        }
        // TODO: Why is this substantially faster than zipping accumulators with values?
        Iterator<String> valueIt = values.iterator();
        return accumulators.map(accumulator -> accumulator.add(valueIt.next()));
    }

    private static Stream<? extends Acc<?, ?>> createColumnAccumulators(CSVRecord columnNames, CSVRecord columnTypes, Optional<DateTimeFormatter> formatter) {

        if (columnNames.size() != columnTypes.size()) {
            String msg = String.format(
                    "Number of column names (%s) must match number of column types (%s)",
                    columnNames.size(),
                    columnTypes.size());
            throw new IllegalArgumentException(msg);
        }

        Stream<String> nameStream = Stream.ofAll(columnNames);
        Stream<Tuple2<String, String>> nameWithTypeStream = nameStream.zip(columnTypes);

        return nameWithTypeStream.map(nameWithType -> {
            String name = nameWithType._1;
            ColumnType<?> type = ColumnType.getByDescriptionOrDefault(nameWithType._2, ColumnType.STRING);
            return createColumnAccumulator(name, type, formatter);
        });
    }

    private static Stream<? extends Acc<?, ?>> createColumnAccumulators(IndexedSeq<Field> fields) {
        return fields.map(field -> {
            ColumnType<?> type = field.getType();
            Optional<DateTimeFormatter> formatter = field.getFormat().map(DateTimeFormatter::ofPattern);
            return createColumnAccumulator(field.getName(), type, formatter);
        }).toStream();
    }

    private static Acc<?, ?> createColumnAccumulator(String name, ColumnType<?> type, Optional<DateTimeFormatter> formatter) {
        if (ColumnType.INT.equals(type)) {
            return intColumnAccumulator(name);
        } else if (ColumnType.DOUBLE.equals(type)) {
            return doubleColumnAccumulator(name);
        } else if (ColumnType.BOOLEAN.equals(type)) {
            return booleanColumnAccumulator(name);
        } else if (ColumnType.TIMESTAMP.equals(type)) {
            return timestampColumnAccumulator(name, formatter);
        } else if (ColumnType.CATEGORY.equals(type)) {
            return categoryColumnAccumulator(name);
        } else {
            return stringColumnAccumulator(name);
        }
    }

    private static final class Acc<V, C extends Column<?>> {

        private final Column.Builder<V, C> builder;
        private final Function<String, V> parseLogic;

        Acc(Column.Builder<V, C> builder, Function<String, V> parseLogic) {
            this.builder = builder;
            this.parseLogic = parseLogic;
        }

        public Acc<V, C> add(String stringValue) {
            V value = parseLogic.apply(stringValue);
            Column.Builder<V, C> newBuilder = builder.add(value);
            return new Acc<>(newBuilder, parseLogic);
        }

        public C getColumn() {
            return builder.build();
        }

    }

    private static Acc<Integer, IntColumn> intColumnAccumulator(String name) {
        IntColumn.Builder builder = IntColumn.builder(ColumnIds.intCol(name));
        Function<String, Integer> parseLogic = Integer::parseInt;
        return new Acc<>(builder, parseLogic);
    }

    private static Acc<Double, DoubleColumn> doubleColumnAccumulator(String name) {
        DoubleColumn.Builder builder = DoubleColumn.builder(ColumnIds.doubleCol(name));
        Function<String, Double> parseLogic = Double::parseDouble;
        return new Acc<>(builder, parseLogic);
    }

    private static Acc<Boolean, BooleanColumn> booleanColumnAccumulator(String name) {
        BooleanColumn.Builder builder = BooleanColumn.builder(ColumnIds.booleanCol(name));
        Function<String, Boolean> parseLogic = Boolean::parseBoolean;
        return new Acc<>(builder, parseLogic);
    }

    private static Acc<String, StringColumn> stringColumnAccumulator(String name) {
        StringColumn.Builder builder = StringColumn.builder(ColumnIds.stringCol(name));
        Function<String, String> parseLogic = s -> s;
        return new Acc<>(builder, parseLogic);
    }

    private static Acc<String, CategoryColumn> categoryColumnAccumulator(String name) {
        CategoryColumn.Builder builder = CategoryColumn.builder(ColumnIds.categoryCol(name));
        Function<String, String> parseLogic = s -> s;
        return new Acc<>(builder, parseLogic);
    }

    private static Acc<Instant, TimestampColumn> timestampColumnAccumulator(String name, Optional<DateTimeFormatter> formatter) {
        TimestampColumn.Builder builder = TimestampColumn.builder(ColumnIds.timestampCol(name));
        Function<String, Instant> parseLogic = stringValue -> {
            if (formatter.isPresent()) {
                LocalDateTime dateTime = LocalDateTime.from(formatter.get().parse(stringValue));
                return dateTime.atZone(ZoneId.systemDefault()).toInstant();
            } else {
                return Instant.parse(stringValue);
            }
        };
        return new Acc<>(builder, parseLogic);
    }

}