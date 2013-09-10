package org.flexiblepower.observation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Observation<T> {
    private final Date observedAt;
    private final T value;

    public Observation(Date observedAt, T value) {
        if (observedAt == null) {
            throw new IllegalArgumentException("observedAt == null");
        } else if (value == null) {
            throw new IllegalArgumentException("value == null");
        }

        this.observedAt = observedAt;
        this.value = value;
    }

    public Date getObservedAt() {
        return observedAt;
    }

    public T getValue() {
        return value;
    }

    public Object getValue(String name) {
        Method method = ObservationTranslationHelper.getGetterMethods(value.getClass()).get(name);
        if (method != null) {
            return executeMethod(method);
        }
        return null;
    }

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
        return 31 * observedAt.hashCode() + 97 * value.hashCode();
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
