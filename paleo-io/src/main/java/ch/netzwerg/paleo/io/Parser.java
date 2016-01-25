/*
 * Copyright 2016 Rahel Lüthy
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
import ch.netzwerg.paleo.io.impl.ScalaParserImpl;
import ch.netzwerg.paleo.schema.Field;
import ch.netzwerg.paleo.schema.Schema;
import javaslang.Tuple2;
import javaslang.collection.IndexedSeq;
import javaslang.collection.Map;
import javaslang.collection.Stream;
import javaslang.control.Option;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public final class Parser {

    public static DataFrame parseTabDelimited(Reader in) throws IOException {
        return parseTabDelimited(in, Option.none());
    }

    public static DataFrame parseTabDelimited(Reader in, String timestampPattern) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timestampPattern);
        return parseTabDelimited(in, Option.of(formatter));
    }

    public static DataFrame parseTabDelimited(Reader in, Option<DateTimeFormatter> formatter) throws IOException {
        java.util.Iterator<CSVRecord> it = CSVFormat.TDF.parse(in).iterator();

        CSVRecord columnNames = it.next();
        CSVRecord columnTypes = it.next();

        Stream<? extends Acc<?, ?>> columnAccumulators = createColumnAccumulators(columnNames, columnTypes, formatter);

        return parseDataFrame(() -> it, columnAccumulators, 2);
    }

    public static DataFrame parseTabDelimited(Schema schema, File parentDir) throws IOException {
        return ScalaParserImpl.parseTabDelimited(schema, parentDir);
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

        // Purely functional variants (zipWithIndex, foldLeft) were considerably slower – GC?

        AtomicInteger rowIndex = new AtomicInteger();
        rowRecords.forEach(values -> {

            if (values.size() != accumulators.length()) {
                String msgFormat = "Row '%s' contains '%s' values (but should match column count '%s')";
                int oneBasedRowIndex = rowIndex.get() + rowOffset + 1;
                String msg = String.format(msgFormat, oneBasedRowIndex, values.size(), accumulators.length());
                throw new IllegalArgumentException(msg);
            }

            accumulators.zip(values).forEach(t -> t._1.add(t._2));

            rowIndex.incrementAndGet();
        });

        Iterable<Column<?>> columns = accumulators.map(accumulator -> accumulator.getColumn());
        return DataFrame.ofAll(columns);
    }

    private static Stream<? extends Acc<?, ?>> createColumnAccumulators(CSVRecord columnNames, CSVRecord columnTypes, Option<DateTimeFormatter> formatter) {

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
            Option<DateTimeFormatter> formatter = field.getFormat().map(DateTimeFormatter::ofPattern);
            Acc<?, ?> accumulator = createColumnAccumulator(field.getName(), type, formatter);
            accumulator.putAllMetaData(field.getMetaData());
            return accumulator;
        }).toStream();
    }

    private static Acc<?, ?> createColumnAccumulator(String name, ColumnType<?> type, Option<DateTimeFormatter> formatter) {
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
            builder.add(value);
            return this;
        }

        public void putAllMetaData(Map<String, String> metaData) {
            builder.putAllMetaData(metaData);
        }

        public C getColumn() {
            return builder.build();
        }

    }

    private static Acc<Integer, IntColumn> intColumnAccumulator(String name) {
        IntColumn.Builder builder = IntColumn.builder(ColumnIds.IntColumnId.of(name));
        Function<String, Integer> parseLogic = Integer::parseInt;
        return new Acc<>(builder, parseLogic);
    }

    private static Acc<Double, DoubleColumn> doubleColumnAccumulator(String name) {
        DoubleColumn.Builder builder = DoubleColumn.builder(ColumnIds.DoubleColumnId.of(name));
        Function<String, Double> parseLogic = Double::parseDouble;
        return new Acc<>(builder, parseLogic);
    }

    private static Acc<Boolean, BooleanColumn> booleanColumnAccumulator(String name) {
        BooleanColumn.Builder builder = BooleanColumn.builder(ColumnIds.BooleanColumnId.of(name));
        Function<String, Boolean> parseLogic = Boolean::parseBoolean;
        return new Acc<>(builder, parseLogic);
    }

    private static Acc<String, StringColumn> stringColumnAccumulator(String name) {
        StringColumn.Builder builder = StringColumn.builder(ColumnIds.StringColumnId.of(name));
        Function<String, String> parseLogic = s -> s;
        return new Acc<>(builder, parseLogic);
    }

    private static Acc<String, CategoryColumn> categoryColumnAccumulator(String name) {
        CategoryColumn.Builder builder = CategoryColumn.builder(ColumnIds.CategoryColumnId.of(name));
        Function<String, String> parseLogic = s -> s;
        return new Acc<>(builder, parseLogic);
    }

    private static Acc<Instant, TimestampColumn> timestampColumnAccumulator(String name, Option<DateTimeFormatter> formatter) {
        TimestampColumn.Builder builder = TimestampColumn.builder(ColumnIds.TimestampColumnId.of(name));
        Function<String, Instant> parseLogic = stringValue -> {
            if (formatter.isDefined()) {
                LocalDateTime dateTime = LocalDateTime.from(formatter.get().parse(stringValue));
                return dateTime.atZone(ZoneId.systemDefault()).toInstant();
            } else {
                return Instant.parse(stringValue);
            }
        };
        return new Acc<>(builder, parseLogic);
    }

}