package org.flexiblepower.observation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * This utility class has a 2 methods that are used by the {@link Observation#getValueMap()} and
 * {@link Observation#getValue(String)} methods. It finds the all the getX() methods of a class using the
 * {@link #getGetterMethods(Class)} function and it can test if it is a valid java bean (see {@link #isJavaBean(Class)}.
 */
public final class ObservationTranslationHelper {
    private static WeakHashMap<Class<?>, Map<String, Method>> cache = new WeakHashMap<Class<?>, Map<String, Method>>();

    private ObservationTranslationHelper() {
    }

    private static boolean isGetter(Method method) {
        String name = method.getName();
        if (method.getParameterTypes().length == 0 && method.getReturnType() != Void.TYPE) {
            if (name.startsWith("get") || name.startsWith("is")) {
                return !name.equals("getClass");
            }
        }
        return false;
    }

    private static Map<String, Method> createGetterMethods(Class<?> clazz) {
        Map<String, Method> result = new HashMap<String, Method>();
        for (Method method : clazz.getMethods()) {
            if (isGetter(method)) {
                String name = null;
                if (name == null || name.isEmpty()) {
                    name = method.getName();
                    name = name.startsWith("get") ? name.substring("get".length()) : name.substring("is".length());
                    name = name.replaceAll("([A-Z])", "_$1").toLowerCase();
                    if (name.charAt(0) == '_') {
                        name = name.substring(1);
                    }
                }

                result.put(name, method);
            }
        }
        return result;
    }

    /**
     * @param clazz
     *            The class from which it will detect all the getter methods.
     * @return A map with the names of the members as key and the {@link Method} as value.
     */
    public static Map<String, Method> getGetterMethods(Class<?> clazz) {
        if (isPrimite(clazz)) {
            return Collections.emptyMap();
        }

        Map<String, Method> result = cache.get(clazz);
        if (result == null) {
            result = createGetterMethods(clazz);
            cache.put(clazz, result);
        }
        return result;
    }

    /**
     * @param type
     *            The class that will be checked if it is a primitive class
     * @return true when the class is either a java primitive (e.g. int, long, double, etc.), an array, an enumeration
     *         or a class from the java library.
     */
    private static boolean isPrimite(Class<?> type) {
        return type.isPrimitive() || type.isArray() || type.isEnum() || type.getPackage().getName().startsWith("java.");
    }

    /**
     * @param type
     *            The class that will be checked if it is a java bean
     * @return true when the given type is a java bean that contains at least 1 getter method and that is not a
     *         primitive (see {@link #isPrimite(Class)}.
     */
    public static boolean isJavaBean(Class<?> type) {
        return !isPrimite(type) && !getGetterMethods(type).isEmpty();
    }

    /**
     * Gets the member of the given object. This supports deep references by using the '.' in between the names (e.g.
     * looking for the member "location.geo.latitude" will effectively call
     * object.getLocation().getGeo().getLatitude()).
     * 
     * @param object
     *            The object on which to call the getter methods.
     * @param name
     *            The name of the member that is needed. For deep referencing use the dot as a seperator.
     * @return The member or null if it did not exist.
     */
    public static Object getMember(Object object, String name) {
        int ix = name.indexOf('.');
        if (ix >= 0) {
            String key = name.substring(0, ix);
            String rest = name.substring(ix + 1);

            Method method = getGetterMethods(object.getClass()).get(key);
            return method != null ? getMember(executeMethod(object, method), rest) : null;
        } else {
            Method method = getGetterMethods(object.getClass()).get(name);
            return method != null ? executeMethod(object, method) : null;
        }
    }

    /**
     * Executes the method on the object. This call the {@link Method#setAccessible(boolean)} first to make sure it can
     * be called. Then it calls the {@link Method#invoke(Object, Object...)} to really execute the method. This will
     * also handle any Exception thrown by the invocation by wrapping it in a RuntimeException.
     * 
     * @param object
     *            The object on which to call the method.
     * @param method
     *            The {@link Method} that should be used.
     * @return The result of the invocation.
     */
    public static Object executeMethod(Object object, Method method) {
        try {
            method.setAccessible(true);
            return method.invoke(object);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
        }
    }
}
