package org.flexiblepower.ral;

import org.flexiblepower.observation.ObservationProvider;

/**
 * This {@link ResourceDriver} is responsible for translating the raw data as received from the {@link ProtocolDriver}
 * into functions that can be used by the {@link ResourceManager}. When implementing a {@link ResourceDriver}, there
 * should always be a specific interface used that extends this one. For example, you would first define a
 * RefrigeratorDriver interface that describes the state of the Refrigerator and has a method to enable or disable the
 * superCool functionality.
 */
public interface ResourceDriver<RS extends ResourceState, RCP> extends ObservationProvider<RS> {
    /**
     * @param resourceControlParameters
     *            The control parameters that need to be applied to the managed resource.
     */
    void setControlParameters(RCP resourceControlParameters);
}
