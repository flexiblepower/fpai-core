package org.flexiblepower.runtime.wiring;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.flexiblepower.control.ControllerManager;
import org.flexiblepower.rai.ResourceType;
import org.flexiblepower.ral.ResourceControlParameters;
import org.flexiblepower.ral.ResourceDriver;
import org.flexiblepower.ral.ResourceManager;
import org.flexiblepower.ral.ResourceState;
import org.flexiblepower.ral.wiring.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceContainer<RS extends ResourceState, RCP extends ResourceControlParameters> implements
                                                                                                Resource<RS, RCP> {
    private static final Logger logger = LoggerFactory.getLogger(ResourceContainer.class);

    private final String resourceId;

    private final ResourceType<?, ?, ?> resourceType = null;

    private ControllerManager controllerManager;
    private final Map<ResourceManager<?, RS, RCP>, ControlCommunication> resourceManagers = new HashMap<ResourceManager<?, RS, RCP>, ControlCommunication>();
    private final Set<ResourceDriver<RS, RCP>> drivers = new HashSet<ResourceDriver<RS, RCP>>();

    public ResourceContainer(String resourceId) {
        this.resourceId = resourceId;
    }

    public synchronized void setControllerManager(ControllerManager controller) {
        if (this.controllerManager == controller) {
            return;
        }

        if (this.controllerManager != null) {
            logger.warn("Adding the controller while there is already one active! Ignoring...");
        } else {
            logger.debug("Adding resource controller for [{}]: {}", resourceId, controller);
            this.controllerManager = controller;
            for (ResourceManager<?, RS, RCP> resourceManager : resourceManagers.keySet()) {
                logger.debug("Bound resource manager [{}] to controller [{}]", resourceManager, controller);
                try {
                    ControlCommunication communication = new ControlCommunication(controllerManager, resourceManager);
                    resourceManagers.put(resourceManager, communication);
                } catch (Throwable ex) {
                    logger.error("Error during bind: {}", ex.getMessage(), ex);
                }
            }
        }
    }

    public synchronized void unsetControllerManager(ControllerManager controller) {
        if (this.controllerManager != controller) {
            logger.error("Could not remove the controller, because it does not match the registered one");
            return;
        }

        logger.debug("Removing resource controller for [{}]: {}", resourceId, controller);
        for (ResourceManager<?, RS, RCP> manager : resourceManagers.keySet()) {
            logger.debug("Unbound resource manager [{}] from controller [{}]", manager, controller);
            try {
                ControlCommunication controlCommunication = resourceManagers.get(manager);
                if (controlCommunication != null) {
                    controlCommunication.disconnect();
                    resourceManagers.put(manager, null);
                }
            } catch (Throwable ex) {
                logger.error("Error during unbind: {}", ex.getMessage(), ex);
            }
        }
        this.controllerManager = null;
    }

    public synchronized void addResourceManager(ResourceManager<?, RS, RCP> resourceManager) {
        logger.debug("Adding resource manager for [{}]: {}", resourceId, resourceManager);
        if (!resourceManagers.containsKey(resourceManager)) {
            if (controllerManager != null) {
                logger.debug("Bound resource manager [{}] to controller [{}]", resourceManager, controllerManager);
                try {
                    ControlCommunication communication = new ControlCommunication(controllerManager, resourceManager);
                    resourceManagers.put(resourceManager, communication);
                } catch (Throwable ex) {
                    logger.error("Error during bind: {}", ex.getMessage(), ex);
                }
            } else {
                resourceManagers.put(resourceManager, null);
            }

            for (ResourceDriver<RS, RCP> driver : drivers) {
                logger.debug("Bound resource manager [{}] to driver [{}]", resourceManager, driver);
                try {
                    resourceManager.registerDriver(driver);
                } catch (Throwable ex) {
                    logger.error("Error during bind: {}", ex.getMessage(), ex);
                }
            }
        }
    }

    public synchronized void removeResourceManager(ResourceManager<?, RS, RCP> manager) {
        logger.debug("Removing resource manager for [{}]: {}", resourceId, manager);
        if (resourceManagers.containsKey(manager)) {
            if (controllerManager != null) {
                logger.debug("Unbound resource manager [{}] from controller [{}]", manager, controllerManager);
                try {
                    ControlCommunication controlCommunication = resourceManagers.get(manager);
                    if (controlCommunication != null) {
                        controlCommunication.disconnect();
                    }
                } catch (Throwable ex) {
                    logger.error("Error during unbind: {}", ex.getMessage(), ex);
                }
            }
            resourceManagers.remove(manager);
            for (ResourceDriver<RS, RCP> driver : drivers) {
                logger.debug("Unbound resource manager [{}] from driver [{}]", manager, driver);
                try {
                    manager.unregisterDriver(driver);
                } catch (Throwable ex) {
                    logger.error("Error during unbind: {}", ex.getMessage(), ex);
                }
            }
        }
    }

    public synchronized void addDriver(ResourceDriver<RS, RCP> driver) {
        logger.debug("Adding resource driver for [{}]: {}", resourceId, driver);
        if (drivers.add(driver)) {
            for (ResourceManager<?, RS, RCP> manager : resourceManagers.keySet()) {
                logger.debug("Bound resource manager [{}] to driver [{}]", manager, driver);
                try {
                    manager.registerDriver(driver);
                } catch (Throwable ex) {
                    logger.error("Error during bind: {}", ex.getMessage(), ex);
                }
            }
        }
    }

    public synchronized void removeDriver(ResourceDriver<RS, RCP> driver) {
        logger.debug("Removing resource driver for [{}]: {}", resourceId, driver);
        if (drivers.remove(driver)) {
            for (ResourceManager<?, RS, RCP> manager : resourceManagers.keySet()) {
                logger.debug("Unbound resource manager [{}] from driver [{}]", manager, driver);
                try {
                    manager.unregisterDriver(driver);
                } catch (Throwable ex) {
                    logger.error("Error during unbind: {}", ex.getMessage(), ex);
                }
            }
        }
    }

    @Override
    public String getId() {
        return resourceId;
    }

    public boolean isEmpty() {
        return controllerManager == null && drivers.isEmpty() && resourceManagers.isEmpty();
    }

    @Override
    public ControllerManager getControllerManager() {
        return controllerManager;
    }

    @Override
    public Set<ResourceDriver<RS, RCP>> getResourceDrivers() {
        return Collections.unmodifiableSet(drivers);
    }

    @Override
    public Set<ResourceManager<?, RS, RCP>> getResourceManagers() {
        return Collections.unmodifiableSet(resourceManagers.keySet());
    }

    @Override
    public String toString() {
        return "Resource [" + resourceId
               + "]: Controller: "
               + controllerManager
               + ", managers: "
               + resourceManagers
               + ", drivers: "
               + drivers;
    }

    @Override
    public ResourceType<?, ?, ?> getResourceType() {
        return this.resourceType;
    }
}
