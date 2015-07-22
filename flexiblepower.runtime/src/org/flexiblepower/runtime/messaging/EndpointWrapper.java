package org.flexiblepower.runtime.messaging;

import java.io.Closeable;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.flexiblepower.context.FlexiblePowerContext;
import org.flexiblepower.messaging.ConnectionManager.ManagedEndpoint;
import org.flexiblepower.messaging.Endpoint;
import org.flexiblepower.messaging.Port;
import org.flexiblepower.messaging.Ports;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link EndpointWrapper} wraps the {@link Endpoint} object and makes sure that each message is handled on a
 * separate thread.
 */
public class EndpointWrapper implements ManagedEndpoint, Closeable {
    private static final Logger logger = LoggerFactory.getLogger(EndpointWrapper.class);

    private final String pid;
    private final Endpoint endpoint;
    private final ConnectionManagerImpl connectionManager;

    private final BundleContext bundleContext;
    /** {@link ServiceReference} to the {@link FlexiblePowerContext} if there is one, otherwise null */
    private final ServiceReference<FlexiblePowerContext> serviceReference;
    /** The {@link FlexiblePowerContext} to schedule commands if there is one, otherwise null */
    private final FlexiblePowerContext endpointContext;
    /** In case there is no {@link FlexiblePowerContext}, this {@link ExecutorService} is used to submit commands */
    private final ExecutorService executorService;

    private final SortedMap<String, EndpointPortImpl> ports;

    /**
     * @param pid
     *            The persistent identifier of the {@link Endpoint} that uniquely identifies it.
     * @param endpoint
     *            The reference to the {@link Endpoint} object
     * @param connectionManager
     *            The reference back to the implementation of the connection manager
     */
    public EndpointWrapper(String pid, Endpoint endpoint, ConnectionManagerImpl connectionManager) {
        this.pid = pid;
        this.endpoint = endpoint;
        this.connectionManager = connectionManager;
        ports = new TreeMap<String, EndpointPortImpl>();
        parsePorts(endpoint.getClass());
        checkPorts();

        Bundle bundle = FrameworkUtil.getBundle(endpoint.getClass());
        if (bundle == null || bundle.getBundleContext() == null
            || bundle.getBundleContext().getServiceReference(FlexiblePowerContext.class) == null) {
            // We are not able to find a FlexiblePowerContext. Create an ExecutorService to deliver messages.
            executorService = Executors.newSingleThreadExecutor();
            bundleContext = null;
            serviceReference = null;
            endpointContext = null;
        } else {
            // Use the FlexiblePowerContext to deliver messages.
            executorService = null;
            bundleContext = bundle.getBundleContext();
            serviceReference = bundleContext.getServiceReference(FlexiblePowerContext.class);
            endpointContext = bundleContext.getService(serviceReference);
        }
    }

    private void parsePorts(Class<?> clazz) {
        logger.debug("Start detection of ports on {}", clazz.getName());
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
                logger.debug("Adding port on endpoint [{}]: {}", endpoint, port.name());
                this.ports.put(port.name(), endpointPort);
            } else if (storedPort.getPort().sends().length == 0 && storedPort.getPort().accepts().length == 0) {
                if (storedPort.getPort().cardinality() != port.cardinality()) {
                    logger.warn("Defined cardinality {} on port {} is different from the implementation port {}",
                                storedPort.getCardinality(),
                                port.name(),
                                port.cardinality());
                }
                logger.debug("Replacing port on endpoint [{}]: {}", endpoint, port.name());
                this.ports.put(port.name(), endpointPort);
                storedPort.close();
            } else if (port.sends().length == 0 && port.accepts().length == 0) {
                if (storedPort.getPort().cardinality() != port.cardinality()) {
                    logger.warn("Defined cardinality {} on port {} is different from the implementation port {}",
                                storedPort.getCardinality(),
                                port.name(),
                                port.cardinality());
                }
            } else {
                logger.error("Implementation of port {} is defined multiple times! Possibly undefined behavior can be expected.",
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

    /**
     * @return The real object that has been wrapped.
     */
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

    void addCommand(Command command) {
        if (endpointContext == null) {
            executorService.submit(command);
        } else { // executorService == null
            endpointContext.submit(command);
        }
    }

    @Override
    public void close() {
        for (EndpointPortImpl port : ports.values()) {
            port.close();
        }

        if (bundleContext != null) {
            bundleContext.ungetService(serviceReference);
        }
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
