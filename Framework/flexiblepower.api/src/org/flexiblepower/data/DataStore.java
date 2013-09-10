package org.flexiblepower.data;

import java.io.IOException;
import java.util.Set;

/**
 * The {@link DataStore} is used to persist data. This has a similar functionality as a Map, with a String as the key.
 * The implementation of the {@link DataStore} should always be thread-safe.
 * 
 * @param <T>
 *            The type of object that can be stored.
 */
public interface DataStore<T extends IdentifyableObject> {
    /**
     * @param id
     *            The identifier
     * @return <code>true</code> if an object with that identifier is stored.
     */
    boolean contains(String id);

    /**
     * @param id
     *            The identifier
     * @return An object stored with the given identifier, or a new (empty) object if it was not stored yet. It is
     *         guaranteed that the resulting object will return the given id by its {@link IdentifyableObject#getId()}.
     * @throws SecurityException
     *             When the {@link DataStorePermission} with a read action is missing.
     */
    T get(String id);

    /**
     * Stores the given object in the {@link DataStore}. All the registered listeners will be called with an
     * {@link DataChangeEvent} with type {@link DataChangeEvent.Type#CREATED} if no object with the same identifier
     * existed before, or otherwise with the type {@link DataChangeEvent.Type#CHANGED}.
     * 
     * @param object
     *            The object to be stored.
     * @throws IOException
     *             When anything went wrong during the save action.
     * @throws SecurityException
     *             When the {@link DataStorePermission} with a write action is missing.
     */
    void save(T object) throws IOException;

    /**
     * Removed the object represented by the given identifier. All the registered listeners will be called with an
     * {@link DataChangeEvent} with type {@link DataChangeEvent.Type#DELETED}.
     * 
     * @param id
     *            The identifier that is used to find the object.
     * @return The object that has been removed or null if there was no such object.
     * @throws IOException
     *             When anything went wrong during the remove action.
     * @throws SecurityException
     *             When the {@link DataStorePermission} with a write action is missing.
     */
    T remove(String id) throws IOException;

    /**
     * @return A {@link Set} of identifiers of all the currently available keys in the store.
     */
    Set<String> keySet();

    /**
     * Adds a {@link DataChangeListener} to this {@link DataStore}.
     * 
     * @param listener
     *            The listener that should be called when any part of the data changes.
     */
    void addDataServiceListener(DataChangeListener<T> listener);

    /**
     * Removes a {@link DataChangeListener} from this {@link DataStore}.
     * 
     * @param listener
     *            The listener that should no longer be called.
     */
    void removeDataServiceListener(DataChangeListener<T> listener);
}
