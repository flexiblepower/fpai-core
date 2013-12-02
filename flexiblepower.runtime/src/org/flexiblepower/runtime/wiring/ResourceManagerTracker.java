package org.flexiblepower.runtime.wiring;

import java.util.HashMap;
import java.util.Map;

import org.flexiblepower.ral.ResourceManager;
import org.flexiblepower.ral.wiring.ResourceWiringManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({ "rawtypes", "unchecked" })
class ResourceManagerTracker implements ServiceTrackerCustomizer<ResourceManager, ResourceManager> {
    private static final Logger logger = LoggerFactory.getLogger(ResourceManagerTracker.class);

    private final ResourceWiringManagerImpl wiring;
    private final ServiceTracker<ResourceManager, ResourceManager> tracker;

    private final Map<ResourceManager, String> resourceIds;

    public ResourceManagerTracker(ResourceWiringManagerImpl wiring, BundleContext context) {
        this.wiring = wiring;
        resourceIds = new HashMap<ResourceManager, String>();
        tracker = new ServiceTracker<ResourceManager, ResourceManager>(context, ResourceManager.class, this);
        tracker.open();
    }

    public void close() {
        tracker.close();
    }

    @Override
    public synchronized ResourceManager addingService(ServiceReference<ResourceManager> reference) {
        ResourceManager resourceManager = tracker.addingService(reference);

        Object resourceId = reference.getProperty(ResourceWiringManager.RESOURCE_ID);
        if (resourceId != null) {
            logger.debug("Adding manager {} for id [{}]", resourceManager, resourceId);
            resourceIds.put(resourceManager, resourceId.toString());
            wiring.getResource(resourceId.toString()).addManager(resourceManager);
        }

        return resourceManager;
    }

    @Override
    public synchronized void modifiedService(ServiceReference<ResourceManager> reference,
                                             ResourceManager resourceManager) {
        if (resourceIds.containsKey(resourceManager)) {
            String oldId = resourceIds.get(resourceManager);
            Object currId = reference.getProperty(ResourceWiringManager.RESOURCE_ID);

            if (!oldId.equals(currId)) {
                logger.debug("Modifying manager {} for id [{}]", resourceManager, currId);
                resourceIds.put(resourceManager, currId.toString());
                wiring.getResource(oldId).removeManager(resourceManager);
                wiring.getResource(currId.toString()).addManager(resourceManager);

                wiring.cleanUp();
            }
        }
    }

    @Override
    public synchronized void
            removedService(ServiceReference<ResourceManager> reference, ResourceManager resourceManager) {
        if (resourceIds.containsKey(resourceManager)) {
            String id = resourceIds.get(resourceManager);
            logger.debug("Removing manager {} for id [{}]", resourceManager, id);
            wiring.getResource(id).removeManager(resourceManager);
            resourceIds.remove(resourceManager);

            wiring.cleanUp();
        }

        tracker.removedService(reference, resourceManager);
    }
}
