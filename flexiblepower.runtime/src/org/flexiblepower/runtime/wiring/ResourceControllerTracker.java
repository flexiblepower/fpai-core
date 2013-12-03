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
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ResourceControllerTracker implements ServiceTrackerCustomizer<ControllerManager, ControllerManager> {
    private static final Logger logger = LoggerFactory.getLogger(ResourceControllerTracker.class);

    private final ResourceWiringManagerImpl wiring;
    private final ServiceTracker<ControllerManager, ControllerManager> tracker;

    private final Map<ControllerManager, Set<String>> resourceIds;

    public ResourceControllerTracker(ResourceWiringManagerImpl wiring, BundleContext context) {
        this.wiring = wiring;
        resourceIds = new HashMap<ControllerManager, Set<String>>();
        tracker = new ServiceTracker<ControllerManager, ControllerManager>(context, ControllerManager.class, this);
        tracker.open();
    }

    public void close() {
        tracker.close();
    }

    @SuppressWarnings("unchecked")
    private Set<String> getIds(ServiceReference<ControllerManager> reference) {
        Object propIds = reference.getProperty(ResourceWiringManager.RESOURCE_IDS);
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
    public synchronized ControllerManager addingService(ServiceReference<ControllerManager> reference) {
        ControllerManager controller = tracker.addingService(reference);

        Set<String> ids = getIds(reference);
        logger.debug("Registering controller {} for ids {}", controller, ids);
        resourceIds.put(controller, ids);
        for (String id : ids) {
            wiring.getResource(id).setControllerManager(controller);
        }

        return controller;
    }

    @Override
    public synchronized void
            modifiedService(ServiceReference<ControllerManager> reference, ControllerManager controller) {
        if (resourceIds.containsKey(controller)) {
            Set<String> oldIds = resourceIds.get(controller);
            Set<String> currIds = getIds(reference);

            if (!oldIds.equals(currIds)) {
                logger.debug("Modifying controller {} for ids {}", controller, currIds);
                Set<String> toRemove = new HashSet<String>(oldIds);
                toRemove.removeAll(currIds);
                for (String id : toRemove) {
                    wiring.getResource(id).unsetControllerManager(controller);
                }
                logger.debug("Removed controller ids {}", toRemove);

                Set<String> toAdd = new HashSet<String>(currIds);
                toAdd.removeAll(oldIds);
                for (String id : toAdd) {
                    wiring.getResource(id).setControllerManager(controller);
                }
                logger.debug("Added controller ids {}", toAdd);

                resourceIds.put(controller, currIds);

                wiring.cleanUp();
            }
        }
    }

    @Override
    public synchronized void
            removedService(ServiceReference<ControllerManager> reference, ControllerManager controller) {
        if (resourceIds.containsKey(controller)) {
            Set<String> ids = resourceIds.get(controller);
            logger.debug("Removing controller {} for ids {}", controller, ids);
            for (String id : ids) {
                wiring.getResource(id).unsetControllerManager(controller);
            }
            resourceIds.remove(controller);

            wiring.cleanUp();
        }

        tracker.removedService(reference, controller);
    }
}
