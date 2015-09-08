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

package ch.netzwerg.paleo;

import com.google.common.collect.ImmutableList;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class DataGeneratorTest {

    private static final Random R = new Random();
    private static final int ROW_COUNT = 1000000;
    private static final String WORDS_TXT = "/words.txt";

    @Test @Ignore
    public void generate() throws IOException {
        List<String> words = loadWords();

        File tempFile = File.createTempFile("paleo-", ".txt");
        FileWriter writer = new FileWriter(tempFile);
        Instant instant = Instant.now();
        writer.write("Timestamp\tMonth\tName\tAge\tHeight\n");
        writer.write("Timestamp\tCategory\tString\tInt\tDouble\n");

        for (int rowIndex = 0; rowIndex < ROW_COUNT; rowIndex++) {
            instant = instant.plusMillis(100000);
            writer.append(instant.toString()).append("\t");
            writer.append(toMonth(instant).toString()).append("\t");
            writer.append(randomName(words)).append("\t");
            writer.append(String.valueOf(randomAge())).append("\t");
            writer.append(String.valueOf(randomHeight()));
            writer.write("\n");
        }

        writer.close();
        System.out.println("Data generated to " + tempFile.getCanonicalPath());
    }

    private static List<String> loadWords() {
        Scanner scanner = new Scanner(DataGeneratorTest.class.getResourceAsStream(WORDS_TXT));
        ImmutableList.Builder<String> listBuilder = ImmutableList.builder();
        while (scanner.hasNextLine()) {
            listBuilder.add(scanner.nextLine());
        }
        return listBuilder.build();
    }

    private static Month toMonth(Instant instant) {
        return instant.atZone(ZoneId.from(ZoneOffset.UTC)).getMonth();
    }

    private static String randomName(List<String> words) {
        return words.get(R.nextInt(words.size()));
    }

    private static int randomAge() {
        return R.nextInt(99);
    }

    private double randomHeight() {
        return 1 + R.nextDouble();
    }

}
