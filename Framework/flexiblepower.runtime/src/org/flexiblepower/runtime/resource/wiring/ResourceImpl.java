package org.flexiblepower.runtime.resource.wiring;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.flexiblepower.control.ControllerManager;
import org.flexiblepower.ral.ResourceDriver;
import org.flexiblepower.ral.ResourceManager;
import org.flexiblepower.ral.ResourceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceImpl<RS extends ResourceState> implements Resource<RS> {
    private final static Logger logger = LoggerFactory.getLogger(ResourceImpl.class);

    private final String resourceId;

    private ControllerManager controller;
    private final Set<ResourceManager<RS>> managers = new HashSet<ResourceManager<RS>>();
    private final Set<ResourceDriver<RS, ?>> drivers = new HashSet<ResourceDriver<RS, ?>>();

    public ResourceImpl(String resourceId) {
        this.resourceId = resourceId;
    }

    public synchronized void setController(ControllerManager controller) {
        if (this.controller == controller) {
            return;
        }

        if (this.controller != null) {
            logger.warn("Setting the controller while there is already one active! Removed the old one...");
            unsetController(this.controller);
        }

        this.controller = controller;
        for (ResourceManager<RS> manager : managers) {
            logger.debug("Bound resource manager for [" + resourceId + "] to controller " + controller);
            controller.registerResource(manager);
        }
    }

    public synchronized void unsetController(ControllerManager controller) {
        if (this.controller != controller) {
            logger.error("Could not unset the controller, because it does not match the registered one");
            return;
        }

        for (ResourceManager<RS> manager : managers) {
            logger.debug("Unbound resource manager for [" + resourceId + "] to controller " + controller);
            controller.unregisterResource(manager);
        }
        this.controller = null;
    }

    public synchronized void addManager(ResourceManager<RS> manager) {
        if (managers.add(manager)) {
            if (controller != null) {
                logger.debug("Bound resource manager for [" + resourceId + "] to controller " + controller);
                controller.registerResource(manager);
            }

            for (ResourceDriver<RS, ?> driver : drivers) {
                logger.debug("Bound resource driver for [" + resourceId + "] to its manager " + manager);
                manager.registerDriver(driver);
            }
        }
    }

    public synchronized void removeManager(ResourceManager<RS> manager) {
        if (managers.remove(manager)) {
            if (controller != null) {
                logger.debug("Unbound resource manager for [" + resourceId + "] to controller " + controller);
                controller.unregisterResource(manager);
            }
            for (ResourceDriver<RS, ?> driver : drivers) {
                logger.debug("Unbound resource driver for [" + resourceId + "] to its manager " + manager);
                manager.unregisterDriver(driver);
            }
        }
    }

    public synchronized void addDriver(ResourceDriver<RS, ?> driver) {
        if (drivers.add(driver)) {
            for (ResourceManager<RS> manager : managers) {
                logger.debug("Bound resource driver for [" + resourceId + "] to its manager " + manager);
                manager.registerDriver(driver);
            }
        }
    }

    public synchronized void removeDriver(ResourceDriver<RS, ?> driver) {
        if (drivers.remove(driver)) {
            for (ResourceManager<RS> manager : managers) {
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
    public ControllerManager getController() {
        return controller;
    }

    @Override
    public Set<ResourceDriver<RS, ?>> getResourceDrivers() {
        return Collections.unmodifiableSet(drivers);
    }

    @Override
    public Set<ResourceManager<RS>> getResourceManagers() {
        return Collections.unmodifiableSet(managers);
    }

    @Override
    public String toString() {
        return "Resource [" + resourceId
               + "]: Controller: "
               + controller
               + ", managers: "
               + managers
               + ", drivers: "
               + drivers;
    }
}
