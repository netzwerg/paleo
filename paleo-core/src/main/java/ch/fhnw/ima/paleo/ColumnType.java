package ch.fhnw.ima.paleo;

public final class ColumnType {

    public static final ColumnType PRIMITIVE_INT = new ColumnType("Int");
    public static final ColumnType PRIMITIVE_DOUBLE = new ColumnType("Double");
    public static final ColumnType STRING = new ColumnType("String");
    public static final ColumnType TIMESTAMP = new ColumnType("Timestamp");
    public static final ColumnType CATEGORY = new ColumnType("Category");

    private final String description;

    public ColumnType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

}