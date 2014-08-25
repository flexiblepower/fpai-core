package org.flexiblepower.efi.unconstrained;

import java.util.Date;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

import org.flexiblepower.rai.comm.ControlSpaceRegistration;
import org.flexiblepower.rai.values.Commodity;

public class UnconstrainedRegistration extends ControlSpaceRegistration {

    private final Commodity.Set supportedCommodities;

    public UnconstrainedRegistration(String resourceId,
                                     Date timestamp,
                                     Measurable<Duration> allocationDelay,
                                     Commodity.Set supportedCommodities) {
        super(resourceId, timestamp, allocationDelay);
        this.supportedCommodities = supportedCommodities;
    }

    public Commodity.Set getSupportedCommodities() {
        return supportedCommodities;
    }

}
