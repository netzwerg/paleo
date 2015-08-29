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

    public static InstantColumnId instantCol(String name) {
        return new InstantColumnId(name);
    }

    public static FactorColumnId factorCol(String name) {
        return new FactorColumnId(name);
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

    public static final class InstantColumnId extends GenericColumnId {
        private InstantColumnId(String name) {
            super(name, ColumnType.INSTANT);
        }
    }

    public static final class FactorColumnId extends GenericColumnId {
        private FactorColumnId(String name) {
            super(name, ColumnType.FACTOR);
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
