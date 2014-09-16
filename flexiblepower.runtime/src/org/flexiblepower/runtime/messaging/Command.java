package org.flexiblepower.runtime.messaging;

import java.util.concurrent.CountDownLatch;

import org.flexiblepower.messaging.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Command {
    void execute();

    public class HandleMessage implements Command {
        private static final Logger log = LoggerFactory.getLogger(Command.HandleMessage.class);

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
            try {
                handler.handleMessage(message);
            } catch (RuntimeException ex) {
                log.error("Error while handling message (" + message + "): " + ex.getMessage(), ex);
            }
        }
    }

    public class Disconnect implements Command {
        private static final Logger log = LoggerFactory.getLogger(Command.Disconnect.class);

        private final MessageHandler handler;
        private final CountDownLatch latch;

        public Disconnect(MessageHandler handler, CountDownLatch latch) {
            this.handler = handler;
            this.latch = latch;
        }

        @Override
        public void execute() {
            try {
                handler.disconnected();
            } catch (RuntimeException ex) {
                log.error("Error while disconnecting: " + ex.getMessage(), ex);
            } finally {
                latch.countDown();
            }
        }
    }
}
