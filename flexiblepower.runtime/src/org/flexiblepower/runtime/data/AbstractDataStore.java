package org.flexiblepower.runtime.data;

import java.io.IOException;
import java.security.AccessController;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.flexiblepower.data.DataChangeEvent;
import org.flexiblepower.data.DataChangeEvent.Type;
import org.flexiblepower.data.DataChangeListener;
import org.flexiblepower.data.DataStore;
import org.flexiblepower.data.DataStorePermission;
import org.flexiblepower.data.IdentifyableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDataStore<T extends IdentifyableObject> implements DataStore<T> {
    private final Logger logger;

    private final List<DataChangeListener<T>> listeners;
    private final Map<String, T> store;

    private final DataStorePermission readPermission;
    private final DataStorePermission writePermission;

    public AbstractDataStore() {
        logger = LoggerFactory.getLogger(getClass());

        listeners = new CopyOnWriteArrayList<DataChangeListener<T>>();
        store = new ConcurrentHashMap<String, T>();

        readPermission = new DataStorePermission(getClass().getName(), "read");
        writePermission = new DataStorePermission(getClass().getName(), "write");
    }

    protected abstract T newObject(String id);

    protected abstract T copyObject(T object);

    @Override
    public boolean contains(String id) {
        return store.containsKey(id);
    }

    @Override
    public T get(String id) {
        AccessController.checkPermission(readPermission);

        T object = store.get(id);
        return object == null ? newObject(id) : copyObject(object);
    }

    @Override
    public void save(T object) {
        AccessController.checkPermission(writePermission);

        T oldObject = store.put(object.getId(), copyObject(object));
        logger.debug("Saved object: " + object);

        Type type = oldObject == null ? Type.CREATED : Type.CHANGED;
        DataChangeEvent<T> evt = new DataChangeEvent<T>(object.getId(), this, type);
        for (DataChangeListener<T> l : listeners) {
            l.dataChanged(evt);
        }
    }

    @Override
    public T remove(String id) throws IOException {
        AccessController.checkPermission(writePermission);

        T object = store.remove(id);
        logger.debug("Removed object: " + object);

        DataChangeEvent<T> evt = new DataChangeEvent<T>(object.getId(), this, Type.DELETED);
        for (DataChangeListener<T> l : listeners) {
            l.dataChanged(evt);
        }

        return object;
    }

    @Override
    public Set<String> keySet() {
        return new HashSet<String>(store.keySet());
    }

    @Override
    public void addDataServiceListener(DataChangeListener<T> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeDataServiceListener(DataChangeListener<T> listener) {
        listeners.remove(listener);
    }
}
