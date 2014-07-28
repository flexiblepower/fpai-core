package org.flexiblepower.runtime.messaging;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.flexiblepower.messaging.ConnectionManager;
import org.flexiblepower.messaging.Endpoint;
import org.flexiblepower.messaging.Port;
import org.flexiblepower.messaging.Ports;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component(immediate = true)
public class ConnectionManagerImpl implements ConnectionManager {
    private static final Logger log = LoggerFactory.getLogger(ConnectionManagerImpl.class);

    private final Set<EndpointPortImpl> endpointPorts;

    public ConnectionManagerImpl() {
        endpointPorts = new HashSet<EndpointPortImpl>();
    }

    @Reference(dynamic = true, multiple = true, optional = true)
    public synchronized void addEndpoint(Endpoint endpoint, Map<String, ?> properties) {
        Port[] ports = null;

        Ports portsAnnotation = endpoint.getClass().getAnnotation(Ports.class);
        if (portsAnnotation != null) {
            ports = portsAnnotation.value();
        } else {
            Port portAnnotation = endpoint.getClass().getAnnotation(Port.class);
            if (portAnnotation != null) {
                ports = new Port[] { portAnnotation };
            } else {
                log.warn("Found an Endpoint with no Port definition (pid={})", properties.get("service.pid"));
            }
        }

        for (Port port : ports) {
            EndpointPortImpl endpointPort = new EndpointPortImpl(endpoint, port);
            detectPossibleConnections(endpointPort);
            endpointPorts.add(endpointPort);
        }
    }

    public synchronized void removeEndpoint(Endpoint endpoint, Map<String, ?> properties) {
        Iterator<EndpointPortImpl> it = endpointPorts.iterator();
        while (it.hasNext()) {
            EndpointPortImpl endpointStore = it.next();
            if (endpointStore.getEndpoint() == endpoint) {
                for (PortMatchImpl connection : endpointStore.getMatchingPorts()) {
                    if (connection.isConnected()) {
                        connection.disconnect();
                    }
    
                    connection.getEitherEnd().removeConnection(connection);
                    connection.getOtherEnd(connection.getEitherEnd()).removeConnection(connection);
                }
                it.remove();
            }
        }
    }

    private void detectPossibleConnections(EndpointPortImpl left) {
        for (EndpointPortImpl right : endpointPorts) {
            if (isSubset(left.getPort().sends(), right.getPort().accepts()) && isSubset(right.getPort().sends(),
                                                                                        left.getPort().accepts())) {
                PortMatchImpl connection = new PortMatchImpl(left, right);
                log.info("Possible connection found: {} <--> {}", left, right);
                left.addConnection(connection);
                right.addConnection(connection);
            }
        }
    }

    private boolean isSubset(Class<?>[] sends, Class<?>[] accepts) {
        boolean correct = true;
        for (Class<?> send : sends) {
            correct = false;
            for (Class<?> accept : accepts) {
                if (accept.isAssignableFrom(send)) {
                    correct = true;
                    break;
                }
            }
            if (!correct) {
                break;
            }
        }
        return correct;
    }

    @Override
    public Set<? extends EndpointPort> getEndpointPorts() {
        return endpointPorts;
    }
}
