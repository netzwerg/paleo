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

import ch.netzwerg.paleo.ColumnIds;
import ch.netzwerg.paleo.ColumnType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public final class ColumnTypeDeserializer extends JsonDeserializer<ColumnType<?>> {

    public static final ColumnType<ColumnIds.StringColumnId> DEFAULT_TYPE = ColumnType.STRING;

    @Override
    public ColumnType<?> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String typeValue = parser.getCodec().readValue(parser, String.class);
        return ColumnType.getByDescriptionOrDefault(typeValue, DEFAULT_TYPE);
    }

}
