package org.flexiblepower.runtime.messaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.flexiblepower.messaging.Cardinality;
import org.flexiblepower.messaging.ConnectionManager;
import org.flexiblepower.messaging.Endpoint;
import org.osgi.framework.Constants;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Modified;
import aQute.bnd.annotation.component.Reference;
import aQute.bnd.annotation.metatype.Meta;

@Component(immediate = true,
           designate = ConnectionManagerImpl.Config.class,
           configurationPolicy = ConfigurationPolicy.optional)
public class ConnectionManagerImpl implements ConnectionManager {
    private static final String KEY_ACTIVE_CONNECTIONS = "active.connections";
    private static final Logger log = LoggerFactory.getLogger(ConnectionManagerImpl.class);

    private static interface Config {
        @Meta.AD(name = KEY_ACTIVE_CONNECTIONS,
                 deflt = "",
                 description = "List of the active connections (e.g. endpoint:a-endpoint:b)")
        List<String> active_connections();
    }

    private final Map<String, Object> otherProperties;
    private final SortedMap<String, EndpointWrapper> endpointWrappers;

    public ConnectionManagerImpl() {
        endpointWrappers = new TreeMap<String, EndpointWrapper>();
        otherProperties = new HashMap<String, Object>();
        activeConnections = new HashSet<String>();

        // After construction we are in an updating state. Only after
        isUpdatingState = true;
    }

    private ConfigurationAdmin configurationAdmin;

    @Reference
    public void setConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
        this.configurationAdmin = configurationAdmin;
    }

    private final Set<String> activeConnections;
    private Configuration configuration;

    @Activate
    public void activate() throws IOException {
        configuration = configurationAdmin.getConfiguration(getClass().getName());

        // Make sure that the configuration can be updated after activation
        isUpdatingState = false;
        updateConnections();
    }

    @Modified
    public void modified() {
        updateConnections();
    }

    @Deactivate
    public void deactivate() {
        // Make sure that the configuration won't be updated after the deactivation
        isUpdatingState = true;
        configuration = null;
    }

    /*
     * This boolean is used to make sure that either we are updating from the updateConnections method, or we are
     * updating from the the (dis)connectedPort methods. This is to break the endless looping that it would create
     * otherwise.
     */
    private volatile boolean isUpdatingState;

    private final static class Retry {
        private boolean shouldRetry, changedSome;

        Retry() {
            shouldRetry = true;
            changedSome = true;
        }

        void reset() {
            shouldRetry = false;
            changedSome = false;
        }

        void changedSome() {
            changedSome = true;
        }

        void retry() {
            shouldRetry = true;
        }

        boolean shouldRetry() {
            return shouldRetry && changedSome;
        }

        boolean shouldRetryButCant() {
            return shouldRetry && !changedSome;
        }
    }

    /**
     * This method tries to disconnect and connect potential connections to try to match the configuration
     */
    private synchronized void updateConnections() {
        if (!isUpdatingState) {
            log.debug("Start updating the connections");

            isUpdatingState = true;

            activeConnections.clear();
            otherProperties.clear();

            Dictionary<String, Object> properties = configuration.getProperties();
            for (Enumeration<String> keys = properties.keys(); keys.hasMoreElements();) {
                String key = keys.nextElement();
                otherProperties.put(key, properties.get(key));
            }

            Object activeConnections = properties.get(KEY_ACTIVE_CONNECTIONS);
            if (activeConnections instanceof String) {
                this.activeConnections.add(activeConnections.toString());
            } else if (activeConnections instanceof List) {
                for (Object item : (List<?>) activeConnections) {
                    this.activeConnections.add(item.toString());
                }
            } else if (activeConnections instanceof String[]) {
                for (String item : (String[]) activeConnections) {
                    this.activeConnections.add(item);
                }
            } else {
                throw new IllegalArgumentException("The active connections should be a list of strings");
            }
            log.debug("These connections should be active: {}", this.activeConnections);

            Retry retry = new Retry();
            while (retry.shouldRetry()) {
                retry.reset();
                for (EndpointWrapper wrapper : endpointWrappers.values()) {
                    updateWrapper(wrapper, retry);
                }
            }

            if (retry.shouldRetryButCant()) {
                log.warn("Could not connect all configured connections");
            }

            log.debug("Completed updating of connections");

            isUpdatingState = false;
        }
    }

    private void updateWrapper(EndpointWrapper wrapper, Retry retry) {
        for (EndpointPortImpl port : wrapper.getPorts().values()) {
            for (PotentialConnectionImpl connection : port.getPotentialConnections().values()) {
                String key = port.toString();
                boolean contains = activeConnections.contains(key);
                boolean connected = connection.isConnected();
                if (contains && !connected) {
                    if (connection.isConnectable()) {
                        connection.connect();
                        retry.changedSome();
                    } else {
                        retry.retry();
                    }
                } else if (!contains && connected) {
                    connection.disconnect();
                    retry.changedSome();
                }
            }
        }
    }

    /**
     * Adds the key to the active connections and updates the configuration accordingly. This won't update when the
     * {@link #updateConnections()} method is running.
     *
     * @param key
     *            The key of the {@link PotentialConnection} that should be added
     */
    synchronized void connectedPort(String key) {
        if (!isUpdatingState) {
            if (activeConnections.add(key)) {
                storeConnections();
            }
        }
    }

    /**
     * Removes the key from the active connections and updates the configuration accordingly. This won't update when the
     * {@link #updateConnections()} method is running.
     *
     * @param key
     *            The key of the {@link PotentialConnection} that should be added
     */
    synchronized void disconnectedPort(String key) {
        if (!isUpdatingState) {
            if (activeConnections.remove(key)) {
                storeConnections();
            }
        }
    }

    /**
     * Stores the active connections in the configuration of this component
     */
    private void storeConnections() {
        isUpdatingState = true;

        // If the configuration is null, it means that the updates are done while the activate and deactivate methods
        // are not active. This is probably due to the fact that it is booting up or is shutting down. Then we don't
        // need to update the configuration.
        if (configuration != null) {
            Dictionary<String, Object> properties = new Hashtable<String, Object>(otherProperties);
            List<String> activeConnections = new ArrayList<String>(this.activeConnections);
            properties.put(KEY_ACTIVE_CONNECTIONS, activeConnections);

            try {
                configuration.update(properties);
            } catch (IOException e) {
                log.warn("Could not store the new active connections: " + e.getMessage(), e);
            }
        }

        isUpdatingState = false;
    }

    @Reference(dynamic = true, multiple = true, optional = true, service = Endpoint.class, name = "endpoint")
    public synchronized void addEndpoint(Endpoint endpoint, Map<String, ?> properties) {
        try {
            String key = getKey(endpoint, properties);
            if (key != null) {
                EndpointWrapper wrapper = new EndpointWrapper(key, endpoint, this);
                endpointWrappers.put(key, wrapper);
                log.debug("Added endpoint on key [{}]", key);

                // Check the wrapper if some of its potential connections should be started directly
                isUpdatingState = true;
                updateWrapper(wrapper, new Retry());
                isUpdatingState = false;
            }
        } catch (IllegalArgumentException ex) {
            log.warn("Could not add endpoint: {}", ex.getMessage());
        }
    }

    public synchronized void removeEndpoint(Endpoint endpoint, Map<String, ?> properties) {
        // This is to make sure that any disconnects that this will cause, won't delete the configuration for that
        // connection
        isUpdatingState = true;

        String key = getKey(endpoint, properties);
        if (key != null) {
            EndpointWrapper endpointWrapper = endpointWrappers.remove(key);
            if (endpointWrapper != null) {
                endpointWrapper.close();
                log.debug("Removed endpoint on key [{}]", key);
            }
        }

        isUpdatingState = false;
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

    private static boolean isSubset(Class<?>[] sends, Class<?>[] accepts) {
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

    @Override
    public void autoConnect() {
        for (EndpointWrapper ew : endpointWrappers.values()) {
            for (EndpointPortImpl port : ew.getPorts().values()) {
                // Try each port detected in the system. We can only auto-connect ports that have single cardinality
                if (port.getCardinality() == Cardinality.SINGLE) {
                    SortedMap<String, PotentialConnectionImpl> potentialConnections = port.getPotentialConnections();
                    // If there is only 1 potential connection to be made, it can be connected
                    if (potentialConnections.size() == 1) {
                        PotentialConnectionImpl connection = potentialConnections.get(potentialConnections.firstKey());
                        synchronized (connection) {
                            // But only if it not connected already
                            if (!connection.isConnected()) {
                                EndpointPortImpl otherEnd = connection.getOtherEnd(port);
                                // Or if the other is has a single cardinality and has other potential connections that
                                // it can make
                                if ((otherEnd.getCardinality() == Cardinality.SINGLE && otherEnd.getPotentialConnections()
                                                                                                .size() == 1) || otherEnd.getCardinality() == Cardinality.MULTIPLE) {
                                    connection.connect();
                                    log.debug("Autoconnected [" + port + "] to [" + otherEnd + "]");
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
