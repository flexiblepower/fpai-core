package org.flexiblepower.runtime.messaging;

import java.io.IOException;

import org.flexiblepower.messaging.ConnectionManager.EndpointPort;
import org.flexiblepower.messaging.ConnectionManager.MatchingPorts;

final class MatchingPortsImpl implements MatchingPorts {
    private final AbstractEndpointPort left, right;

    public MatchingPortsImpl(AbstractEndpointPort left, AbstractEndpointPort right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public AbstractEndpointPort getEitherEnd() {
        return left;
    }

    @Override
    public AbstractEndpointPort getOtherEnd(EndpointPort either) {
        return either == left ? right : left;
    }

    @Override
    public void connect() throws IOException {
        left.connectTo(this);
        right.connectTo(this);
    }

    @Override
    public void disconnect() {
        left.disconnect();
        right.disconnect();
    }

    @Override
    public boolean isConnected() {
        return left.isConnected() && right.isConnected();
    }
}
