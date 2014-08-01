package org.flexiblepower.runtime.messaging;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.flexiblepower.messaging.Cardinality;
import org.flexiblepower.messaging.Connection;
import org.flexiblepower.messaging.ConnectionManager.EndpointPort;
import org.flexiblepower.messaging.Endpoint;
import org.flexiblepower.messaging.MessageHandler;
import org.flexiblepower.messaging.Port;

final class EndpointPortImpl implements EndpointPort {
    private final EndpointWrapper endpoint;
    private final Port port;

    private final Set<MatchingPortsImpl> matchingPorts;

    private final Queue<Object> messageQueue;
    private MessageHandler messageHandler;

    public EndpointPortImpl(EndpointWrapper endpoint, Port port) {
        this.endpoint = endpoint;
        this.port = port;

        matchingPorts = new HashSet<MatchingPortsImpl>();

        messageQueue = new ConcurrentLinkedQueue<Object>();
    }

    public EndpointWrapper getEndpointWrapper() {
        return endpoint;
    }

    @Override
    public Endpoint getEndpoint() {
        return endpoint.getEndpoint();
    }

    @Override
    public Cardinality getCardinality() {
        return port.cardinality();
    }

    @Override
    public String getName() {
        return port.name();
    }

    public Port getPort() {
        return port;
    }

    @Override
    public Set<MatchingPortsImpl> getMatchingPorts() {
        return matchingPorts;
    }

    void addMatch(MatchingPortsImpl match) {
        matchingPorts.add(match);
    }

    void removeMatch(MatchingPortsImpl match) {
        matchingPorts.remove(match);
    }

    synchronized void connectTo(MatchingPortsImpl matchingPort) {
        if (!matchingPorts.contains(matchingPort)) {
            throw new IllegalArgumentException("The given MatchingPort is not of this EndpointPort");
        }

        if (port.cardinality() == Cardinality.SINGLE) {
            if (messageHandler != null) {
                throw new IllegalStateException("Already connected");
            }

            final EndpointPortImpl otherEnd = matchingPort.getOtherEnd(this);
            messageHandler = endpoint.getEndpoint().onConnect(new Connection() {
                @Override
                public void sendMessage(Object message) {
                    otherEnd.addMessage(message);
                }

                @Override
                public Port getPort() {
                    return port;
                }

                @Override
                public String toString() {
                    return "Connection from " + EndpointPortImpl.this + " to " + otherEnd;
                }
            });
        } else {
            // TODO
            throw new UnsupportedOperationException("Multiple connections are not yet supported");
        }
    }

    synchronized void disconnect() {
        // TODO: wait for the messages in the Queue to be handled?
        // messageQueue.clear(); // Just to be sure
        messageHandler.disconnected();
        messageHandler = null;
    }

    boolean isConnected() {
        return messageHandler != null;
    }

    void addMessage(Object message) {
        messageQueue.add(message);
        // Notify the Thread running in the EndpointWrapper that there is a new message
        synchronized (endpoint) {
            endpoint.notify();
        }
    }

    void handleMessage() {
        if (messageHandler != null) {
            while (!messageQueue.isEmpty()) {
                messageHandler.handleMessage(messageQueue.poll());
            }
        }
    }

    @Override
    public String toString() {
        return endpoint.getEndpoint().getClass().getSimpleName() + ":" + port.name();
    }
}
