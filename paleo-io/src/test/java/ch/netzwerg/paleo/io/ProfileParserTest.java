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

package ch.netzwerg.paleo.io;

import ch.netzwerg.paleo.DataFrame;
import ch.netzwerg.paleo.schema.Schema;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("All")
@Ignore
public class ProfileParserTest {

    private static final String PARENT_DIR = "/Users/netzwerg/switchdrive/Projects/greenrad/Data/Simulated/1-mio/";
    private static final String SCHEMA = PARENT_DIR + "artificial.json";

    @Test
    public void run() throws IOException {
        FileReader schema = new FileReader(SCHEMA);
        long start = System.currentTimeMillis();
        DataFrame dataFrame = Parser.tsv(Schema.parseJson(schema), new File(PARENT_DIR));
        System.out.println("Done: " + (System.currentTimeMillis() - start));
        assertEquals(4, dataFrame.getColumnCount());
        assertEquals(1_000_000, dataFrame.getRowCount());
        while (true) {
            // keep profiler running
        }
    }

}
