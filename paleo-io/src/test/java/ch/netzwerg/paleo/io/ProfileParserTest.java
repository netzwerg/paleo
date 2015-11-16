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

package ch.netzwerg.paleo.io;

import ch.netzwerg.paleo.DataFrame;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("All")
@Ignore
public class ProfileParserTest {

    public static final String _100K = "/private/var/folders/sp/8sn5_q1j6k156y5tgv9kgzy00000gn/T/paleo-3757739999792798088.txt";
    public static final String _1M = "/private/var/folders/sp/8sn5_q1j6k156y5tgv9kgzy00000gn/T/paleo-4599627887018509102.txt";

    @Test
    public void run() {
        File f = new File(_1M);
        try (Reader r = new FileReader(f)) {
            long start = System.currentTimeMillis();
            DataFrame dataFrame = Parser.parseTabDelimited(r);
            System.out.println("Done: " + (System.currentTimeMillis() - start));
            while (true) {
                // keep profiler running
            }
        } catch (IOException ex) {
            Logger.getLogger(ProfileParserTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
