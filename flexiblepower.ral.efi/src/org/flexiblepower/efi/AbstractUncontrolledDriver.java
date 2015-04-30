package org.flexiblepower.efi;

import org.flexiblepower.efi.uncontrolled.UncontrolledRegistration;
import org.flexiblepower.messaging.Connection;
import org.flexiblepower.messaging.MessageHandler;
import org.flexiblepower.ral.ControllerManager;
import org.flexiblepower.ral.ResourceState;
import org.flexiblepower.ral.messages.Allocation;
import org.flexiblepower.ral.messages.ResourceMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractUncontrolledDriver<RS extends ResourceState> implements UncontrolledResourceManager {
    /**
     * The logger that should be used by any subclass.
     */
    protected final Logger logger;

    /**
     * Creates a new instance for the specific driver class type and the control space class.
     */
    public AbstractUncontrolledDriver() {
        logger = LoggerFactory.getLogger(getClass());
    }

    /**
     * This method is called when it is connected to the controller and the registration object should be sent.
     *
     * @return The {@link UncontrolledRegistration} that will be sent to the controller.
     */
    protected abstract UncontrolledRegistration startRegistration();

    /**
     * This method is called when a message has been received from the controller. This will generally be an
     * {@link Allocation} object, but could also be of another type, depending on the used message type.
     *
     * @param message
     *            The received message
     */
    protected abstract void receivedAllocation(ResourceMessage message);

    private volatile Connection controllerConnection;

    @Override
    public MessageHandler onConnect(Connection connection) {
        if (controllerConnection == null && "controller".equals(connection.getPort().name())) {
            controllerConnection = connection;
            controllerConnection.sendMessage(startRegistration());

            return new MessageHandler() {
                @Override
                public void handleMessage(Object message) {
                    try {
                        receivedAllocation((ResourceMessage) message);
                    } catch (ClassCastException ex) {
                        logger.warn("Received unknown message type {}", message.getClass().getName());
                    }
                }

                @Override
                public void disconnected() {
                    controllerConnection = null;
                }
            };
        }
        return null;
    }

    /**
     * Indicate if this driver is currently connected to a {@link ControllerManager}.
     *
     * @return boolean indicating if this driver is currently connected to a {@link ControllerManager}
     */
    protected final boolean isConnectedWithResourceController() {
        return controllerConnection != null;
    }

    /**
     * Sends a message to the attached controller. The call is ignored when it is not connected.
     *
     * @param message
     *            The message that is to be sent to the controller.
     */
    protected void sendMessage(ResourceMessage message) {
        if (controllerConnection != null) {
            controllerConnection.sendMessage(message);
        }
    }
}
