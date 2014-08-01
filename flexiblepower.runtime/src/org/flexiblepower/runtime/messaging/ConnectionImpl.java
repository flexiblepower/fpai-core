package org.flexiblepower.runtime.messaging;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.flexiblepower.messaging.Connection;
import org.flexiblepower.messaging.Port;

public class ConnectionImpl {
    private final AbstractEndpointPort left, right;

    private final Queue<Object> leftQueue, rightQueue;

    public ConnectionImpl(AbstractEndpointPort left, AbstractEndpointPort right) {
        this.left = left;
        this.right = right;

        leftQueue = new ConcurrentLinkedQueue<Object>();
        rightQueue = new ConcurrentLinkedQueue<Object>();
    }

    Connection getLeftPart() {
        return new Connection() {
            @Override
            public void sendMessage(Object message) {
                rightQueue.add(message);
            }

            @Override
            public Port getPort() {
                return left.getPort();
            }
        };
    }

    Connection getRightPart() {
        return new Connection() {
            @Override
            public void sendMessage(Object message) {
                leftQueue.add(message);
            }

            @Override
            public Port getPort() {
                return right.getPort();
            }
        };
    }
}
