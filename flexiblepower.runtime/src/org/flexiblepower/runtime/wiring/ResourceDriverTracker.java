package org.flexiblepower.runtime.wiring;

import java.util.HashMap;
import java.util.Map;

import org.flexiblepower.ral.ResourceDriver;
import org.flexiblepower.ral.wiring.ResourceWiringManager;
import org.osgi.framework.BundleContext;

@SuppressWarnings({ "rawtypes", "unchecked" })
class ResourceDriverTracker extends SimpleTracker<ResourceDriver> {
    private final Map<ResourceDriver, String> resourceIds;

    public ResourceDriverTracker(ResourceWiringManagerImpl wiring, BundleContext context) {
        super(wiring, context, ResourceDriver.class, ResourceWiringManager.RESOURCE_ID);
        resourceIds = new HashMap<ResourceDriver, String>();
    }

    @Override
    protected void addedService(ResourceDriver resourceDriver, Object resourceId) {
        if (resourceId != null) {
            logger.debug("Adding driver {} for id [{}]", resourceDriver, resourceId);
            resourceIds.put(resourceDriver, resourceId.toString());
            getResource(resourceId).addDriver(resourceDriver);
        } else {
            resourceIds.put(resourceDriver, null);
        }
    }

    @Override
    protected void modifiedService(ResourceDriver resourceDriver, Object currId) {
        String oldId = resourceIds.get(resourceDriver);

        if (!oldId.equals(currId)) {
            logger.debug("Modifying driver {} for id [{}]", resourceDriver, currId);
            resourceIds.put(resourceDriver, currId.toString());
            getResource(oldId).removeDriver(resourceDriver);
            getResource(currId).addDriver(resourceDriver);
        }
    }

    @Override
    protected void removingService(ResourceDriver resourceDriver) {
        String id = resourceIds.remove(resourceDriver);
        if (id != null) {
            logger.debug("Removing driver {} for id [{}]", resourceDriver, id);
            getResource(id).removeDriver(resourceDriver);
        }
    }
}
