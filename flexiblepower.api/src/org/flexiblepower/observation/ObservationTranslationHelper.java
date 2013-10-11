package org.flexiblepower.observation;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

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

    public static Map<String, Method> getGetterMethods(Class<?> clazz) {
        Map<String, Method> result = cache.get(clazz);
        if (result == null) {
            result = createGetterMethods(clazz);
            cache.put(clazz, result);
        }
        return result;
    }
}
