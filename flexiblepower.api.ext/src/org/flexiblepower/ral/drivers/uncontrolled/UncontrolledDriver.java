package org.flexiblepower.ral.drivers.uncontrolled;

import org.flexiblepower.ral.ResourceDriver;

/**
 * To represent any uncontrolled resource, using the {@link UncontrolledState} and {@link UncontrolledControlParameters}
 * types.
 * 
 * @param <RS>
 *            the type of UncontrolledState
 */
public interface UncontrolledDriver<RS extends UncontrolledState> extends
                                                                  ResourceDriver<RS, UncontrolledControlParameters> {
}
