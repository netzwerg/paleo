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
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import javaslang.collection.LinkedHashMap;
import javaslang.collection.Map;
import javaslang.control.Option;

import java.util.Objects;

import static ch.netzwerg.paleo.schema.NullSafe.safeMap;
import static ch.netzwerg.paleo.schema.NullSafe.safeString;
import static ch.netzwerg.paleo.schema.NullSafe.safeType;

public final class Field {

    private final String name;
    private final ColumnType<?> type;
    private final Option<String> format;
    private final Map<String, String> metaData;

    @JsonCreator
    public Field(@JsonProperty("name") String name, @JsonProperty("type") @JsonDeserialize(using = ColumnTypeDeserializer.class) ColumnType<?> type, @JsonProperty("format") String format, @JsonProperty("metaData") StringStringMap metaData) {
        this.name = safeString(name);
        this.type = safeType(type);
        this.format = Option.of(format);
        this.metaData = safeMap(metaData);
    }

    public Field(String name, ColumnType<?> type, Option<String> format) {
        this.name = safeString(name);
        this.type = safeType(type);
        this.format = Objects.requireNonNull(format, "format must not be null");
        this.metaData = LinkedHashMap.empty();
    }

    public String getName() {
        return name;
    }

    public ColumnType<?> getType() {
        return type;
    }

    public Option<String> getFormat() {
        return format;
    }

    public Map<String, String> getMetaData() {
        return metaData;
    }

}
