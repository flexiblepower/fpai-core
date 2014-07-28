package org.flexiblepower.messaging;

/**
 * An instance of the {@link MessageHandler} should be returned when a connection is coupled to an endpoint (see
 * {@link Endpoint#onConnect(Connection)}.
 *
 * <p>
 * <b>This interface should be implemented by users</b>
 * </p>
 */
public interface MessageHandler {
    /**
     * Called when a new message is available for processing. This method is always called on a separate {@link Thread}
     * by the runtime environment.
     *
     * @param message
     *            The message that should be handled.
     */
    void handleMessage(Object message);

    /**
     * Called when the related connection has been destroyed. Further calls to the related
     * {@link Connection#sendMessage(Object)} will be ignored.
     */
    void disconnected();
}
