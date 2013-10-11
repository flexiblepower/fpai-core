package org.flexiblepower.observation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * An {@link Observation} is a measurement that has been done at a certain time. It is a tuple with the observedAt date
 * and the value (of dynamic type). This class provides a couple of helpful methods, like getting the value as a
 * {@link Map} when you don't know the type (see {@link #getValueMap()}.
 * 
 * For the helper methods, it is assumed that the value is a Java bean with getters.
 * 
 * @author TNO
 * 
 * @param <T>
 *            The type of the value that is stored in the {@link Observation}.
 */
public class Observation<T> {
    private static final int HASH_CONSTANT = 31;

    /**
     * Creates a new {@link Observation} for the given tuple. This static helper method removes the need for specifying
     * T explicitly in java. This is exactly the same as calling
     * <code>new Observation&lt;T&gt;(observedAt, value)</code>.
     * 
     * @param observedAt
     *            The date at which the value has been observed.
     * @param value
     *            The value
     * @param <T>
     *            The type of the value that is stored in the {@link Observation}.
     * @return The newly created Observation object.
     */
    public static <T> Observation<T> create(Date observedAt, T value) {
        return new Observation<T>(observedAt, value);
    }

    private final Date observedAt;
    private final T value;

    /**
     * Creates a new {@link Observation} for the given tuple.
     * 
     * @param observedAt
     *            The date at which the value has been observed.
     * @param value
     *            The value
     * @throws NullPointerException
     *             When either the observedAt or the value is <code>null</code>.
     */
    public Observation(Date observedAt, T value) {
        if (observedAt == null || value == null) {
            throw new NullPointerException();
        }

        this.observedAt = observedAt;
        this.value = value;
    }

    /**
     * @return The date at which the value has been observed.
     */
    public Date getObservedAt() {
        return observedAt;
    }

    /**
     * @return The value
     */
    public T getValue() {
        return value;
    }

    /**
     * Tries to find a part of the value with the given name. It will assume a Java bean with getter methods. This is
     * equal to calling <code>getValueMap().get(name)</code>.
     * 
     * Note: this is heavily dependent on reflection and therefore won't work on embedded Java versions.
     * 
     * @param name
     *            The name that will be used to find the getter method.
     * @return The object when such a value has been found, <code>null</code> otherwise.
     */
    public Object getValue(String name) {
        Method method = ObservationTranslationHelper.getGetterMethods(value.getClass()).get(name);
        if (method != null) {
            return executeMethod(method);
        }
        return null;
    }

    /**
     * Detects all the parts of the value, assuming a Java bean. This will detect all getter methods and return the
     * corresponding sub-values.
     * 
     * @return A {@link Map} with all the sub-values.
     */
    public Map<String, Object> getValueMap() {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Method> methods = ObservationTranslationHelper.getGetterMethods(value.getClass());
        for (Entry<String, Method> entry : methods.entrySet()) {
            Method method = entry.getValue();
            Object object = executeMethod(method);
            if (object != null) {
                if (object.getClass().isEnum()) {
                    result.put(entry.getKey(), object.toString());
                } else {
                    result.put(entry.getKey(), object);
                }
            }
        }
        return result;
    }

    private Object executeMethod(Method method) {
        try {
            method.setAccessible(true);
            return method.invoke(value);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    @Override
    public int hashCode() {
        return HASH_CONSTANT * (observedAt.hashCode() + HASH_CONSTANT * value.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        } else {
            Observation<?> other = (Observation<?>) obj;
            return observedAt.equals(other.observedAt) && value.equals(other.value);
        }
    }

    @Override
    public String toString() {
        return "Observation [observedAt=" + observedAt + ", value=" + value + "]";
    }
}
