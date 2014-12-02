package org.flexiblepower.runtime.messaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.flexiblepower.messaging.ConnectionManager.EndpointPort;
import org.flexiblepower.messaging.Filter;
import org.flexiblepower.messaging.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageListenerContainer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(MessageListenerContainer.class);

    private final Map<MessageListener, List<Class<?>>> messageListeners;
    private final BlockingQueue<Data> data;
    private final Thread thread;
    private volatile boolean running;

    public MessageListenerContainer() {
        messageListeners = new ConcurrentHashMap<MessageListener, List<Class<?>>>();
        data = new LinkedBlockingQueue<MessageListenerContainer.Data>();
        running = true;

        thread = new Thread(this, "MessageListeners Thread");
        thread.start();
    }

    public synchronized void addMessageListener(MessageListener messageListener) {
        List<Class<?>> filter = new ArrayList<Class<?>>();
        Filter annotation = messageListener.getClass().getAnnotation(Filter.class);
        if (annotation != null) {
            for (Class<?> clazz : annotation.value()) {
                filter.add(clazz);
            }
        }

        messageListeners.put(messageListener, filter);
    }

    public synchronized void removeMessageListener(MessageListener messageListener) {
        messageListeners.remove(messageListener);
    }

    public void publishMessage(EndpointPort from, EndpointPort to, Object message) {
        data.add(new Data(from, to, message));
    }

    private boolean matches(List<Class<?>> filter, Class<? extends Object> clazz) {
        if (filter == null || filter.isEmpty()) {
            // No effective filter, so it matches
            return true;
        }
        for (Class<?> filterClass : filter) {
            if (filterClass.isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }

    public void close() {
        try {
            running = false;
            thread.interrupt();
            thread.join();
        } catch (InterruptedException ex) {
            // Ignore
        }
    }

    @Override
    public void run() {
        while (running) {
            try {
                Data data = this.data.take();
                for (Entry<MessageListener, List<Class<?>>> entry : messageListeners.entrySet()) {
                    MessageListener messageListener = entry.getKey();
                    List<Class<?>> filter = entry.getValue();
                    if (matches(filter, data.getMessage().getClass())) {
                        messageListener.handleMessage(data.getFrom(), data.getTo(), data.getMessage());
                    }
                }
            } catch (InterruptedException ex) {
                // Ignore
            } catch (Exception ex) {
                logger.warn("Error while publishing message to a listener: " + ex.getMessage(), ex);
            }
        }
    }

    public static final class Data {
        private final EndpointPort from, to;
        private final Object message;

        public Data(EndpointPort from, EndpointPort to, Object message) {
            this.from = from;
            this.to = to;
            this.message = message;
        }

        public EndpointPort getFrom() {
            return from;
        }

        public EndpointPort getTo() {
            return to;
        }

        public Object getMessage() {
            return message;
        }
    }
}
