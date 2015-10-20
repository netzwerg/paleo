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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.List;

public final class Schema {

    private static final String DEFAULT_STRING = "";

    private final String title;
    private final String dataFileName;
    private final List<Field> fields;

    @JsonCreator
    public Schema(@JsonProperty("title") String title, @JsonProperty("dataFileName") String dataFileName, @JsonProperty("fields") FieldList fields) {
        this.title = safeString(title);
        this.dataFileName = safeString(dataFileName);
        this.fields = safeFields(fields);
    }

    private static String safeString(String s) {
        return s == null ? DEFAULT_STRING : s;
    }

    private static List<Field> safeFields(FieldList fields) {
        return fields == null ? Collections.emptyList() : ImmutableList.copyOf(fields);
    }

    public String getTitle() {
        return this.title;
    }

    public String getDataFileName() {
        return this.dataFileName;
    }

    public List<Field> getFields() {
        return this.fields;
    }

    public static Schema parseJson(Reader in) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(in, Schema.class);
    }

}
