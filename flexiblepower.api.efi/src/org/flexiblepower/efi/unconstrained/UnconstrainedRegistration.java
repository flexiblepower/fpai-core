package org.flexiblepower.efi.unconstrained;

import java.util.Date;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

import org.flexiblepower.rai.ControlSpaceRegistration;
import org.flexiblepower.rai.values.CommoditySet;

public class UnconstrainedRegistration extends ControlSpaceRegistration {

    private final CommoditySet supportedCommodities;

    public UnconstrainedRegistration(String resourceId,
                                     Date timestamp,
                                     Measurable<Duration> allocationDelay,
                                     CommoditySet supportedCommodities) {
        super(resourceId, timestamp, allocationDelay);
        this.supportedCommodities = supportedCommodities;
    }

    public CommoditySet getSupportedCommodities() {
        return supportedCommodities;
    }

}
