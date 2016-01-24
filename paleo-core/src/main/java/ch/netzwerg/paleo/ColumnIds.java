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

public final class ColumnIds {

    private ColumnIds() {
        // enforce usage of static factory methods
    }

    public static IntColumnId intCol(String name) {
        return new IntColumnId(name);
    }

    public static DoubleColumnId doubleCol(String name) {
        return new DoubleColumnId(name);
    }

    public static BooleanColumnId booleanCol(String name) {
        return new BooleanColumnId(name);
    }

    public static StringColumnId stringCol(String name) {
        return new StringColumnId(name);
    }

    public static TimestampColumnId timestampCol(String name) {
        return new TimestampColumnId(name);
    }

    public static CategoryColumnId categoryCol(String name) {
        return new CategoryColumnId(name);
    }

    public static GenericColumnId genericCol(String name, ColumnType<?> type) {
        return new GenericColumnId(name, type);
    }

    public static final class IntColumnId extends GenericColumnId {
        private IntColumnId(String name) {
            super(name, ColumnType.INT);
        }
    }

    public static final class DoubleColumnId extends GenericColumnId {
        private DoubleColumnId(String name) {
            super(name, ColumnType.DOUBLE);
        }
    }

    public static final class BooleanColumnId extends GenericColumnId {
        private BooleanColumnId(String name) {
            super(name, ColumnType.BOOLEAN);
        }
    }

    public static final class StringColumnId extends GenericColumnId {
        private StringColumnId(String name) {
            super(name, ColumnType.STRING);
        }
    }

    public static final class TimestampColumnId extends GenericColumnId {
        private TimestampColumnId(String name) {
            super(name, ColumnType.TIMESTAMP);
        }
    }

    public static final class CategoryColumnId extends GenericColumnId {
        private CategoryColumnId(String name) {
            super(name, ColumnType.CATEGORY);
        }
    }

    public static class GenericColumnId implements ColumnId {

        private final String name;
        private final ColumnType<?> type;

        public GenericColumnId(String name, ColumnType<?> type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public ColumnType<?> getType() {
            return type;
        }

    }

}
