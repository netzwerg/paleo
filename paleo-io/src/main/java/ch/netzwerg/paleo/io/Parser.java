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

package ch.netzwerg.paleo.io;

import ch.netzwerg.paleo.DataFrame;
import ch.netzwerg.paleo.io.impl.ScalaParserImpl;
import ch.netzwerg.paleo.schema.Schema;
import io.vavr.control.Option;

import java.io.File;
import java.io.Reader;

public interface Parser {

    // -- Tab Delimited Values

    static DataFrame tsv(Reader in) {
        return ScalaParserImpl.parseViaReaderTsv(in, Option.none());
    }

    static DataFrame tsv(Reader in, String timestampPattern) {
        return ScalaParserImpl.parseViaReaderTsv(in, Option.of(timestampPattern));
    }

    static DataFrame tsv(Schema schema, File parentDir) {
        return ScalaParserImpl.parseViaSchemaTsv(schema, parentDir);
    }

    static DataFrame tsv(Schema schema) {
        return ScalaParserImpl.parseViaSchemaTsv(schema);
    }

    // -- Comma Separated Values

    static DataFrame csv(Reader in) {
        return ScalaParserImpl.parseViaReaderCsv(in, Option.none());
    }

    static DataFrame csv(Reader in, String timestampPattern) {
        return ScalaParserImpl.parseViaReaderCsv(in, Option.of(timestampPattern));
    }

    static DataFrame csv(Schema schema, File parentDir) {
        return ScalaParserImpl.parseViaSchemaCsv(schema, parentDir);
    }

    static DataFrame csv(Schema schema) {
        return ScalaParserImpl.parseViaSchemaCsv(schema);
    }

}