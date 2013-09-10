package org.flexiblepower.observation;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

class ObservationTranslationHelper {
    private static WeakHashMap<Class<?>, Map<String, Method>> cache = new WeakHashMap<Class<?>, Map<String, Method>>();

    static Map<String, Method> getGetterMethods(Class<?> clazz) {
        Map<String, Method> result = cache.get(clazz);
        if (result == null) {
            result = new HashMap<String, Method>();
            for (Method method : clazz.getMethods()) {
                if ((method.getName().startsWith("get") || method.getName().startsWith("is") && method.getParameterTypes().length == 0
                                                           && method.getReturnType() != Void.TYPE) && !method.getName()
                                                                                                             .equals("getClass")) {
                    String name = null;
                    if (name == null || name.isEmpty()) {
                        String n = method.getName();
                        String p = n.startsWith("get") ? n.substring(3) : n.substring(2);
                        name = p.replaceAll("([A-Z])", " $1").toLowerCase().trim().replace(' ', '_');
                    }

                    result.put(name, method);
                }
            }
            cache.put(clazz, result);
        }
        return result;
    }
}
