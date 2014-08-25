package org.flexiblepower.messaging;

import org.osgi.framework.Filter;

/**
 * <p>
 * An {@link Endpoint} is either end of a {@link Connection} that will be used for messaging. Any implementation of this
 * should register itself as an {@link Endpoint} in the service registry of OSGi and add the following properties:
 * </p>
 *
 * <ul>
 *
 * <li> <code>messaging.topic</code> <i>(required)</i> <br/>
 * The name(s) of the topic(s) on which the connection should be set-up. When two endpoints with the same topic are
 * registered, they will be connected. When more than two endpoints are available, every pair will get their own
 * connection (e.g. 4 endpoints will result in 6 active connections). This should be either a <code>String</code> or a
 * <code>String[]</code> when there are multiple topics.</li>
 *
 * <li> <code>messaging.target</code> <i>(optional)</i> <br/>
 * A target {@link Filter} to look for a specific Endpoint(s) to connect with. This will limit the number of connections
 * that will be made based on the topic name.</li>
 *
 * <li> <code>messaging.type</code> <i>(optional)</i> The class names which are valid messages that this class will send
 * across the connection.</li>
 *
 * </ul>
 *
 * <p>
 * Also see the {@link EndpointConfig} for description of the configuration in the form of annotations.
 * </p>
 *
 * <p>
 * The service that will bind the endpoints together must make sure that messages are sent asynchronously. Typically
 * this will be achieved by using a thread pool that will send each message. This will make sure that sending a message
 * is virtually instantaneous.
 * </p>
 *
 * <p>
 * <b>This interface should be implemented by users</b>
 * </p>
 */
public interface Endpoint {
    String TOPIC = "messaging.topic";
    String FILTER = "messaging.filter";
    String TYPE = "messaging.type";

    /**
     * Called when a new connection has been established.
     *
     * @param connection
     *            The {@link Connection} object related to the connection.
     * @return This method should return a reference to a
     */
    MessageHandler onConnect(Connection connection);
}
