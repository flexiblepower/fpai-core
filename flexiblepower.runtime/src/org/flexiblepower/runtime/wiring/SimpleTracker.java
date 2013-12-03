package org.flexiblepower.runtime.wiring;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import org.flexiblepower.ral.ResourceControlParameters;
import org.flexiblepower.ral.ResourceState;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SimpleTracker<T> implements ServiceListener {
    protected final Logger logger;
    private final ResourceWiringManagerImpl wiring;
    private final BundleContext context;
    private final Map<ServiceReference<T>, T> trackedReferences;
    private final String propertyKey;
    private final Class<T> clazz;
    private final AtomicBoolean started;

    public SimpleTracker(ResourceWiringManagerImpl wiring, BundleContext context, Class<T> clazz, String propertyKey) {
        this.wiring = wiring;
        this.context = context;
        this.trackedReferences = new HashMap<ServiceReference<T>, T>();
        this.propertyKey = propertyKey;
        this.clazz = clazz;
        logger = LoggerFactory.getLogger(getClass());
        started = new AtomicBoolean(false);
    }

    public void start() {
        if (started.compareAndSet(false, true)) {
            try {
                context.addServiceListener(this, "(" + Constants.OBJECTCLASS + "=" + clazz.getName() + ")");
                Collection<ServiceReference<T>> references = context.getServiceReferences(clazz, null);
                for (ServiceReference<T> reference : references) {
                    track(reference);
                }
                logger.info("Tracker started");
            } catch (InvalidSyntaxException e) {
                // Should never happen with the null
                throw new RuntimeException(e);
            }
        }
    }

    public synchronized void close() {
        if (started.compareAndSet(true, false)) {
            logger.info("Tracker is closing down");
            context.removeServiceListener(this);
            for (Entry<ServiceReference<T>, T> entry : trackedReferences.entrySet()) {
                removingService(entry.getValue());
                context.ungetService(entry.getKey());
            }
            wiring.cleanUp();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void serviceChanged(ServiceEvent event) {
        switch (event.getType()) {
        case ServiceEvent.REGISTERED:
        case ServiceEvent.MODIFIED:
            track((ServiceReference<T>) event.getServiceReference());
            break;
        case ServiceEvent.MODIFIED_ENDMATCH:
        case ServiceEvent.UNREGISTERING:
            untrack((ServiceReference<T>) event.getServiceReference());
            break;
        default:
            logger.warn("Unknown service event, code " + event.getType());
        }
    }

    protected ResourceImpl<ResourceState, ResourceControlParameters> getResource(Object id) {
        return wiring.getResource(id.toString());
    }

    protected abstract void addedService(T service, Object property);

    protected abstract void modifiedService(T service, Object property);

    protected abstract void removingService(T service);

    private synchronized void track(ServiceReference<T> reference) {
        if (started.get()) {
            if (!trackedReferences.containsKey(reference)) {
                T resourceManager = context.getService(reference);
                trackedReferences.put(reference, resourceManager);
                addedService(resourceManager, reference.getProperty(propertyKey));
            } else {
                T resourceManager = trackedReferences.get(reference);
                modifiedService(resourceManager, reference.getProperty(propertyKey));
                wiring.cleanUp();
            }
        }
    }

    private synchronized void untrack(ServiceReference<T> reference) {
        if (started.get()) {
            T resourceManager = trackedReferences.remove(reference);
            if (resourceManager != null) {
                removingService(resourceManager);
                wiring.cleanUp();
                context.ungetService(reference);
            }
        }
    }
}
