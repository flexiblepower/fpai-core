package org.flexiblepower.runtime.messaging;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.flexiblepower.messaging.Cardinality;
import org.flexiblepower.messaging.Connection;
import org.flexiblepower.messaging.ConnectionManager.EndpointPort;
import org.flexiblepower.messaging.ConnectionManager.MatchingPorts;
import org.flexiblepower.messaging.MessageHandler;
import org.flexiblepower.messaging.Port;

final class MatchingPortsImpl implements MatchingPorts {
    private static final class HalfConnection implements Connection {
        private final Queue<Object> queue;
        private final Port port;
        private final EndpointPortImpl receivingEndpoint;

        public HalfConnection(Queue<Object> sendingQueue, Port port, EndpointPortImpl receivingEndpoint) {
            queue = sendingQueue;
            this.port = port;
            this.receivingEndpoint = receivingEndpoint;
        }

        @Override
        public void sendMessage(Object message) {
            queue.add(message);

            EndpointWrapper wrapper = receivingEndpoint.getEndpointWrapper();
            synchronized (wrapper) {
                wrapper.newMessage();
                wrapper.notifyAll();
            }
        }

        @Override
        public Port getPort() {
            return port;
        }
    }

    private final EndpointPortImpl left, right;

    private final Queue<Object> leftQueue, rightQueue;
    private MessageHandler leftMessageHandler, rightMessageHandler;

    public MatchingPortsImpl(EndpointPortImpl left, EndpointPortImpl right) {
        this.left = left;
        this.right = right;

        leftQueue = new ConcurrentLinkedQueue<Object>();
        rightQueue = new ConcurrentLinkedQueue<Object>();
    }

    @Override
    public EndpointPortImpl getEitherEnd() {
        return left;
    }

    @Override
    public EndpointPortImpl getOtherEnd(EndpointPort either) {
        return either == left ? right : left;
    }

    @Override
    public synchronized void connect() throws IOException {
        if (isConnected()) {
            throw new IllegalStateException("Already connected");
        } else if (left.getCardinality() == Cardinality.SINGLE && left.isConnected()) {
            throw new IllegalStateException("The port [" + left
                                            + "] is already connected and doesn't support multiple connections");
        } else if (right.getCardinality() == Cardinality.SINGLE && right.isConnected()) {
            throw new IllegalStateException("The port [" + right
                                            + "] is already connected and doesn't support multiple connections");
        }

        leftMessageHandler = left.getEndpoint().onConnect(new HalfConnection(rightQueue, left.getPort(), right));
        rightMessageHandler = right.getEndpoint().onConnect(new HalfConnection(leftQueue, right.getPort(), left));
    }

    @Override
    public synchronized void disconnect() {
        // TODO Should we wait for the queue being emptied?

        leftMessageHandler.disconnected();
        rightMessageHandler.disconnected();
        leftMessageHandler = null;
        rightMessageHandler = null;

        // Just to be sure, empty the queue
        leftQueue.clear();
        rightQueue.clear();
    }

    @Override
    public synchronized boolean isConnected() {
        return leftMessageHandler != null && rightMessageHandler != null;
    }

    void handleMessages(EndpointPortImpl port) {
        if (port == left) {
            assert Thread.currentThread().getName().contains(left.getEndpoint().getClass().getSimpleName());

            while (!leftQueue.isEmpty()) {
                leftMessageHandler.handleMessage(leftQueue.remove());
            }
        } else if (port == right) {
            assert Thread.currentThread().getName().contains(right.getEndpoint().getClass().getSimpleName());

            while (!rightQueue.isEmpty()) {
                rightMessageHandler.handleMessage(rightQueue.remove());
            }
        } else {
            throw new IllegalArgumentException("Don't know that endpoint");
        }
    }
}
