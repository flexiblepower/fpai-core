package org.flexiblepower.messaging;


/**
 * An {@link Endpoint} is one end of a {@link Connection}. This is normally a component in OSGi that is registered as an
 * Endpoint in the service registry. It should use the {@link Port} annotation to indicate what kind of connections it
 * supports.
 */
public interface Endpoint {
    /**
     * Called when a new connection has been established.
     *
     * @param connection
     *            The {@link Connection} object related to the connection.
     * @return This method should return a reference to a
     */
    MessageHandler onConnect(Connection connection);
}
