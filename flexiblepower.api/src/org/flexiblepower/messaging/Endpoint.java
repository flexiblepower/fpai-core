package org.flexiblepower.messaging;


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
