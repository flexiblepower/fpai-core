package org.flexiblepower.observation;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.flexiblepower.observation.ObservationAttribute;
import org.flexiblepower.observation.ObservationProvider;
import org.flexiblepower.observation.ObservationTranslationHelper;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

public class ObservationProviderRegistrationHelper {
    public static final String KEY_BASE = "org.flexiblepower.monitoring";
    public static final String KEY_OBSERVED_BY = KEY_BASE + ".observedBy";
    public static final String KEY_OBSERVATION_OF = KEY_BASE + ".observationOf";
    public static final String KEY_OBSERVATION_TYPE = KEY_BASE + ".type";

    private final BundleContext bundleContext;
    private final Hashtable<String, Object> properties;
    private final Object serviceObject;

    public ObservationProviderRegistrationHelper(Object serviceObject) {
        this.serviceObject = serviceObject;
        bundleContext = FrameworkUtil.getBundle(serviceObject.getClass()).getBundleContext();
        properties = new Hashtable<String, Object>();
    }

    public ObservationProviderRegistrationHelper setProperty(String key, Object value) {
        properties.put(key, value);
        return this;
    }

    public ObservationProviderRegistrationHelper observedBy(String observedBy) {
        return setProperty(KEY_OBSERVED_BY, observedBy);
    }

    public ObservationProviderRegistrationHelper observationOf(String observationOf) {
        return setProperty(KEY_OBSERVATION_OF, observationOf);
    }

    private static void addInterfaces(Class<?> baseClass, Set<String> interfaces) {
        interfaces.add(baseClass.getName());
        for (Class<?> iface : baseClass.getInterfaces()) {
            addInterfaces(iface, interfaces);
        }
    }

    public ObservationProviderRegistrationHelper observationType(Class<?> observationClass) {
        Set<String> interfaces = new HashSet<String>();
        addInterfaces(observationClass, interfaces);
        setProperty(KEY_OBSERVATION_TYPE, interfaces.toArray(new String[interfaces.size()]));

        Map<String, Method> methods = ObservationTranslationHelper.getGetterMethods(observationClass);
        for (Entry<String, Method> entry : methods.entrySet()) {
            String name = entry.getKey();
            Method method = entry.getValue();

            Class<?> type = method.getReturnType();
            String typeName = type.isEnum() ? String.class.getName() : type.getName();
            setProperty(KEY_OBSERVATION_TYPE + "." + name, typeName);

            ObservationAttribute annotation = method.getAnnotation(ObservationAttribute.class);
            if (annotation != null) {
                setProperty(KEY_OBSERVATION_TYPE + "." + name + ".unit", annotation.unit());
                setProperty(KEY_OBSERVATION_TYPE + "." + name + ".optional", annotation.optional());
            }
        }

        return this;
    }

    public ServiceRegistration<?> register(Class<?>... otherInterfaces) {
        if (serviceObject == null) {
            throw new NullPointerException("No serviceObject has been set");
        }

        String[] classes = new String[otherInterfaces.length + 1];
        classes[0] = ObservationProvider.class.getName();
        for (int i = 0; i < otherInterfaces.length; i++) {
            classes[i + 1] = otherInterfaces[i].getName();
        }
        return bundleContext.registerService(classes, serviceObject, properties);
    }
}
