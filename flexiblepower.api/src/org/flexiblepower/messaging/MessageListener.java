package org.flexiblepower.messaging;

import org.flexiblepower.messaging.ConnectionManager.EndpointPort;

public interface MessageListener {
    void handleMessage(EndpointPort from, EndpointPort to, Object message);
}
