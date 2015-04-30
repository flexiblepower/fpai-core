package org.flexiblepower.runtime.messaging;

import java.util.concurrent.CountDownLatch;

import org.flexiblepower.messaging.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Command extends Runnable {

    public class HandleMessage implements Command {
        private static final Logger logger = LoggerFactory.getLogger(Command.HandleMessage.class);

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
        public void run() {
            try {
                logger.trace("Handling message {}", message);
                handler.handleMessage(message);
            } catch (RuntimeException ex) {
                logger.error("Error while handling message (" + message + "): " + ex.getMessage(), ex);
            }
        }

        @Override
        public String toString() {
            return "Handle message: " + message.toString();
        }
    }

    public class Disconnect implements Command {
        private static final Logger logger = LoggerFactory.getLogger(Command.Disconnect.class);

        private final MessageHandler handler;
        private final CountDownLatch latch;

        public Disconnect(MessageHandler handler, CountDownLatch latch) {
            this.handler = handler;
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                logger.trace("Disconnecting");
                handler.disconnected();
            } catch (RuntimeException ex) {
                logger.error("Error while disconnecting: " + ex.getMessage(), ex);
            } finally {
                latch.countDown();
            }
        }

        @Override
        public String toString() {
            return "Disconnect command";
        }
    }
}
