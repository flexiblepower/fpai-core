package org.flexiblepower.efi.unconstrained;

import java.util.Date;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

import org.flexiblepower.rai.ControlSpaceRegistration;
import org.flexiblepower.rai.values.CommoditySet;

/**
 * This class is derived from ControlSpaceRegistration and contains the registration items that are unique to a buffer.
 */
public class UnconstrainedRegistration extends ControlSpaceRegistration {
    private final CommoditySet supportedCommodities;

    public UnconstrainedRegistration(String resourceId,
                                     Date timestamp,
                                     Measurable<Duration> allocationDelay,
                                     CommoditySet supportedCommodities) {
        super(resourceId, timestamp, allocationDelay);
        if (supportedCommodities == null) {
            throw new NullPointerException("supportedCommodities");
        } else if (supportedCommodities.isEmpty()) {
            throw new IllegalArgumentException("supportedCommodities is empty");
        }
        this.supportedCommodities = supportedCommodities;
    }

    /**
     * @return The set of all commodities that can be produced or consumed by this appliance
     */
    public CommoditySet getSupportedCommodities() {
        return supportedCommodities;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + supportedCommodities.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        UnconstrainedRegistration other = (UnconstrainedRegistration) obj;
        return other.supportedCommodities.equals(supportedCommodities);
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append("supportedCommodities=").append(supportedCommodities).append(", ");
    }
}
