package org.flexiblepower.runtime.messaging;

import java.util.concurrent.CountDownLatch;

import org.flexiblepower.messaging.Cardinality;
import org.flexiblepower.messaging.Connection;
import org.flexiblepower.messaging.ConnectionManager.EndpointPort;
import org.flexiblepower.messaging.ConnectionManager.PotentialConnection;
import org.flexiblepower.messaging.MessageHandler;
import org.flexiblepower.messaging.Port;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class PotentialConnectionImpl implements PotentialConnection {
    private static final Logger log = LoggerFactory.getLogger(PotentialConnectionImpl.class);

    private static final MessageHandler DUMP = new MessageHandler() {
        @Override
        public void handleMessage(Object message) {
            log.debug("Dumping message {}", message);
        }

        @Override
        public void disconnected() {
        }
    };

    private static final class HalfConnection implements Connection {
        private final Port port;
        private final EndpointWrapper receivingEndpoint;
        volatile MessageHandler messageHandler;

        public HalfConnection(Port port, EndpointWrapper receivingEndpoint) {
            this.port = port;
            this.receivingEndpoint = receivingEndpoint;
            messageHandler = null;
        }

        synchronized void setMessageHandler(MessageHandler messageHandler) {
            if (this.messageHandler != null) {
                throw new IllegalStateException("The messageHandler should only be set once");
            }
            this.messageHandler = messageHandler;
            notifyAll();
        }

        @Override
        public void sendMessage(Object message) {
            if (message == null) {
                log.warn("Trying to send a null message to {}, ignoring", receivingEndpoint.getPid());
                return;
            }

            MessageHandler messageHandler = this.messageHandler;
            if (messageHandler == null) {
                messageHandler = new MessageHandler() {
                    @Override
                    public void handleMessage(Object message) {
                        synchronized (HalfConnection.this) {
                            while (HalfConnection.this.messageHandler == null) {
                                try {
                                    HalfConnection.this.wait(1000);
                                } catch (InterruptedException e) {
                                }
                            }

                            HalfConnection.this.messageHandler.handleMessage(message);
                        }
                    }

                    @Override
                    public void disconnected() {
                        throw new AssertionError("This method should never be called");
                    }
                };
            }

            receivingEndpoint.addCommand(new Command.HandleMessage(message, messageHandler));
        }

        @Override
        public Port getPort() {
            return port;
        }
    }

    private final EndpointPortImpl left, right;
    private volatile MessageHandler leftMessageHandler, rightMessageHandler;

    public PotentialConnectionImpl(EndpointPortImpl left, EndpointPortImpl right) {
        if (left.equals(right)) {
            throw new IllegalArgumentException("You can not connect an Endpoint to itself");
        }
        if (left.toString().compareTo(right.toString()) < 0) {
            this.left = left;
            this.right = right;
        } else {
            this.left = right;
            this.right = left;
        }
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
    public synchronized boolean isConnectable() {
        return (!isConnected() && getConnectableError() == null);
    }

    private String getConnectableError() {
        if (left.getCardinality() == Cardinality.SINGLE && left.isConnected()) {
            return "The port [" + left
                   + "] is already connected and doesn't support multiple connections";
        } else if (right.getCardinality() == Cardinality.SINGLE && right.isConnected()) {
            return "The port [" + right
                   + "] is already connected and doesn't support multiple connections";
        } else {
            return null;
        }
    }

    @Override
    public synchronized void connect() {
        if (!isConnected()) {
            String connectableError = getConnectableError();
            if (connectableError != null) {
                throw new IllegalStateException(connectableError);
            }

            log.debug("Connecting port [{}] to port [{}]", left, right);

            HalfConnection leftHalfConnection = new HalfConnection(left.getPort(), right.getEndpoint());
            HalfConnection rightHalfConnection = new HalfConnection(right.getPort(), left.getEndpoint());

            leftMessageHandler = left.getEndpoint().getEndpoint().onConnect(leftHalfConnection);
            rightMessageHandler = right.getEndpoint().getEndpoint().onConnect(rightHalfConnection);

            if (leftMessageHandler == null || rightMessageHandler == null) {
                log.warn("Could not connect port [{}] to port [{}], because the onConnect failed (returned null)",
                         left,
                         right);

                // When one of the connects fails, we need to dump the messages that are received
                leftHalfConnection.setMessageHandler(DUMP);
                rightHalfConnection.setMessageHandler(DUMP);
                disconnect();
            } else {
                leftHalfConnection.setMessageHandler(rightMessageHandler);
                rightHalfConnection.setMessageHandler(leftMessageHandler);

                left.getEndpoint().getConnectionManager().connectedPort(toString());
                log.debug("Connected port [{}] to port [{}]", left, right);
            }
        }
    }

    @Override
    public synchronized void disconnect() {
        close();
        left.getEndpoint().getConnectionManager().disconnectedPort(toString());
    }

    synchronized void close() {
        if (isConnected()) {
            log.debug("Disconnecting port [{}] to port [{}]", left, right);
            try {
                CountDownLatch latch = new CountDownLatch(2);

                if (leftMessageHandler != null) {
                    left.getEndpoint().addCommand(new Command.Disconnect(leftMessageHandler, latch));
                } else {
                    latch.countDown();
                }

                if (rightMessageHandler != null) {
                    right.getEndpoint().addCommand(new Command.Disconnect(rightMessageHandler, latch));
                } else {
                    latch.countDown();
                }

                latch.await();
            } catch (InterruptedException e) {
            }

            leftMessageHandler = null;
            rightMessageHandler = null;
        }
    }

    @Override
    public synchronized boolean isConnected() {
        return leftMessageHandler != null && rightMessageHandler != null;
    }

    @Override
    public String toString() {
        return left.toString() + "-" + right.toString();
    }
}
