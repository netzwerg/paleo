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

package ch.netzwerg.paleo.schema;

import ch.netzwerg.paleo.ColumnIds;
import ch.netzwerg.paleo.ColumnType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Optional;

public final class Field {

    public static final String DEFAULT_NAME = "";
    public static final ColumnType<ColumnIds.StringColumnId> DEFAULT_TYPE = ColumnTypeDeserializer.DEFAULT_TYPE;

    private final String name;
    private final ColumnType<?> type;
    private final Optional<String> format;

    @JsonCreator
    public Field(@JsonProperty("name") String name, @JsonProperty("type") @JsonDeserialize(using = ColumnTypeDeserializer.class) ColumnType<?> type, @JsonProperty("format") String format) {
        this.name = safeName(name);
        this.type = safeType(type);
        this.format = Optional.ofNullable(format);
    }

    private static String safeName(String name) {
        return name == null ? DEFAULT_NAME : name;
    }

    private static ColumnType<?> safeType(ColumnType<?> type) {
        return type == null ? DEFAULT_TYPE : type;
    }

    public String getName() {
        return name;
    }

    public ColumnType<?> getType() {
        return type;
    }

    public Optional<String> getFormat() {
        return format;
    }

}
