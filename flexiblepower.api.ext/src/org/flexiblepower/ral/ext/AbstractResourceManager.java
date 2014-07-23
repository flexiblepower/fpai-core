package org.flexiblepower.ral.ext;

import java.util.LinkedList;
import java.util.Queue;

import org.flexiblepower.rai.ResourceController;
import org.flexiblepower.rai.ResourceMessageSubmitter;
import org.flexiblepower.rai.ResourceType;
import org.flexiblepower.rai.comm.Allocation;
import org.flexiblepower.rai.comm.ControlSpaceRegistration;
import org.flexiblepower.rai.comm.ResourceMessage;
import org.flexiblepower.ral.ResourceControlParameters;
import org.flexiblepower.ral.ResourceDriver;
import org.flexiblepower.ral.ResourceManager;
import org.flexiblepower.ral.ResourceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gives a basic implementation for a {@link ResourceManager}. Any subclass of this class should only implement the
 * {@link #consume(org.flexiblepower.observation.ObservationProvider, org.flexiblepower.observation.Observation)}
 * method. Also, the @{link {@link #disconnect()} method should be
 * 
 * @param <A>
 *            The type of {@link Allocation} that this {@link ResourceManager} should process
 * @param <RS>
 *            The type of {@link ResourceState}
 * @param <RCP>
 *            The type of {@link ResourceControlParameters}
 */
public abstract class AbstractResourceManager<A extends Allocation, RS extends ResourceState, RCP extends ResourceControlParameters> implements
                                                                                                                                     ResourceManager<A, RS, RCP> {
    /**
     * The logger that should be used by any subclass.
     */
    protected final Logger logger;

    /**
     * Class of the driver
     */
    @SuppressWarnings("rawtypes")
    private final Class<? extends ResourceDriver> driverClass;

    /**
     * Reference to the driver if a driver is connected, otherwise null
     */
    private ResourceDriver<? extends RS, ? super RCP> driver;

    /**
     * Type of resource associated with this {@link ResourceManager}
     */
    private final ResourceType<A, ?, ?> resourceType;

    /**
     * This queue is used to buffer messages before the resourceMessageSubmitter is set. Once the
     * resourceMessageSubmitter is set, all the messages in the queue are submitted. This way the ResourceManager
     * doesn't have to wait for the resourceMessageSubmitter. The {@link ControlSpaceRegistration} message is stored
     * separately, since it might be necessary to reuse it.
     */
    private final Queue<ResourceMessage> messageQueue = new LinkedList<ResourceMessage>();

    /**
     * Sink for {@link ResourceMessage}s. Is null when not connected.
     */
    private ResourceMessageSubmitter resourceMessageSubmitter;

    /**
     * The {@link ControlSpaceRegistration} message created by this {@link ResourceManager}.
     */
    private ControlSpaceRegistration controlSpaceRegistration;

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
        if (controlSpaceRegistration != null) {
            // First send the controlSpaceRegistation
            resourceMessageSubmitter.submitResourceMessage(controlSpaceRegistration);
        }
        while (!messageQueue.isEmpty()) {
            resourceMessageSubmitter.submitResourceMessage(messageQueue.poll());
        }
    }

    /**
     * This helper method publishes a {@link ResourceMessage} to its {@link ResourceController}. When there is not yet a
     * {@link ResourceController} connected the messages are queued.
     * 
     * @param resourceMessage
     *            The {@link ResourceMessage} that must be published.
     */
    protected void publish(ResourceMessage resourceMessage) {
        if (resourceMessage instanceof ControlSpaceRegistration) {
            // This is a ControlSpace registration, store it separately and don't queue it
            if (controlSpaceRegistration != null) {
                throw new IllegalStateException("ResourceManager can only publish one ControlSpaceRegistration");
            } else {
                controlSpaceRegistration = (ControlSpaceRegistration) resourceMessage;
                if (resourceMessageSubmitter != null) {
                    resourceMessageSubmitter.submitResourceMessage(controlSpaceRegistration);
                }
            }
        } else {
            // This is another ResourceMessage, submit it or save it in the queue
            if (resourceMessageSubmitter == null) {
                messageQueue.add(resourceMessage);
            } else {
                resourceMessageSubmitter.submitResourceMessage(resourceMessage);
            }
        }
    }

    /**
     * This method is called when the {@link ResourceController} is disconnected. This method can be overwritten by a
     * subclass, but should always call this implementation to make sure the reference to the
     * {@link ResourceMessageSubmitter} is removed.
     */
    @Override
    public void disconnect() {
        resourceMessageSubmitter = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerDriver(ResourceDriver<? extends RS, ? super RCP> driver) {
        if (driver != null && driverClass.isAssignableFrom(driver.getClass())) {
            this.driver = driver;
            driver.subscribe(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unregisterDriver(ResourceDriver<? extends RS, ? super RCP> driver) {
        if (this.driver == driver) {
            driver.unsubscribe(this);
            this.driver = null;
        }
    }

    /**
     * Get a reference to the {@link ResourceDriver}
     * 
     * @return The connected {@link ResourceDriver}, or null when not connected
     */
    public ResourceDriver<? extends RS, ? super RCP> getDriver() {
        return this.driver;
    }

    /**
     * Indicate if this {@link ResourceManager} is currently connected to a {@link ResourceController}
     * 
     * @return boolean indicating if this {@link ResourceManager} is currently connected to a {@link ResourceController}
     */
    protected boolean isConnectedWithResourceController() {
        return this.resourceMessageSubmitter != null;
    }

    /**
     * Indicate if this {@link ResourceManager} is currently connected to a {@link ResourceDriver}
     * 
     * @return boolean indicating if this {@link ResourceManager} is currently connected to a {@link ResourceDriver}
     */
    protected boolean isConnectedWithResourceDriver() {
        return this.driver != null;
    }

}
