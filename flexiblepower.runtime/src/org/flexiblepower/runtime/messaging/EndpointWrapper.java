package org.flexiblepower.runtime.messaging;

import java.io.Closeable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.flexiblepower.messaging.Endpoint;
import org.flexiblepower.messaging.Port;
import org.flexiblepower.messaging.Ports;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EndpointWrapper implements Runnable, Iterable<EndpointPortImpl>, Closeable {
    private static final Logger log = LoggerFactory.getLogger(EndpointWrapper.class);

    private final Endpoint endpoint;
    private final ConnectionManagerImpl connectionManager;

    private final Thread thread;
    private final AtomicBoolean running;
    private final BlockingQueue<Command> commandQueue;

    private final Set<EndpointPortImpl> ports;

    public EndpointWrapper(Endpoint endpoint, ConnectionManagerImpl connectionManager) {
        this.endpoint = endpoint;
        this.connectionManager = connectionManager;

        ports = parsePorts();

        thread = new Thread(this, "Message handler thread for " + endpoint.getClass().getSimpleName());
        running = new AtomicBoolean(true);
        commandQueue = new LinkedBlockingQueue<Command>();

        thread.start();
    }

    private Set<EndpointPortImpl> parsePorts() {
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

        Set<EndpointPortImpl> result = new HashSet<EndpointPortImpl>();
        for (Port port : ports) {
            EndpointPortImpl endpointPort = new EndpointPortImpl(this, port);
            connectionManager.detectPossibleConnections(endpointPort);
            result.add(endpointPort);
        }
        return result;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    @Override
    public Iterator<EndpointPortImpl> iterator() {
        return ports.iterator();
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

        for (EndpointPortImpl port : ports) {
            for (MatchingPortsImpl matchingPort : port.getMatchingPorts()) {
                if (matchingPort.isConnected()) {
                    matchingPort.disconnect();
                }
                port.removeMatch(matchingPort);
                matchingPort.getOtherEnd(port).removeMatch(matchingPort);
            }
        }
    }
}
