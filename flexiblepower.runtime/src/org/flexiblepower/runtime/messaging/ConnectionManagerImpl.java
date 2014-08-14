package org.flexiblepower.runtime.messaging;

import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.flexiblepower.messaging.ConnectionManager;
import org.flexiblepower.messaging.Endpoint;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component(immediate = true)
public class ConnectionManagerImpl implements ConnectionManager {
    private static final Logger log = LoggerFactory.getLogger(ConnectionManagerImpl.class);

    private final SortedMap<String, EndpointWrapper> endpointWrappers;

    public ConnectionManagerImpl() {
        endpointWrappers = new TreeMap<String, EndpointWrapper>();
    }

    @Reference(dynamic = true, multiple = true, optional = true, service = Endpoint.class, name = "endpoint")
    public synchronized void addEndpoint(Endpoint endpoint, Map<String, ?> properties) {
        String key = getKey(endpoint, properties);
        if (key != null) {
            endpointWrappers.put(key, new EndpointWrapper(key, endpoint, this));
        }
    }

    public synchronized void removeEndpoint(Endpoint endpoint, Map<String, ?> properties) {
        String key = getKey(endpoint, properties);
        if (key != null) {
            EndpointWrapper endpointWrapper = endpointWrappers.remove(key);
            if (endpointWrapper != null) {
                endpointWrapper.close();
            }
        }
    }

    private String getKey(Endpoint endpoint, Map<String, ?> properties) {
        String key = (String) properties.get(Constants.SERVICE_PID);
        if (key == null) {
            Long id = (Long) properties.get(Constants.SERVICE_ID);
            if (id != null) {
                key = endpoint.getClass().getName() + "-" + id;
            }
        }
        return key;
    }

    void detectPossibleConnections(EndpointPortImpl left) {
        for (EndpointWrapper wrapper : endpointWrappers.values()) {
            for (EndpointPortImpl right : wrapper.getPorts().values()) {
                if (isSubset(left.getPort().sends(), right.getPort().accepts()) && isSubset(right.getPort().sends(),
                                                                                            left.getPort().accepts())) {
                    PotentialConnectionImpl connection = new PotentialConnectionImpl(left, right);
                    log.info("Found matching ports: {} <--> {}", left, right);
                    left.addMatch(connection);
                    right.addMatch(connection);
                }
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
    public ManagedEndpoint getEndpoint(String pid) {
        EndpointWrapper wrapper = endpointWrappers.get(pid);
        if (wrapper == null) {
            SortedMap<String, EndpointWrapper> tailMap = endpointWrappers.tailMap(pid);
            if (!tailMap.isEmpty()) {
                wrapper = tailMap.get(tailMap.firstKey());
                if (!wrapper.getPid().startsWith(pid)) {
                    wrapper = null;
                }
            }
        }
        return wrapper;
    }

    @Override
    public SortedMap<String, EndpointWrapper> getEndpoints() {
        return Collections.unmodifiableSortedMap(endpointWrappers);
    }

    @Override
    public String toString() {
        return endpointWrappers.toString();
    }
}
