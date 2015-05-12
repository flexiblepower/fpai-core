package org.flexiblepower.ral;

import org.flexiblepower.messaging.Endpoint;
import org.flexiblepower.messaging.Port;
import org.flexiblepower.ral.messages.Allocation;
import org.flexiblepower.ral.messages.ResourceMessage;

/**
 * The {@link ResourceManager} is responsible to translating the current state of an appliance (as received from a
 * {@link ResourceDriver}) to {@link ResourceMessage}s to translate the {@link Allocation}s to actions that are
 * performed on the {@link ResourceDriver}. The {@link ResourceDriver} is dynamically connected to the
 * {@link ResourceManager} in OSGi, using the messaging framework in FPAI. For this it is expected for each
 * {@link ResourceManager} to define 2 ports: 1 for the link to the controller and 1 for the link to the driver. It is
 * also possible for the {@link ResourceManager} to be connected to several drivers (e.g. for a heatbuffer and an
 * actuator when they are connected separately).
 *
 * Any {@link ResourceManager} is an {@link Endpoint} and should have (at least) 1 port, called "controller" to connect
 * to a {@link ControllerManager}.
 */
@Port(name = "controller")
public interface ResourceManager extends Endpoint {
}
