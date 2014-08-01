package org.flexiblepower.runtime.messaging;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

    private final Map<Long, EndpointWrapper> endpointWrappers;

    public ConnectionManagerImpl() {
        endpointWrappers = new HashMap<Long, EndpointWrapper>();
    }

    @Reference(dynamic = true, multiple = true, optional = true)
    public synchronized void addEndpoint(Endpoint endpoint, Map<String, ?> properties) {
        Long serviceId = (Long) properties.get(Constants.SERVICE_ID);
        endpointWrappers.put(serviceId, new EndpointWrapper(endpoint, this));
    }

    public synchronized void removeEndpoint(Endpoint endpoint, Map<String, ?> properties) {
        Long serviceId = (Long) properties.get(Constants.SERVICE_ID);
        EndpointWrapper endpointWrapper = endpointWrappers.remove(serviceId);
        endpointWrapper.close();
    }

    void detectPossibleConnections(EndpointPortImpl left) {
        for (EndpointWrapper wrapper : endpointWrappers.values()) {
            for (EndpointPortImpl right : wrapper) {
                if (isSubset(left.getPort().sends(), right.getPort().accepts()) && isSubset(right.getPort().sends(),
                                                                                            left.getPort().accepts())) {
                    MatchingPortsImpl connection = new MatchingPortsImpl(left, right);
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
    public Iterator<EndpointPort> iterator() {
        final Iterator<EndpointWrapper> wrapperIterator = endpointWrappers.values().iterator();
        return new Iterator<EndpointPort>() {
            private boolean loaded = false;
            private EndpointPort current = null;
            private Iterator<EndpointPortImpl> it = null;

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            private void load() {
                if (!loaded) {
                    if ((it == null || !it.hasNext()) && wrapperIterator.hasNext()) {
                        it = wrapperIterator.next().iterator();
                    }
                    if (it.hasNext()) {
                        current = it.next();
                    } else {
                        current = null;
                    }

                    loaded = true;
                }
            }

            @Override
            public EndpointPort next() {
                try {
                    load();
                    return current;
                } finally {
                    loaded = false;
                }
            }

            @Override
            public boolean hasNext() {
                load();
                return current != null;
            }
        };
    }
}
