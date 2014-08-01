package org.flexiblepower.runtime.messaging;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.flexiblepower.messaging.Cardinality;
import org.flexiblepower.messaging.Connection;
import org.flexiblepower.messaging.MessageHandler;
import org.flexiblepower.messaging.Port;

final class SingleEndpointPort extends AbstractEndpointPort {
    private final Queue<Object> messageQueue;
    private MessageHandler messageHandler;

    public SingleEndpointPort(EndpointWrapper endpoint, Port port) {
        super(endpoint, port);
        assert port.cardinality() == Cardinality.SINGLE;

        messageQueue = new ConcurrentLinkedQueue<Object>();
    }

    @Override
    protected synchronized void connectTo(MatchingPortsImpl matchingPort) {
        if (!getMatchingPorts().contains(matchingPort)) {
            throw new IllegalArgumentException("The given MatchingPort is not of this EndpointPort");
        }

        if (messageHandler != null) {
            throw new IllegalStateException("Already connected");
        }

        final AbstractEndpointPort otherEnd = matchingPort.getOtherEnd(this);
        messageHandler = getEndpoint().onConnect(new Connection() {
            @Override
            public void sendMessage(Object message) {
                otherEnd.addMessage(message);
            }

            @Override
            public Port getPort() {
                return SingleEndpointPort.this.getPort();
            }

            @Override
            public String toString() {
                return "Connection from " + SingleEndpointPort.this + " to " + otherEnd;
            }
        });
    }

    @Override
    protected synchronized void disconnect() {
        // TODO: wait for the messages in the Queue to be handled?
        // messageQueue.clear(); // Just to be sure
        messageHandler.disconnected();
        messageHandler = null;
    }

    @Override
    protected boolean isConnected() {
        return messageHandler != null;
    }

    @Override
    protected void addMessage(Object message) {
        messageQueue.add(message);
        // Notify the Thread running in the EndpointWrapper that there is a new message
        synchronized (getEndpoint()) {
            getEndpoint().notify();
        }
    }

    @Override
    protected void handleMessage() {
        if (messageHandler != null) {
            while (!messageQueue.isEmpty()) {
                messageHandler.handleMessage(messageQueue.poll());
            }
        }
    }
}
