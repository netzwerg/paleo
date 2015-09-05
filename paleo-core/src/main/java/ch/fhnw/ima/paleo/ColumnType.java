package ch.fhnw.ima.paleo;

import static ch.fhnw.ima.paleo.ColumnIds.*;

public final class ColumnType<T extends ColumnId> {

    public static final ColumnType<IntColumnId> INT = new ColumnType<>("Int", IntColumnId.class);
    public static final ColumnType<DoubleColumnId> DOUBLE = new ColumnType<>("Double", DoubleColumnId.class);
    public static final ColumnType<BooleanColumnId> BOOLEAN = new ColumnType<>("Boolean", BooleanColumnId.class);
    public static final ColumnType<StringColumnId> STRING = new ColumnType<>("String", StringColumnId.class);
    public static final ColumnType<TimestampColumnId> TIMESTAMP = new ColumnType<>("Timestamp", TimestampColumnId.class);
    public static final ColumnType<CategoryColumnId> CATEGORY = new ColumnType<>("Category", CategoryColumnId.class);

    private final String description;
    private final Class<T> idType;

    public ColumnType(String description, Class<T> idType) {
        this.description = description;
        this.idType = idType;
    }

    public String getDescription() {
        return this.description;
    }

    public Class<T> getIdType() {
        return idType;
    }

}