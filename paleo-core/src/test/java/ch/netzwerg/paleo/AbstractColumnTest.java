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

import javaslang.collection.Array;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public abstract class AbstractColumnTest<V, C extends AbstractColumn<V, ?>> extends AbstractBaseColumnTest<V, C> {

    abstract V generateValue();

    @Test
    public void values() {
        V v0 = generateValue();
        V v1 = generateValue();
        V v2 = generateValue();
        C column = builder().add(v0).add(v1).add(v2).build();

        Array<V> expected = Array.of(v0, v1, v2);

        assertEquals(expected, column.getValues());
        assertEquals(expected, column.valueStream().toArray());
    }

}