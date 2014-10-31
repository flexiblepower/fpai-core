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
        checkPorts();

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
            EndpointPortImpl storedPort = this.ports.get(port.name());

            if (storedPort == null) {
                log.debug("Adding port on endpoint [{}]: {}", endpoint, port.name());
                this.ports.put(port.name(), endpointPort);
            } else if (storedPort.getPort().sends().length == 0 && storedPort.getPort().accepts().length == 0) {
                if (storedPort.getPort().cardinality() != port.cardinality()) {
                    log.warn("Defined cardinality {} on port {} is different from the implementation port {}",
                             storedPort.getCardinality(),
                             port.name(),
                             port.cardinality());
                }
                log.debug("Replacing port on endpoint [{}]: {}", endpoint, port.name());
                this.ports.put(port.name(), endpointPort);
                storedPort.close();
            } else if (port.sends().length == 0 && port.accepts().length == 0) {
                if (storedPort.getPort().cardinality() != port.cardinality()) {
                    log.warn("Defined cardinality {} on port {} is different from the implementation port {}",
                             storedPort.getCardinality(),
                             port.name(),
                             port.cardinality());
                }
            } else {
                log.error("Implementation of port {} is defined multiple times! Possibly undefined behavior can be expected.",
                          port.name());
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

    private void checkPorts() {
        for (EndpointPortImpl ep : ports.values()) {
            Port port = ep.getPort();
            if (port.sends().length == 0 && port.accepts().length == 0) {
                throw new IllegalArgumentException("The port [" + ep + "] is missing a definition");
            }
        }
    }

    ConnectionManagerImpl getConnectionManager() {
        return connectionManager;
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
        for (EndpointPortImpl port : ports.values()) {
            port.close();
        }

        try {
            running.set(false);
            thread.interrupt();
            thread.join();
        } catch (InterruptedException e) {
        }
    }
}
