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
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Scanner;

public interface Parser {

    // -- Tab Delimited

    static DataFrame parseTabDelimited(Reader in) {
        return ScalaParserImpl.parseViaReaderTabDelimited(in, Option.none());
    }

    static DataFrame parseTabDelimited(Reader in, String timestampPattern) {
        return ScalaParserImpl.parseViaReaderTabDelimited(in, Option.of(timestampPattern));
    }

    static DataFrame parseTabDelimited(Schema schema, File parentDir) {
        return ScalaParserImpl.parseViaSchemaTabDelimited(schema, parentDir);
    }

    static DataFrame parseTabDelimited(Schema schema) throws IOException {
        try (InputStream inputStream = Parser.class.getResourceAsStream(schema.getDataFileName());
             Scanner scanner = new Scanner(inputStream)) {

            scanner.useDelimiter(ScalaParserImpl.LineDelimiter());
            return ScalaParserImpl.parseViaFieldsTabDelimited(schema.getFields(), scanner, 0, schema.getMetaData());
        }
    }

    // -- Comma Separated

    static DataFrame parseCommaSeparated(Reader in) {
        return ScalaParserImpl.parseViaReaderCommaSeparated(in, Option.none());
    }

    static DataFrame parseCommaSeparated(Reader in, String timestampPattern) {
        return ScalaParserImpl.parseViaReaderCommaSeparated(in, Option.of(timestampPattern));
    }

    static DataFrame parseCommaSeparated(Schema schema, File parentDir) {
        return ScalaParserImpl.parseViaSchemaCommaSeparated(schema, parentDir);
    }

    static DataFrame parseCommaSeparated(Schema schema) throws IOException {
        try (InputStream inputStream = Parser.class.getResourceAsStream(schema.getDataFileName());
             Scanner scanner = new Scanner(inputStream)) {

            scanner.useDelimiter(ScalaParserImpl.LineDelimiter());
            return ScalaParserImpl.parseViaFieldsCommaSeparated(schema.getFields(), scanner, 0, schema.getMetaData());
        }
    }

}