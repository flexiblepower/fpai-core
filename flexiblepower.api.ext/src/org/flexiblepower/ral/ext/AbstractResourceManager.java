package org.flexiblepower.ral.ext;

import java.util.List;

import org.flexiblepower.messaging.Connection;
import org.flexiblepower.messaging.MessageHandler;
import org.flexiblepower.rai.ResourceController;
import org.flexiblepower.rai.comm.ResourceMessage;
import org.flexiblepower.ral.ResourceControlParameters;
import org.flexiblepower.ral.ResourceDriver;
import org.flexiblepower.ral.ResourceManager;
import org.flexiblepower.ral.ResourceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gives a basic implementation for a {@link ResourceManager} which does simple translation, possible while keeping
 * state information.
 */
public abstract class AbstractResourceManager<RS extends ResourceState, RCP extends ResourceControlParameters> implements
ResourceManager {
    /**
     * The logger that should be used by any subclass.
     */
    protected final Logger logger;

    /**
     * Creates a new instance for the specific driver class type and the control space class.
     */
    protected AbstractResourceManager() {
        logger = LoggerFactory.getLogger(getClass());
    }

    private volatile boolean hasRegistered = false;

    protected abstract List<? extends ResourceMessage> startRegistration(RS state);

    protected abstract List<? extends ResourceMessage> updatedState(RS state);

    protected abstract RCP receivedAllocation(ResourceMessage message);

    private volatile Connection driverConnection, controllerConnection;

    @Override
    public MessageHandler onConnect(Connection connection) {
        if (driverConnection == null && "driver".equals(connection.getPort().name())) {
            driverConnection = connection;
            return new MessageHandler() {
                @SuppressWarnings("unchecked")
                @Override
                public void handleMessage(Object message) {
                    try {
                        if (controllerConnection != null) {
                            List<? extends ResourceMessage> messages = null;
                            if (!hasRegistered) {
                                messages = startRegistration((RS) message);
                                hasRegistered = true;
                            } else {
                                messages = updatedState((RS) message);
                            }

                            if (messages != null) {
                                for (ResourceMessage msg : messages) {
                                    controllerConnection.sendMessage(msg);
                                }
                            }
                        }
                    } catch (ClassCastException ex) {
                        logger.warn("Received unknown message type {}", message.getClass().getName());
                    }
                }

                @Override
                public void disconnected() {
                    hasRegistered = false;
                    driverConnection = null;
                }
            };
        } else if (controllerConnection == null && "controller".equals(connection.getPort().name())) {
            controllerConnection = connection;
            return new MessageHandler() {
                @SuppressWarnings("unchecked")
                @Override
                public void handleMessage(Object message) {
                    try {
                        if (driverConnection != null) {
                            RCP control = receivedAllocation((ResourceMessage) message);
                            if (control != null) {
                                driverConnection.sendMessage(control);
                            }
                        }
                    } catch (ClassCastException ex) {
                        logger.warn("Received unknown message type {}", message.getClass().getName());
                    }

                }

                @Override
                public void disconnected() {
                    hasRegistered = false;
                    controllerConnection = null;
                }
            };
        }
        return null;
    }

    /**
     * Indicate if this {@link ResourceManager} is currently connected to a {@link ResourceController}
     *
     * @return boolean indicating if this {@link ResourceManager} is currently connected to a {@link ResourceController}
     */
    protected boolean isConnectedWithResourceController() {
        return controllerConnection != null;
    }

    /**
     * Indicate if this {@link ResourceManager} is currently connected to a {@link ResourceDriver}
     *
     * @return boolean indicating if this {@link ResourceManager} is currently connected to a {@link ResourceDriver}
     */
    protected boolean isConnectedWithResourceDriver() {
        return driverConnection != null;
    }

}
