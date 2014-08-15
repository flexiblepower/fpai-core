package org.flexiblepower.ral.ext;

import java.util.concurrent.ScheduledExecutorService;

import org.flexiblepower.messaging.Connection;
import org.flexiblepower.messaging.MessageHandler;
import org.flexiblepower.ral.ResourceControlParameters;
import org.flexiblepower.ral.ResourceDriver;
import org.flexiblepower.ral.ResourceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gives a basic implementation for a {@link ResourceDriver}. Any subclass of this class should only implement the
 * {@link #setControlParameters(ResourceControlParameters)} method and some way to periodically send states (e.g. make
 * it {@link Runnable} and use the {@link ScheduledExecutorService} for scheduling itself).
 *
 * @param <RS>
 *            The type of {@link ResourceState}
 * @param <RCP>
 *            The type of {@link ResourceControlParameters}
 */
public abstract class AbstractResourceDriver<RS extends ResourceState, RCP extends ResourceControlParameters> implements
                                                                                                              ResourceDriver {
    /**
     * The logger that should by any subclass.
     */
    protected final Logger logger;

    /**
     * Constructs a new {@link AbstractResourceDriver}.
     */
    public AbstractResourceDriver() {
        this.logger = LoggerFactory.getLogger(getClass());
    }

    private volatile Connection driverConnection;

    @Override
    public MessageHandler onConnect(Connection connection) {
        if (this.driverConnection != null && "manager".equals(connection.getPort().name())) {
            this.driverConnection = connection;
            return new MessageHandler() {
                @SuppressWarnings("unchecked")
                @Override
                public void handleMessage(Object message) {
                    try {
                        handleControlParameters((RCP) message);
                    } catch (ClassCastException ex) {
                        logger.warn("Received an unknown message type [{}]", message.getClass().getName());
                    }
                }

                @Override
                public void disconnected() {
                    driverConnection = null;
                }
            };
        }
        return null;
    }

    protected final void publishState(RS state) {
        if (driverConnection != null) {
            driverConnection.sendMessage(state);
        }
    }

    protected abstract void handleControlParameters(RCP controlParameters);
}
