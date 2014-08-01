package org.flexiblepower.runtime.messaging;

import java.io.Closeable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.flexiblepower.messaging.Cardinality;
import org.flexiblepower.messaging.Endpoint;
import org.flexiblepower.messaging.Port;
import org.flexiblepower.messaging.Ports;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EndpointWrapper implements Runnable, Iterable<AbstractEndpointPort>, Closeable {
    private static final Logger log = LoggerFactory.getLogger(EndpointWrapper.class);

    private final Endpoint endpoint;
    private final ConnectionManagerImpl connectionManager;

    private final Thread thread;
    private final AtomicBoolean running;

    private final Set<AbstractEndpointPort> ports;

    public EndpointWrapper(Endpoint endpoint, ConnectionManagerImpl connectionManager) {
        this.endpoint = endpoint;
        this.connectionManager = connectionManager;

        ports = parsePorts();

        thread = new Thread(this, "Message handler thread for " + endpoint.getClass().getSimpleName());
        running = new AtomicBoolean(true);

        thread.start();
    }

    private Set<AbstractEndpointPort> parsePorts() {
        Port[] ports = null;

        Ports portsAnnotation = endpoint.getClass().getAnnotation(Ports.class);
        if (portsAnnotation != null) {
            ports = portsAnnotation.value();
        } else {
            Port portAnnotation = endpoint.getClass().getAnnotation(Port.class);
            if (portAnnotation != null) {
                ports = new Port[] { portAnnotation };
            } else {
                log.warn("Found an Endpoint with no Port definition: {}", endpoint.getClass().getSimpleName());
                return Collections.emptySet();
            }
        }

        Set<AbstractEndpointPort> result = new HashSet<AbstractEndpointPort>();
        for (Port port : ports) {
            AbstractEndpointPort endpointPort = port.cardinality() == Cardinality.SINGLE ? new SingleEndpointPort(this,
                                                                                                                  port)
                                                                                        : new MultipleEndpointPort(this,
                                                                                                                   port);
            connectionManager.detectPossibleConnections(endpointPort);
            result.add(endpointPort);
        }
        return result;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    @Override
    public Iterator<AbstractEndpointPort> iterator() {
        return ports.iterator();
    }

    @Override
    public void run() {
        while (running.get()) {
            synchronized (thread) {
                for (AbstractEndpointPort port : ports) {
                    try {
                        port.handleMessage();
                    } catch (Exception ex) {
                        log.error("Uncaught exception while handling message on port " + port + ": " + ex.getMessage(),
                                  ex);
                        log.warn("Closing the port because of the previous exception");
                        port.disconnect();
                    }
                }
                try {
                    thread.wait(1000);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    @Override
    public void close() {
        synchronized (thread) {
            running.set(false);
            thread.notifyAll();
        }
        try {
            thread.join();
        } catch (InterruptedException e) {
        }

        for (AbstractEndpointPort port : ports) {
            port.disconnect();
            for (MatchingPortsImpl matchingPort : port.getMatchingPorts()) {
                port.removeMatch(matchingPort);
                matchingPort.getOtherEnd(port).removeMatch(matchingPort);
            }
        }
    }
}
