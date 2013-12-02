package org.flexiblepower.runtime.wiring;

import java.util.HashMap;
import java.util.Map;

import org.flexiblepower.ral.ResourceDriver;
import org.flexiblepower.ral.wiring.ResourceWiringManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

@SuppressWarnings({ "rawtypes", "unchecked" })
class ResourceDriverTracker implements ServiceTrackerCustomizer<ResourceDriver, ResourceDriver> {
    private final ResourceWiringManagerImpl wiring;
    private final ServiceTracker<ResourceDriver, ResourceDriver> tracker;

    private final Map<ResourceDriver, String> resourceIds;

    public ResourceDriverTracker(ResourceWiringManagerImpl wiring, BundleContext context) {
        this.wiring = wiring;
        resourceIds = new HashMap<ResourceDriver, String>();
        tracker = new ServiceTracker<ResourceDriver, ResourceDriver>(context, ResourceDriver.class, this);
        tracker.open();
    }

    public void close() {
        tracker.close();
    }

    @Override
    public synchronized ResourceDriver addingService(ServiceReference<ResourceDriver> reference) {
        ResourceDriver resourceDriver = tracker.addingService(reference);

        Object resourceId = reference.getProperty(ResourceWiringManager.RESOURCE_ID);
        if (resourceId != null) {
            resourceIds.put(resourceDriver, resourceId.toString());
            wiring.getResource(resourceId.toString()).addDriver(resourceDriver);
        }

        return resourceDriver;
    }

    @Override
    public synchronized void modifiedService(ServiceReference<ResourceDriver> reference, ResourceDriver resourceDriver) {
        if (resourceIds.containsKey(resourceDriver)) {
            String oldId = resourceIds.get(resourceDriver);
            Object currId = reference.getProperty(ResourceWiringManager.RESOURCE_ID);

            if (!oldId.equals(currId)) {
                resourceIds.put(resourceDriver, currId.toString());
                wiring.getResource(oldId).removeDriver(resourceDriver);
                wiring.getResource(currId.toString()).addDriver(resourceDriver);
            }
        }
    }

    @Override
    public synchronized void removedService(ServiceReference<ResourceDriver> reference, ResourceDriver resourceDriver) {
        if (resourceIds.containsKey(resourceDriver)) {
            String id = resourceIds.get(resourceDriver);
            wiring.getResource(id).removeDriver(resourceDriver);
            resourceIds.remove(resourceDriver);
        }

        tracker.removedService(reference, resourceDriver);
    }
}
