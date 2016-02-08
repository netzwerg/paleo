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
package ch.netzwerg.paleo;

import javaslang.collection.List;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Warmup(iterations = 3)
@Measurement(iterations = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@SuppressWarnings("unused")
public class CategoryColumnBenchmarks {

    private static final int ROW_COUNT = 1_000_000;
    private static final List<String> UNIQUE_STRINGS = List.ofAll(() -> IntStream.range(0, ROW_COUNT).mapToObj(String::valueOf).iterator());

    @Benchmark
    public void buildCategoryColumn() {
        CategoryColumn.Builder builder = CategoryColumn.builder(ColumnIds.CategoryColumnId.of("test"));
        UNIQUE_STRINGS.forEach(builder::add);
        CategoryColumn column = builder.build();
        if (column.getRowCount() != ROW_COUNT) {
            throw new IllegalArgumentException("Building failed – expected 1 mio rows");
        }
    }

}
