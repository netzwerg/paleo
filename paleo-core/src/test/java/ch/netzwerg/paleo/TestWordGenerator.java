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

package ch.netzwerg.paleo;

import java.util.Random;
import java.util.stream.Stream;

/**
 * Simple random word generator for tests (replaces the former chabis dependency).
 */
final class TestWordGenerator {

    private static final String[] SYLLABLES = {
            "ba", "be", "bi", "bo", "bu",
            "da", "de", "di", "do", "du",
            "fa", "fe", "fi", "fo", "fu",
            "ga", "ge", "gi", "go", "gu",
            "ha", "he", "hi", "ho", "hu",
            "ka", "ke", "ki", "ko", "ku",
            "la", "le", "li", "lo", "lu",
            "ma", "me", "mi", "mo", "mu",
            "na", "ne", "ni", "no", "nu",
            "pa", "pe", "pi", "po", "pu",
            "ra", "re", "ri", "ro", "ru",
            "sa", "se", "si", "so", "su",
            "ta", "te", "ti", "to", "tu"
    };

    private final Random random;

    TestWordGenerator(Random random) {
        this.random = random;
    }

    String randomWord() {
        int syllableCount = 2 + random.nextInt(3);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < syllableCount; i++) {
            sb.append(SYLLABLES[random.nextInt(SYLLABLES.length)]);
        }
        return sb.toString();
    }

    Stream<String> randomWords(int count) {
        return Stream.generate(this::randomWord).limit(count);
    }

}
