package org.flexiblepower.ral.ext;

import org.flexiblepower.ral.drivers.uncontrolled.UncontrolledControlParameters;
import org.flexiblepower.ral.drivers.uncontrolled.UncontrolledDriver;
import org.flexiblepower.ral.drivers.uncontrolled.UncontrolledState;

/**
 * This is a basic implementation of an uncontrolled resource driver. This already implements the
 * {@link #setControlParameters(UncontrolledControlParameters)} method with no action (this should be nothing to
 * control).
 * 
 * @param <RS>
 *            The resource state type
 */
public abstract class UncontrolledResourceDriver<RS extends UncontrolledState> extends
                                                                               AbstractResourceDriver<RS, UncontrolledControlParameters> implements
                                                                                                                                        UncontrolledDriver<RS> {
    @Override
    public final void setControlParameters(UncontrolledControlParameters resourceControlParameters) {
        // Nothing to control
    }
}
