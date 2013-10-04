package org.flexiblepower.data;

public class DataChangeEvent<T extends IdentifyableObject> {
    public static enum Type {
        CREATED, CHANGED, DELETED
    }

    private final String id;
    private final DataStore<T> dataStore;
    private final Type type;

    public DataChangeEvent(String id, DataStore<T> datastore, Type type) {
        this.id = id;
        this.dataStore = datastore;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public DataStore<T> getDataStore() {
        return dataStore;
    }

    public Type getType() {
        return type;
    }
}
