package org.flexiblepower.ral.ext;

import org.flexiblepower.rai.ResourceMessageSubmitter;
import org.flexiblepower.rai.ResourceType;
import org.flexiblepower.rai.comm.Allocation;
import org.flexiblepower.rai.comm.ResourceMessage;
import org.flexiblepower.rai.old.ControlSpace;
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
 * @param <RS>
 *            The type of {@link ResourceState}
 * @param <RCP>
 *            The type of {@link ResourceControlParameters}
 */
public abstract class AbstractResourceManager<A extends Allocation, RS extends ResourceState, RCP extends ResourceControlParameters> implements
                                                                                                                                     ResourceManager<A, RS, RCP> {
    /**
     * The logger that should by any subclass.
     */
    protected final Logger logger;

    @SuppressWarnings("rawtypes")
    private final Class<? extends ResourceDriver> driverClass;

    private ResourceDriver<? extends RS, ? super RCP> driver;

    private final ResourceType<A, ?, ?> resourceType;

    private ResourceMessageSubmitter resourceMessageSubmitter;

    /**
     * Creates a new instance for the specific driver class type and the control space class.
     * 
     * @param driverClass
     *            The class of the driver that is expected.
     * @param controlSpaceType
     *            The class of the control space that is expected.
     */
    @SuppressWarnings("rawtypes")
    protected AbstractResourceManager(Class<? extends ResourceDriver> driverClass, ResourceType<A, ?, ?> resourceType) {
        this.resourceType = resourceType;
        this.driverClass = driverClass;
        this.logger = LoggerFactory.getLogger(getClass());
    }

    @Override
    public ResourceType<A, ?, ?> getResourceType() {
        return resourceType;
    }

    @Override
    public void initialize(ResourceMessageSubmitter resourceMessageSubmitter) {
        this.resourceMessageSubmitter = resourceMessageSubmitter;
    }

    /**
     * This helper method publishes the {@link ControlSpace} to its controller if its available.
     * 
     * @param controlSpace
     *            The {@link ControlSpace} that must be published.
     */
    protected void publish(ResourceMessage resourceMessage) {
        if (resourceMessageSubmitter != null) {
            resourceMessageSubmitter.submitResourceMessage(resourceMessage);
        }
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
