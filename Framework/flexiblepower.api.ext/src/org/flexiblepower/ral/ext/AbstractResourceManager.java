package org.flexiblepower.ral.ext;

import org.flexiblepower.rai.ControlSpace;
import org.flexiblepower.rai.Controller;
import org.flexiblepower.rai.ResourceType;
import org.flexiblepower.ral.ResourceDriver;
import org.flexiblepower.ral.ResourceManager;
import org.flexiblepower.ral.ResourceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractResourceManager<RS extends ResourceState, RCP, D extends ResourceDriver<RS, RCP>> implements
                                                                                                                ResourceManager<RS> {
    protected static final String KEY_APPLIANCE_ID = "applianceId";

    protected final Logger logger;

    private final Class<D> driverClass;
    private final ResourceType resourceType;

    private D driver;

    private ControlSpace currentControlSpace;

    private Controller controller;

    public AbstractResourceManager(Class<D> driverClass, ResourceType resourceType) {
        this.driverClass = driverClass;
        this.resourceType = resourceType;
        this.logger = LoggerFactory.getLogger(getClass());
    }

    public ControlSpace getCurrentControlSpace() {
        return currentControlSpace;
    }

    protected void publish(ControlSpace controlSpace) {
        this.currentControlSpace = controlSpace;

        if (controller != null) {
            controller.controlSpaceUpdated(this, controlSpace);
        }
    }

    @Override
    public void setController(Controller controller) {
        if (this.controller != null) {
            throw new IllegalStateException("This ResourceManager has already got a controller bound to it");
        }
        this.controller = controller;
    }

    @Override
    public void unsetController(Controller controller) {
        this.controller = null;
    }

    /**
     * @return The {@link ResourceDriver} that is currently linked to this {@link ResourceManager}
     */
    public D getDriver() {
        return driver;
    }

    @Override
    public ResourceType getResourceType() {
        return resourceType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void registerDriver(ResourceDriver<RS, ?> driver) {
        if (this.driver != null && driverClass.isAssignableFrom(driver.getClass())) {
            this.driver = (D) driver;
            driver.subscribe(this);
        }
    }

    @Override
    public void unregisterDriver(ResourceDriver<RS, ?> driver) {
        if (this.driver == driver) {
            driver.unsubscribe(this);
            this.driver = null;
        }
    }
}
