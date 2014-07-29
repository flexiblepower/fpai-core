package org.flexiblepower.efi.unconstrained;

import java.util.Date;
import java.util.Set;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

import org.flexiblepower.rai.comm.ControlSpaceRegistration;
import org.flexiblepower.rai.values.Commodity;

public class UnconstrainedRegistration extends ControlSpaceRegistration {

    private final Set<Commodity> supportedCommodities;

    public UnconstrainedRegistration(String resourceId,
                                     Date timestamp,
                                     Measurable<Duration> allocationDelay,
                                     Set<Commodity> supportedCommodities) {
        super(resourceId, timestamp, allocationDelay);
        this.supportedCommodities = supportedCommodities;
    }

    public Set<Commodity> getSupportedCommodities() {
        return supportedCommodities;
    }

}
