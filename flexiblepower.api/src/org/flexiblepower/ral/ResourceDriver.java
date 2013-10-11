package org.flexiblepower.ral;

import org.flexiblepower.observation.ObservationProvider;

/**
 * This {@link ResourceDriver} is responsible for translating the raw data as received from the ProtocolDriver into
 * functions that can be used by the {@link ResourceManager}. When implementing a {@link ResourceDriver}, there should
 * always be a specific interface used that extends this one. For example, you would first define a RefrigeratorDriver
 * interface that describes the state of the Refrigerator and has a method to enable or disable the superCool
 * functionality.
 * 
 * @param <RS>
 *            The type of {@link ResourceState} that will be published through the observation framework.
 * @param <RCP>
 *            The type of {@link ResourceControlParameters} that can be understood by this driver. See the
 *            {@link #setControlParameters(ResourceControlParameters)} method.
 */
public interface ResourceDriver<RS extends ResourceState, RCP extends ResourceControlParameters> extends
                                                                                                 ObservationProvider<RS> {
    /**
     * @param resourceControlParameters
     *            The control parameters that need to be applied to the managed resource.
     */
    void setControlParameters(RCP resourceControlParameters);
}
