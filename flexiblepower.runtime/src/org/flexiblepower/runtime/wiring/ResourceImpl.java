package org.flexiblepower.runtime.wiring;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.flexiblepower.control.ControllerManager;
import org.flexiblepower.ral.ResourceControlParameters;
import org.flexiblepower.ral.ResourceDriver;
import org.flexiblepower.ral.ResourceManager;
import org.flexiblepower.ral.ResourceState;
import org.flexiblepower.runtime.api.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceImpl<RS extends ResourceState, RCP extends ResourceControlParameters> implements Resource<RS, RCP> {
    private final static Logger logger = LoggerFactory.getLogger(ResourceImpl.class);

    private final String resourceId;

    private ControllerManager controllerManager;
    private final Set<ResourceManager<?, RS, RCP>> managers = new HashSet<ResourceManager<?, RS, RCP>>();
    private final Set<ResourceDriver<RS, RCP>> drivers = new HashSet<ResourceDriver<RS, RCP>>();

    public ResourceImpl(String resourceId) {
        this.resourceId = resourceId;
    }

    public synchronized void setControllerManager(ControllerManager controller) {
        if (this.controllerManager == controller) {
            return;
        }

        if (this.controllerManager != null) {
            logger.warn("Setting the controller while there is already one active! Removed the old one...");
            unsetControllerManager(this.controllerManager);
        }

        this.controllerManager = controller;
        for (ResourceManager<?, RS, RCP> manager : managers) {
            logger.debug("Bound resource manager for [" + resourceId + "] to controller " + controller);
            controller.registerResource(manager);
        }
    }

    public synchronized void unsetControllerManager(ControllerManager controller) {
        if (this.controllerManager != controller) {
            logger.error("Could not unset the controller, because it does not match the registered one");
            return;
        }

        for (ResourceManager<?, RS, RCP> manager : managers) {
            logger.debug("Unbound resource manager for [" + resourceId + "] to controller " + controller);
            controller.unregisterResource(manager);
        }
        this.controllerManager = null;
    }

    public synchronized void addManager(ResourceManager<?, RS, RCP> manager) {
        if (managers.add(manager)) {
            if (controllerManager != null) {
                logger.debug("Bound resource manager for [" + resourceId + "] to controller " + controllerManager);
                controllerManager.registerResource(manager);
            }

            for (ResourceDriver<RS, RCP> driver : drivers) {
                logger.debug("Bound resource driver for [" + resourceId + "] to its manager " + manager);
                manager.registerDriver(driver);
            }
        }
    }

    public synchronized void removeManager(ResourceManager<?, RS, RCP> manager) {
        if (managers.remove(manager)) {
            if (controllerManager != null) {
                logger.debug("Unbound resource manager for [" + resourceId + "] to controller " + controllerManager);
                controllerManager.unregisterResource(manager);
            }
            for (ResourceDriver<RS, RCP> driver : drivers) {
                logger.debug("Unbound resource driver for [" + resourceId + "] to its manager " + manager);
                manager.unregisterDriver(driver);
            }
        }
    }

    public synchronized void addDriver(ResourceDriver<RS, RCP> driver) {
        if (drivers.add(driver)) {
            for (ResourceManager<?, RS, RCP> manager : managers) {
                logger.debug("Bound resource driver for [" + resourceId + "] to its manager " + manager);
                manager.registerDriver(driver);
            }
        }
    }

    public synchronized void removeDriver(ResourceDriver<RS, RCP> driver) {
        if (drivers.remove(driver)) {
            for (ResourceManager<?, RS, RCP> manager : managers) {
                logger.debug("Unbound resource driver for [" + resourceId + "] to its manager " + manager);
                manager.unregisterDriver(driver);
            }
        }
    }

    @Override
    public String getId() {
        return resourceId;
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
