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

import ch.netzwerg.paleo.DataFrame;
import ch.netzwerg.paleo.schema.Schema;
import org.openjdk.jmh.annotations.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 1)
@Measurement(iterations = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@SuppressWarnings("unused")
public class ParserBenchmarks {

    // TODO: Generate data in a @Setup fixture (using logic in DataGeneratorTest)
    private static final String PARENT_DIR = "/Users/netzwerg/switchdrive/Projects/greenrad/Data/Simulated/1-mio/";
    private static final String SCHEMA = PARENT_DIR + "artificial.json";

    @Benchmark
    public void parseWithSchema() throws IOException {
        FileReader schema = new FileReader(SCHEMA);
        DataFrame dataFrame = Parser.parseTabDelimited(Schema.parseJson(schema), new File(PARENT_DIR));
        if (dataFrame.getRowCount() != 1_000_000) {
            throw new IllegalArgumentException("Parsing failed – expected 1 mio rows");
        }
    }

}
