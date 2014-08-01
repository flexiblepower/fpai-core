package org.flexiblepower.runtime.messaging;

import org.flexiblepower.messaging.Port;

public class MultipleEndpointPort extends AbstractEndpointPort {

    public MultipleEndpointPort(EndpointWrapper endpointWrapper, Port port) {
        super(endpointWrapper, port);
    }

    @Override
    protected synchronized void connectTo(MatchingPortsImpl matchingPort) {
        if (!getMatchingPorts().contains(matchingPort)) {
            throw new IllegalArgumentException("The given MatchingPort is not of this EndpointPort");
        }

        // TODO: implementation
    }

    @Override
    protected void disconnect() {
        // TODO Auto-generated method stub

    }

    @Override
    protected boolean isConnected() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected void addMessage(Object message) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void handleMessage() {
        // TODO Auto-generated method stub

    }

}
