package org.flexiblepower.runtime.messaging;

import org.flexiblepower.messaging.ConnectionManager.EndpointPort;
import org.flexiblepower.messaging.ConnectionManager.MatchingPorts;

final class PortMatchImpl implements MatchingPorts {
    private final EndpointPortImpl left, right;

    public PortMatchImpl(EndpointPortImpl left, EndpointPortImpl right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public EndpointPortImpl getEitherEnd() {
        return left;
    }

    @Override
    public EndpointPortImpl getOtherEnd(EndpointPort either) {
        return either == left ? right : left;
    }

    @Override
    public void connect() {
        // TODO Auto-generated method stub
    }

    @Override
    public void disconnect() {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean isConnected() {
        // TODO Auto-generated method stub
        return false;
    }
}