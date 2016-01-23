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

import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.LinkedHashMap;
import javaslang.collection.Map;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public abstract class AbstractBaseColumnTest<V, C extends Column<?>> {

    protected abstract Column.Builder<V, C> builder();

    @SuppressWarnings("unchecked")
    @Test
    public void metaData() {

        Tuple2<String, String> entry0 = Tuple.of("k0", "v0");
        Tuple2<String, String> entry1 = Tuple.of("k1", "v1");

        // Simple inserts
        {
            Map<String, String> singleInserts = builder().putMetaData("k0", "v0").putMetaData("k1", "v1").build().getMetaData();
            Map<String, String> bulkInserts = builder().putAllMetaData(LinkedHashMap.ofAll(entry0, entry1)).build().getMetaData();
            assertEquals(singleInserts, bulkInserts);
            assertEquals("k0", singleInserts.toArray().get(0)._1);
        }

        // Insertion order
        {
            Map<String, String> metaData = LinkedHashMap.ofAll(entry1, entry0);
            Column<?> column = builder().putAllMetaData(metaData).build();
            assertEquals(metaData, column.getMetaData());
            assertEquals("k1", column.getMetaData().toArray().get(0)._1);
        }

        // Null-Safety
        try {
            builder().putAllMetaData(null).build();
            fail("null values should lead to NPE");
        } catch (NullPointerException npe) {
            assertEquals("metaData is null", npe.getMessage());
        }

    }

}
