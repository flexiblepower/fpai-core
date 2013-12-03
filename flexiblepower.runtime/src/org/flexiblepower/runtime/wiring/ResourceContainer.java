package org.flexiblepower.runtime.wiring;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.flexiblepower.control.ControllerManager;
import org.flexiblepower.ral.ResourceControlParameters;
import org.flexiblepower.ral.ResourceDriver;
import org.flexiblepower.ral.ResourceManager;
import org.flexiblepower.ral.ResourceState;
import org.flexiblepower.ral.wiring.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceContainer<RS extends ResourceState, RCP extends ResourceControlParameters> implements Resource<RS, RCP> {
    private static final Logger logger = LoggerFactory.getLogger(ResourceContainer.class);

    private final String resourceId;

    private ControllerManager controllerManager;
    private final Set<ResourceManager<?, RS, RCP>> managers = new HashSet<ResourceManager<?, RS, RCP>>();
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
            for (ResourceManager<?, RS, RCP> manager : managers) {
                logger.debug("Bound resource manager [{}] to controller [{}]", manager, controller);
                try {
                    controller.registerResource(manager);
                } catch (Exception ex) {
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
        for (ResourceManager<?, RS, RCP> manager : managers) {
            logger.debug("Unbound resource manager [{}] from controller [{}]", manager, controller);
            try {
                controller.unregisterResource(manager);
            } catch (Exception ex) {
                logger.error("Error during unbind: {}", ex.getMessage(), ex);
            }
        }
        this.controllerManager = null;
    }

    public synchronized void addManager(ResourceManager<?, RS, RCP> manager) {
        logger.debug("Adding resource manager for [{}]: {}", resourceId, manager);
        if (managers.add(manager)) {
            if (controllerManager != null) {
                logger.debug("Bound resource manager [{}] to controller [{}]", manager, controllerManager);
                try {
                    controllerManager.registerResource(manager);
                } catch (Exception ex) {
                    logger.error("Error during bind: {}", ex.getMessage(), ex);
                }
            }

            for (ResourceDriver<RS, RCP> driver : drivers) {
                logger.debug("Bound resource manager [{}] to driver [{}]", manager, driver);
                try {
                    manager.registerDriver(driver);
                } catch (Exception ex) {
                    logger.error("Error during bind: {}", ex.getMessage(), ex);
                }
            }
        }
    }

    public synchronized void removeManager(ResourceManager<?, RS, RCP> manager) {
        logger.debug("Removing resource manager for [{}]: {}", resourceId, manager);
        if (managers.remove(manager)) {
            if (controllerManager != null) {
                logger.debug("Unbound resource manager [{}] from controller [{}]", manager, controllerManager);
                try {
                    controllerManager.unregisterResource(manager);
                } catch (Exception ex) {
                    logger.error("Error during unbind: {}", ex.getMessage(), ex);
                }
            }
            for (ResourceDriver<RS, RCP> driver : drivers) {
                logger.debug("Unbound resource manager [{}] from driver [{}]", manager, driver);
                try {
                    manager.unregisterDriver(driver);
                } catch (Exception ex) {
                    logger.error("Error during unbind: {}", ex.getMessage(), ex);
                }
            }
        }
    }

    public synchronized void addDriver(ResourceDriver<RS, RCP> driver) {
        logger.debug("Adding resource driver for [{}]: {}", resourceId, driver);
        if (drivers.add(driver)) {
            for (ResourceManager<?, RS, RCP> manager : managers) {
                logger.debug("Bound resource manager [{}] to driver [{}]", manager, driver);
                try {
                    manager.registerDriver(driver);
                } catch (Exception ex) {
                    logger.error("Error during bind: {}", ex.getMessage(), ex);
                }
            }
        }
    }

    public synchronized void removeDriver(ResourceDriver<RS, RCP> driver) {
        logger.debug("Removing resource driver for [{}]: {}", resourceId, driver);
        if (drivers.remove(driver)) {
            for (ResourceManager<?, RS, RCP> manager : managers) {
                logger.debug("Unbound resource manager [{}] from driver [{}]", manager, driver);
                try {
                    manager.unregisterDriver(driver);
                } catch (Exception ex) {
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
        return controllerManager == null && drivers.isEmpty() && managers.isEmpty();
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
        return Collections.unmodifiableSet(managers);
    }

    @Override
    public String toString() {
        return "Resource [" + resourceId
               + "]: Controller: "
               + controllerManager
               + ", managers: "
               + managers
               + ", drivers: "
               + drivers;
    }
}
