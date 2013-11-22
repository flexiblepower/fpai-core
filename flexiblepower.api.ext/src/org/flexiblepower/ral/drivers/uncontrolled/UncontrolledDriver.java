package org.flexiblepower.ral.drivers.uncontrolled;

import org.flexiblepower.ral.ResourceDriver;

/**
 * To represent any uncontrolled resource, using the {@link UncontrolledState} and {@link UncontrolledControlParameters}
 * types.
 * 
 * @param S
 *            the type of UncontrolledState
 */
public interface UncontrolledDriver<S extends UncontrolledState> extends
                                                                 ResourceDriver<S, UncontrolledControlParameters> {
}
