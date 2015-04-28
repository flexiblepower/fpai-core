package org.flexiblepower.runtime.ui.connectionspage;

import org.flexiblepower.messaging.ConnectionManager;
import org.flexiblepower.messaging.ConnectionManager.EndpointPort;
import org.flexiblepower.messaging.ConnectionManager.ManagedEndpoint;
import org.flexiblepower.messaging.ConnectionManager.PotentialConnection;
import org.osgi.framework.BundleContext;

public class BaseWidget {

    private final ConnectionManager connectionManager;
    private final BundleContext bunleContext;

    public BaseWidget(ConnectionManager connectionManager, BundleContext bunleContext) {
        this.connectionManager = connectionManager;
        this.bunleContext = bunleContext;
    }

    public ConnectionManagerState currentState() {
        ConnectionManagerState state = new ConnectionManagerState(connectionManager, bunleContext);
        state.performLayout();
        return state;
    }

    public void autoconnect() {
        connectionManager.autoConnect();
    }

    public boolean connect(ConnectionInfo info) {
        if (info.getSourceEndpoint() == null || info.getTargetEndpoint() == null) {
            return false;
        }

        ManagedEndpoint sourceEndpoint = connectionManager.getEndpoint(info.getSourceEndpoint());
        ManagedEndpoint targetEndpoint = connectionManager.getEndpoint(info.getTargetEndpoint());

        if (sourceEndpoint != null && targetEndpoint != null) {
            EndpointPort sourcePort = sourceEndpoint.getPort(info.getSourcePort());
            EndpointPort targetPort = targetEndpoint.getPort(info.getTargetPort());

            if (sourcePort != null && targetPort != null) {
                PotentialConnection connection = sourcePort.getPotentialConnection(targetPort);
                if (connection != null && connection.isConnectable()) {
                    connection.connect();
                    return true;
                }
            }
        }

        return false;
    }

    public boolean disconnect(ConnectionInfo info) {
        if (info.getSourceEndpoint() == null || info.getTargetEndpoint() == null) {
            return false;
        }

        ManagedEndpoint sourceEndpoint = connectionManager.getEndpoint(info.getSourceEndpoint());
        ManagedEndpoint targetEndpoint = connectionManager.getEndpoint(info.getTargetEndpoint());

        if (sourceEndpoint != null && targetEndpoint != null) {
            EndpointPort sourcePort = sourceEndpoint.getPort(info.getSourcePort());
            EndpointPort targetPort = targetEndpoint.getPort(info.getTargetPort());

            if (sourcePort != null && targetPort != null) {
                PotentialConnection connection = sourcePort.getPotentialConnection(targetPort);
                if (connection != null && connection.isConnected()) {
                    connection.disconnect();
                    return true;
                }
            }
        }

        return false;
    }
}
