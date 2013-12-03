package org.flexiblepower.runtime.wiring;

import java.util.HashMap;
import java.util.Map;

import org.flexiblepower.ral.ResourceManager;
import org.flexiblepower.ral.wiring.ResourceWiringManager;
import org.osgi.framework.AllServiceListener;
import org.osgi.framework.BundleContext;

@SuppressWarnings({ "rawtypes", "unchecked" })
class ResourceManagerTracker extends SimpleTracker<ResourceManager> implements AllServiceListener {
    private final Map<ResourceManager, String> resourceIds;

    public ResourceManagerTracker(ResourceWiring wiring, BundleContext context) {
        super(wiring, context, ResourceManager.class, ResourceWiringManager.RESOURCE_ID);
        resourceIds = new HashMap<ResourceManager, String>();
    }

    @Override
    protected void addedService(ResourceManager resourceManager, Object resourceId) {
        if (resourceId != null) {
            logger.debug("Adding manager {} for id [{}]", resourceManager, resourceId);
            resourceIds.put(resourceManager, resourceId.toString());
            getResource(resourceId).addManager(resourceManager);
        } else {
            resourceIds.put(resourceManager, null);
        }
    }

    @Override
    protected void modifiedService(ResourceManager resourceManager, Object currId) {
        String oldId = resourceIds.get(resourceManager);

        if (!oldId.equals(currId)) {
            logger.debug("Modifying manager {} for id [{}]", resourceManager, currId);
            resourceIds.put(resourceManager, currId.toString());
            getResource(oldId).removeManager(resourceManager);
            getResource(currId).addManager(resourceManager);
        }
    }

    @Override
    protected void removingService(ResourceManager resourceManager) {
        String id = resourceIds.remove(resourceManager);
        if (id != null) {
            logger.debug("Removing manager {} for id [{}]", resourceManager, id);
            getResource(id).removeManager(resourceManager);
        }
    }
}
