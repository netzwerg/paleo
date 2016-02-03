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

import ch.netzwerg.paleo.ColumnType;
import javaslang.collection.LinkedHashMap;
import javaslang.collection.Map;

public interface NullSafe {

    static String safeString(String s) {
        return s == null ? "" : s;
    }

    static ColumnType<?> safeType(ColumnType<?> type) {
        return type == null ? ColumnTypeDeserializer.DEFAULT_TYPE : type;
    }

    static Map<String, String> safeMap(StringStringMap javaMap) {
        if (javaMap == null) {
            return LinkedHashMap.empty();
        } else {
            return LinkedHashMap.ofAll(javaMap);
        }
    }

}
