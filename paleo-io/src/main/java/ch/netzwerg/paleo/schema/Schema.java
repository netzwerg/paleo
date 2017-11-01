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

package ch.netzwerg.paleo.schema;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.collection.IndexedSeq;
import io.vavr.collection.Map;
import io.vavr.collection.Vector;
import io.vavr.control.Option;
import io.vavr.jackson.datatype.VavrModule;

import java.io.IOException;
import java.io.Reader;

import static ch.netzwerg.paleo.schema.NullSafe.safeMap;
import static ch.netzwerg.paleo.schema.NullSafe.safeOption;
import static ch.netzwerg.paleo.schema.NullSafe.safeString;

public final class Schema {

    private final String title;
    private final String dataFileName;
    private final Vector<Field> fields;
    private final Map<String, String> metaData;
    private final Option<String> charsetName;

    @JsonCreator
    public Schema(@JsonProperty("title") String title, @JsonProperty("dataFileName") String dataFileName, @JsonProperty("fields") FieldList fields, @JsonProperty("metaData") StringStringMap metaData,  @JsonProperty("charsetName") Option<String> charsetName) {
        this.title = safeString(title);
        this.dataFileName = safeString(dataFileName);
        this.fields = safeFields(fields);
        this.metaData = safeMap(metaData);
        this.charsetName = safeOption(charsetName);
    }

    private static Vector<Field> safeFields(FieldList fields) {
        return fields == null ? Vector.empty() : Vector.ofAll(fields);
    }

    public String getTitle() {
        return title;
    }

    public String getDataFileName() {
        return dataFileName;
    }

    public IndexedSeq<Field> getFields() {
        return fields;
    }

    public Map<String, String> getMetaData() {
        return metaData;
    }

    public Option<String> getCharsetName(){
        return charsetName;
    }

    public static Schema parseJson(Reader in) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new VavrModule());
        return mapper.readValue(in, Schema.class);
    }

}
