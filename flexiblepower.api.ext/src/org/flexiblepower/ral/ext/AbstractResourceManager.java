package org.flexiblepower.ral.ext;

import org.flexiblepower.rai.ControlSpace;
import org.flexiblepower.rai.Controller;
import org.flexiblepower.ral.ResourceControlParameters;
import org.flexiblepower.ral.ResourceDriver;
import org.flexiblepower.ral.ResourceManager;
import org.flexiblepower.ral.ResourceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractResourceManager<CS extends ControlSpace, RS extends ResourceState, RCP extends ResourceControlParameters> implements
                                                                                                                                        ResourceManager<CS, RS, RCP> {
    protected static final String KEY_APPLIANCE_ID = "applianceId";

    protected final Logger logger;

    private final Class<? extends ResourceDriver<RS, RCP>> driverClass;
    private final Class<CS> controlSpaceType;

    private ResourceDriver<RS, RCP> driver;

    private CS currentControlSpace;

    private Controller<? super CS> controller;

    protected AbstractResourceManager(Class<? extends ResourceDriver<RS, RCP>> driverClass, Class<CS> controlSpaceType) {
        this.driverClass = driverClass;
        this.controlSpaceType = controlSpaceType;
        this.logger = LoggerFactory.getLogger(getClass());
    }

    public ControlSpace getCurrentControlSpace() {
        return currentControlSpace;
    }

    protected void publish(CS controlSpace) {
        this.currentControlSpace = controlSpace;

        if (controller != null) {
            controller.controlSpaceUpdated(this, controlSpace);
        }
    }

    @Override
    public void setController(Controller<? super CS> controller) {
        if (this.controller != null) {
            throw new IllegalStateException("This ResourceManager has already got a controller bound to it");
        }
        this.controller = controller;
    }

    @Override
    public void unsetController(Controller<? super CS> controller) {
        this.controller = null;
    }

    /**
     * @return The {@link ResourceDriver} that is currently linked to this {@link ResourceManager}
     */
    public ResourceDriver<RS, RCP> getDriver() {
        return driver;
    }

    @Override
    public Class<CS> getControlSpaceType() {
        return controlSpaceType;
    }

    @Override
    public void registerDriver(ResourceDriver<RS, RCP> driver) {
        if (this.driver != null && driverClass.isAssignableFrom(driver.getClass())) {
            this.driver = driver;
            driver.subscribe(this);
        }
    }

    @Override
    public void unregisterDriver(ResourceDriver<RS, RCP> driver) {
        if (this.driver == driver) {
            driver.unsubscribe(this);
            this.driver = null;
        }
    }
}
