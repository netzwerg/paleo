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

package ch.netzwerg.paleo;

import javaslang.collection.Stream;
import javaslang.collection.Vector;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Investigating memory usage. Observing the following deep sizes:
 * - 207 MB (javaslang Vector)
 * - 20 MB  (jdk ArrayList)
 */
@SuppressWarnings("All")
@Ignore
public class ProfilingTest {

    private static final int COUNT = 1_000_000;

    @Test
    public void immutable() {
        Vector<Integer> vector = Stream.range(0, COUNT).foldLeft(Vector.empty(), Vector::append);
        assertEquals(COUNT, vector.length());
        ResultWrapper<Vector<Integer>> resultWrapper = new ResultWrapper<>(vector);
        keepProfilerAlive(resultWrapper);
    }

    @Test
    public void mutable() {
        java.util.List<Integer> list = new ArrayList<>();
        IntStream.range(0, COUNT).forEach(list::add);
        assertEquals(COUNT, list.size());
        ResultWrapper<java.util.List<Integer>> resultWrapper = new ResultWrapper<>(list);
        keepProfilerAlive(resultWrapper);
    }

    private static void keepProfilerAlive(ResultWrapper<?> resultWrapper) {
        System.out.println("Done");
        while (true) {
            assertNotNull(resultWrapper);
        }
    }

    /**
     * A hacky construct to identify our result of interest in a profiler.
     */
    private static final class ResultWrapper<T> {

        private final T result;

        private ResultWrapper(T result) {
            this.result = result;
        }

    }

}