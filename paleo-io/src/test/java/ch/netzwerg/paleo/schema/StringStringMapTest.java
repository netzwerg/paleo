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

package ch.netzwerg.paleo.schema;

import javaslang.Tuple;
import javaslang.Tuple2;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("unchecked")
public class StringStringMapTest {

    private static final Tuple2<String, String> E1 = Tuple.of("bli", "v1");
    private static final Tuple2<String, String> E2 = Tuple.of("bla", "v2");

    @Test
    public void constructFromEmpty() {
        StringStringMap map = new StringStringMap();
        map.put(E1._1, E1._2);
        map.put(E2._1, E2._2);
        assertMapState(map);
    }

    @Test
    public void constructFromMap() {
        Map<String, String> entries = new java.util.LinkedHashMap<String, String>() {
            {
                put(E1._1, E1._2);
                put(E2._1, E2._2);
            }
        };
        StringStringMap map = new StringStringMap(entries);
        assertMapState(map);
    }

    private static void assertMapState(StringStringMap map) {
        assertEquals(2, map.size());
        assertEquals("v2", map.get("bla"));
        assertEquals("Must respect insertion order", "v1", map.values().iterator().next());
    }

}