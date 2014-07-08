package org.flexiblepower.ral.ext;

import org.flexiblepower.rai.old.ControlSpace;
import org.flexiblepower.rai.old.Controller;
import org.flexiblepower.ral.ResourceControlParameters;
import org.flexiblepower.ral.ResourceDriver;
import org.flexiblepower.ral.ResourceManager;
import org.flexiblepower.ral.ResourceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gives a basic implementation for a {@link ResourceManager}. Any subclass of this class should only implement the
 * {@link #consume(org.flexiblepower.observation.ObservationProvider, org.flexiblepower.observation.Observation)}
 * method.
 * 
 * @param <CS>
 *            The type of {@link ControlSpace}
 * @param <RS>
 *            The type of {@link ResourceState}
 * @param <RCP>
 *            The type of {@link ResourceControlParameters}
 */
public abstract class AbstractResourceManager<CS extends ControlSpace, RS extends ResourceState, RCP extends ResourceControlParameters> implements
                                                                                                                                        ResourceManager<CS, RS, RCP> {
    /**
     * The logger that should by any subclass.
     */
    protected final Logger logger;

    @SuppressWarnings("rawtypes")
    private final Class<? extends ResourceDriver> driverClass;
    private final Class<CS> controlSpaceType;

    private ResourceDriver<? extends RS, ? super RCP> driver;

    private CS currentControlSpace;

    private Controller<? super CS> controller;

    /**
     * Creates a new instance for the specific driver class type and the control space class.
     * 
     * @param driverClass
     *            The class of the driver that is expected.
     * @param controlSpaceType
     *            The class of the control space that is expected.
     */
    @SuppressWarnings("rawtypes")
    protected AbstractResourceManager(Class<? extends ResourceDriver> driverClass, Class<CS> controlSpaceType) {
        this.driverClass = driverClass;
        this.controlSpaceType = controlSpaceType;
        this.logger = LoggerFactory.getLogger(getClass());
    }

    /**
     * @return The last control space that has been sent
     */
    public ControlSpace getCurrentControlSpace() {
        return currentControlSpace;
    }

    /**
     * This helper method publishes the {@link ControlSpace} to its controller if its available.
     * 
     * @param controlSpace
     *            The {@link ControlSpace} that must be published.
     */
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
    public ResourceDriver<? extends RS, ? super RCP> getDriver() {
        return driver;
    }

    @Override
    public Class<CS> getControlSpaceType() {
        return controlSpaceType;
    }

    @Override
    public void registerDriver(ResourceDriver<? extends RS, ? super RCP> driver) {
        if (driver != null && driverClass.isAssignableFrom(driver.getClass())) {
            this.driver = driver;
            driver.subscribe(this);
        }
    }

    @Override
    public void unregisterDriver(ResourceDriver<? extends RS, ? super RCP> driver) {
        if (this.driver == driver) {
            driver.unsubscribe(this);
            this.driver = null;
        }
    }
}
