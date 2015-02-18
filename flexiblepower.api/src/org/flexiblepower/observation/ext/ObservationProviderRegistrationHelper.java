package org.flexiblepower.observation.ext;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.flexiblepower.observation.ObservationProvider;
import org.flexiblepower.observation.ObservationTranslationHelper;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * This helper class should be used for correct registration of ObservationProviders in the service repository. This
 * class is using the builder pattern, such that you can easily do a registration in a single line.
 */
public class ObservationProviderRegistrationHelper {
    /**
     * This is the base for all the property-keys that will be registered.
     */
    public static final String KEY_BASE = "org.flexiblepower.monitoring";
    /**
     * This is the key that will be used for describing who is going to do the observing.
     */
    public static final String KEY_OBSERVED_BY = KEY_BASE + ".observedBy";
    /**
     * The is the key that will be used for describing what is being observed.
     */
    public static final String KEY_OBSERVATION_OF = KEY_BASE + ".observationOf";
    /**
     * This is the base for the key that will be used to describe the type.
     */
    public static final String KEY_OBSERVATION_TYPE = KEY_BASE + ".type";

    private final BundleContext bundleContext;
    private final Hashtable<String, Object> properties;
    private final Object serviceObject;

    /**
     * Creates a new instance of this class.
     * 
     * @param serviceObject
     *            The object that will be put into the service registry during the {@link #register(Class...)} method.
     */
    public ObservationProviderRegistrationHelper(Object serviceObject) {
        this(serviceObject, FrameworkUtil.getBundle(serviceObject.getClass()).getBundleContext());
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param serviceObject
     *            The object that will be put into the service registry during the {@link #register(Class...)} method.
     * @param context
     *            The bundleContext that will be used for service registration.
     */
    public ObservationProviderRegistrationHelper(Object serviceObject, BundleContext context) {
        this.serviceObject = serviceObject;
        bundleContext = context;
        properties = new Hashtable<String, Object>();
        properties.put(KEY_OBSERVED_BY, serviceObject.getClass().getName());
        properties.put(KEY_OBSERVATION_OF, "unknown");
    }

    /**
     * Set a custom property.
     * 
     * @param key
     *            The key of the property.
     * @param value
     *            The value of the property.
     * @return this
     */
    public ObservationProviderRegistrationHelper setProperty(String key, Object value) {
        properties.put(key, value);
        return this;
    }

    /**
     * Sets the observedBy property.
     * 
     * @param observedBy
     *            A short description of the thing that is being observed. By default this is the classname of the
     *            service object.
     * @return this
     */
    public ObservationProviderRegistrationHelper observedBy(String observedBy) {
        return setProperty(KEY_OBSERVED_BY, observedBy);
    }

    /**
     * Sets the observationOf property.
     * 
     * @param observationOf
     *            A short description of the thing that is being observed. Usually this is the resource identifier.
     * @return this
     */
    public ObservationProviderRegistrationHelper observationOf(String observationOf) {
        return setProperty(KEY_OBSERVATION_OF, observationOf);
    }

    private static void addInterfaces(Class<?> baseClass, Set<String> interfaces) {
        interfaces.add(baseClass.getName());
        for (Class<?> iface : baseClass.getInterfaces()) {
            addInterfaces(iface, interfaces);
        }
    }

    /**
     * Sets all of the type.* properties using the given type of observations.
     * 
     * @param observationClass
     *            The type of the observations.
     * @return this
     */
    public ObservationProviderRegistrationHelper observationType(Class<?> observationClass) {
        Set<String> interfaces = new HashSet<String>();
        addInterfaces(observationClass, interfaces);
        setProperty(KEY_OBSERVATION_TYPE, interfaces.toArray(new String[interfaces.size()]));
        addType(KEY_OBSERVATION_TYPE, observationClass, new HashSet<Class<?>>());
        return this;
    }

    private void addType(String parentPrefix, Class<?> observationClass, HashSet<Class<?>> visitedClasses) {
        Map<String, Method> methods = ObservationTranslationHelper.getGetterMethods(observationClass);
        for (Entry<String, Method> entry : methods.entrySet()) {
            addTypeField(parentPrefix, entry.getKey(), entry.getValue(), visitedClasses);
        }
    }

    private void addTypeField(String parentPrefix, String name, Method method, HashSet<Class<?>> visitedClasses) {
        String prefix = parentPrefix + "." + name;
        Class<?> type = method.getReturnType();

        if (type.isEnum() || type.equals(String.class)) {
            setProperty(prefix, "string");
        } else {
            setProperty(prefix, type.getName());
        }

        ObservationAttribute annotation = method.getAnnotation(ObservationAttribute.class);
        if (annotation != null) {
            setProperty(prefix + ".unit", annotation.unit());
            setProperty(prefix + ".optional", annotation.optional());
        }

        if (ObservationTranslationHelper.isJavaBean(type)) {
            if (!visitedClasses.add(type)) {
                throw new IllegalArgumentException("Circular typing detected in Observation type [" + type + "]");
            }
            addType(prefix, type, visitedClasses);
            visitedClasses.remove(type);
        }
    }

    /**
     * Registers the service object with all of the set properties in the service registry.
     * 
     * @param otherInterfaces
     *            Any other interfaces (next to the default {@link ObservationProvider}) that this service should
     *            register itself by.
     * @return this
     */
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
