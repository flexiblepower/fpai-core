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

    private static abstract class HalfConnection implements Connection {
        private final Port port;
        private final EndpointWrapper receivingEndpoint;

        public HalfConnection(Port port, EndpointWrapper receivingEndpoint) {
            this.port = port;
            this.receivingEndpoint = receivingEndpoint;
        }

        @Override
        public void sendMessage(Object message) {
            MessageHandler handler = getMessageHandler();
            if (handler != null) {
                receivingEndpoint.addCommand(new Command.HandleMessage(message, handler));
            }
        }

        public abstract MessageHandler getMessageHandler();

        @Override
        public Port getPort() {
            return port;
        }
    }

    private abstract class TempMessageHandler implements MessageHandler {
        @Override
        public void handleMessage(Object message) {
            getRealMessageHandler().handleMessage(message);
        }

        @Override
        public void disconnected() {
            getRealMessageHandler().disconnected();
        }

        private MessageHandler getRealMessageHandler() {
            synchronized (PotentialConnectionImpl.this) {
                while (getMessageHandler() == this) {
                    try {
                        PotentialConnectionImpl.this.wait(100);
                    } catch (InterruptedException e) {
                    }
                }
            }
            return getMessageHandler();
        }

        public abstract MessageHandler getMessageHandler();
    }

    private final EndpointPortImpl left, right;
    private volatile MessageHandler leftMessageHandler, rightMessageHandler;

    public PotentialConnectionImpl(EndpointPortImpl left, EndpointPortImpl right) {
        this.left = left;
        this.right = right;
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
    public synchronized void connect() {
        if (!isConnected()) {
            if (left.getCardinality() == Cardinality.SINGLE && left.isConnected()) {
                throw new IllegalStateException("The port [" + left
                                                + "] is already connected and doesn't support multiple connections");
            } else if (right.getCardinality() == Cardinality.SINGLE && right.isConnected()) {
                throw new IllegalStateException("The port [" + right
                                                + "] is already connected and doesn't support multiple connections");
            }

            log.debug("Connecting port [{}] to port [{}]", left, right);

            leftMessageHandler = new TempMessageHandler() {
                @Override
                public MessageHandler getMessageHandler() {
                    return leftMessageHandler;
                }
            };
            rightMessageHandler = new TempMessageHandler() {
                @Override
                public MessageHandler getMessageHandler() {
                    return rightMessageHandler;
                }
            };

            HalfConnection leftHalfConnection = new HalfConnection(left.getPort(), right.getEndpoint()) {
                @Override
                public MessageHandler getMessageHandler() {
                    return rightMessageHandler;
                }
            };

            HalfConnection rightHalfConnection = new HalfConnection(right.getPort(), left.getEndpoint()) {
                @Override
                public MessageHandler getMessageHandler() {
                    return leftMessageHandler;
                }
            };

            leftMessageHandler = left.getEndpoint().getEndpoint().onConnect(leftHalfConnection);
            rightMessageHandler = right.getEndpoint().getEndpoint().onConnect(rightHalfConnection);
            notifyAll();
        }
    }

    @Override
    public synchronized void disconnect() {
        if (isConnected()) {
            log.debug("Disconnecting port [{}] to port [{}]", left, right);
            try {
                CountDownLatch latch = new CountDownLatch(2);
                left.getEndpoint().addCommand(new Command.Disconnect(leftMessageHandler, latch));
                right.getEndpoint().addCommand(new Command.Disconnect(rightMessageHandler, latch));
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
}
