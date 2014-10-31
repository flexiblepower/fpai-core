package org.flexiblepower.messaging;

/**
 * <p>
 * This object is created by the {@link ConnectionManager} when two {@link Endpoint}s are connected. It will then be
 * provided to them through the {@link Endpoint#onConnect(Connection)} method.
 * </p>
 * <p>
 * <b>This interface should never be implemented by users</b>
 * </p>
 */
public interface Connection {
    /**
     * Sends a message to the other {@link Endpoint}. This message is handled asynchronously, so it will be handled by
     * the other {@link Endpoint} on a seperate thread.
     *
     * Only messages of the type as defined in the {@link Port#sends()} should be sent.
     *
     * @param message
     *            The message that is to be sent.
     */
    void sendMessage(Object message);

    /**
     * @return The {@link Port} on which this connection has be created.
     */
    Port getPort();
}
