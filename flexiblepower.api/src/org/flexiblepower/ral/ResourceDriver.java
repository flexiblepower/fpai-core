package org.flexiblepower.ral;

import org.flexiblepower.messaging.Endpoint;
import org.flexiblepower.messaging.Port;

/**
 * This {@link ResourceDriver} is responsible for translating the raw data as received from the ProtocolDriver into
 * information that can be used by the {@link ResourceManager}. When implementing a {@link ResourceDriver}, there should
 * always be a specific interface used that extends this one. For example, you would first define a RefrigeratorDriver
 * interface that describes the state of the Refrigerator and has a method to enable or disable the superCool
 * functionality.
 *
 * The implementation of the {@link ResourceDriver} should be an {@link Endpoint} with a single port with the name
 * "manager". The specific port definition should be on the specific driver specification.
 */
@Port(name = "manager")
public interface ResourceDriver extends Endpoint {
}
