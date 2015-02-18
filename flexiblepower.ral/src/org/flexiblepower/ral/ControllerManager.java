package org.flexiblepower.ral;

import org.flexiblepower.messaging.Endpoint;
import org.flexiblepower.ral.messages.ResourceMessage;

/**
 * The {@link ControllerManager} is the main interface of an Energy Application. This should be implemented as an
 * {@link Endpoint} with one or more ports to which {@link ResourceManager}s can be connected. Each implementation of
 * the Resource Abstraction Interfaces (RAI) should define which ports are supported (for example see the
 * {@link org.flexiblepower.efi.EfiControllerManager}) and which messages are supported over these ports. These messages
 * should always be a extension of the {@link ResourceMessage}.
 */
public interface ControllerManager extends Endpoint {
}
