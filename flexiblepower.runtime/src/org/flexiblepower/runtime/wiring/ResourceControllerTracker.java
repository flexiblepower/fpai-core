package org.flexiblepower.runtime.wiring;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.flexiblepower.control.ControllerManager;
import org.flexiblepower.ral.wiring.ResourceWiringManager;
import org.osgi.framework.BundleContext;

class ResourceControllerTracker extends SimpleTracker<ControllerManager> {
    private final Map<ControllerManager, Set<String>> resourceIds;

    public ResourceControllerTracker(ResourceWiring wiring, BundleContext context) {
        super(wiring, context, ControllerManager.class, ResourceWiringManager.RESOURCE_IDS);
        resourceIds = new HashMap<ControllerManager, Set<String>>();
    }

    @SuppressWarnings("unchecked")
    private Set<String> getIds(Object propIds) {
        if (propIds == null) {
            return Collections.emptySet();
        } else if (propIds instanceof String) {
            return Collections.singleton(propIds.toString());
        } else if (propIds instanceof String[]) {
            Set<String> ids = new HashSet<String>();
            for (String id : (String[]) propIds) {
                ids.add(id);
            }
            return ids;
        } else if (propIds instanceof Collection) {
            return new HashSet<String>((Collection<String>) propIds);
        } else {
            // unknown type, just ignore
            return Collections.emptySet();
        }
    }

    @Override
    protected void addedService(ControllerManager controller, Object propIds) {
        Set<String> ids = getIds(propIds);
        logger.debug("Registering controller {} for ids {}", controller, ids);
        resourceIds.put(controller, ids);
        for (String id : ids) {
            getResource(id).setControllerManager(controller);
        }
    }

    @Override
    protected void modifiedService(ControllerManager controller, Object propIds) {
        Set<String> oldIds = resourceIds.get(controller);
        Set<String> currIds = getIds(propIds);

        if (!oldIds.equals(currIds)) {
            logger.debug("Modifying controller {} for ids {}", controller, currIds);
            Set<String> toRemove = new HashSet<String>(oldIds);
            toRemove.removeAll(currIds);
            for (String id : toRemove) {
                getResource(id).unsetControllerManager(controller);
            }
            logger.debug("Removed controller ids {}", toRemove);

            Set<String> toAdd = new HashSet<String>(currIds);
            toAdd.removeAll(oldIds);
            for (String id : toAdd) {
                getResource(id).setControllerManager(controller);
            }
            logger.debug("Added controller ids {}", toAdd);

            resourceIds.put(controller, currIds);
        }
    }

    @Override
    protected void removingService(ControllerManager controller) {
        Set<String> ids = resourceIds.remove(controller);
        if (ids != null) {
            logger.debug("Removing controller {} for ids {}", controller, ids);
            for (String id : ids) {
                getResource(id).unsetControllerManager(controller);
            }
        }
    }
}
