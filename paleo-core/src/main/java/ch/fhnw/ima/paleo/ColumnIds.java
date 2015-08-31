package ch.fhnw.ima.paleo;

public final class ColumnIds {

    public static IntColumnId intCol(String name) {
        return new IntColumnId(name);
    }

    public static DoubleColumnId doubleCol(String name) {
        return new DoubleColumnId(name);
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

    public static final class IntColumnId extends GenericColumnId {
        private IntColumnId(String name) {
            super(name, ColumnType.PRIMITIVE_INT);
        }
    }

    public static final class DoubleColumnId extends GenericColumnId {
        private DoubleColumnId(String name) {
            super(name, ColumnType.PRIMITIVE_DOUBLE);
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
        private final ColumnType type;

        public GenericColumnId(String name, ColumnType type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public ColumnType getType() {
            return this.type;
        }

    }

}
