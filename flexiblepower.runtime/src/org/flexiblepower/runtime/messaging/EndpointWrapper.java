package org.flexiblepower.runtime.messaging;

import java.io.Closeable;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.flexiblepower.messaging.ConnectionManager.ManagedEndpoint;
import org.flexiblepower.messaging.Endpoint;
import org.flexiblepower.messaging.Port;
import org.flexiblepower.messaging.Ports;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EndpointWrapper implements Runnable, ManagedEndpoint, Closeable {
    private static final Logger log = LoggerFactory.getLogger(EndpointWrapper.class);

    private final String pid;
    private final Endpoint endpoint;
    private final ConnectionManagerImpl connectionManager;

    private final Thread thread;
    private final AtomicBoolean running;
    private final BlockingQueue<Command> commandQueue;

    private final SortedMap<String, EndpointPortImpl> ports;

    public EndpointWrapper(String pid, Endpoint endpoint, ConnectionManagerImpl connectionManager) {
        this.pid = pid;
        this.endpoint = endpoint;
        this.connectionManager = connectionManager;
        ports = new TreeMap<String, EndpointPortImpl>();
        parsePorts(endpoint.getClass());

        thread = new Thread(this, "Message handler thread for " + endpoint.getClass().getSimpleName());
        running = new AtomicBoolean(true);
        commandQueue = new LinkedBlockingQueue<Command>();

        thread.start();
    }

    private void parsePorts(Class<?> clazz) {
        log.debug("Start detection of ports on {}", clazz.getName());
        Port[] ports = null;

        Ports portsAnnotation = clazz.getAnnotation(Ports.class);
        if (portsAnnotation != null) {
            ports = portsAnnotation.value();
        } else {
            Port portAnnotation = clazz.getAnnotation(Port.class);
            if (portAnnotation != null) {
                ports = new Port[] { portAnnotation };
            } else {
                ports = new Port[0];
            }
        }

        for (Port port : ports) {
            EndpointPortImpl endpointPort = new EndpointPortImpl(this, port);
            connectionManager.detectPossibleConnections(endpointPort);
            if (!this.ports.containsKey(port.name())) {
                log.debug("Adding port on endpoint [{}]: {}", endpoint, port.name());
                this.ports.put(port.name(), endpointPort);
            }
        }

        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null && superclass != Object.class) {
            parsePorts(superclass);
        }
        for (Class<?> interfaceClass : clazz.getInterfaces()) {
            if (interfaceClass != Endpoint.class) {
                parsePorts(interfaceClass);
            }
        }
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    @Override
    public String getPid() {
        return pid;
    }

    @Override
    public EndpointPortImpl getPort(String name) {
        return ports.get(name);
    }

    @Override
    public SortedMap<String, EndpointPortImpl> getPorts() {
        return ports;
    }

    @Override
    public void run() {
        while (running.get()) {
            try {
                commandQueue.take().execute();
            } catch (InterruptedException ex) {
                // Is expected, the thread is probably closing down
            }
        }
    }

    void addCommand(Command command) {
        commandQueue.add(command);
    }

    @Override
    public void close() {
        synchronized (thread) {
            running.set(false);
            notifyAll();
        }
        try {
            thread.join();
        } catch (InterruptedException e) {
        }

        for (EndpointPortImpl port : ports.values()) {
            for (PotentialConnectionImpl matchingPort : port.getPotentialConnections().values()) {
                if (matchingPort.isConnected()) {
                    matchingPort.disconnect();
                }
                port.removeMatch(matchingPort);
                matchingPort.getOtherEnd(port).removeMatch(matchingPort);
            }
        }
    }
}
