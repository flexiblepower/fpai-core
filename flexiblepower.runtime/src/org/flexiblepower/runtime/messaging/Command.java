package org.flexiblepower.runtime.messaging;

import java.util.concurrent.CountDownLatch;

import org.flexiblepower.messaging.MessageHandler;

public interface Command {
    void execute();

    public class HandleMessage implements Command {
        private final Object message;
        private final MessageHandler handler;

        public HandleMessage(Object message, MessageHandler handler) {
            if (message == null || handler == null) {
                throw new NullPointerException();
            }
            this.message = message;
            this.handler = handler;
        }

        @Override
        public void execute() {
            handler.handleMessage(message);
        }

    }

    public class Disconnect implements Command {
        private final MessageHandler handler;
        private final CountDownLatch latch;

        public Disconnect(MessageHandler handler, CountDownLatch latch) {
            this.handler = handler;
            this.latch = latch;
        }

        @Override
        public void execute() {
            handler.disconnected();
            latch.countDown();
        }
    }
}
