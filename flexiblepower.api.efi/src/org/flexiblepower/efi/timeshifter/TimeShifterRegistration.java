package org.flexiblepower.efi.timeshifter;

import java.util.Date;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

import org.flexiblepower.rai.ControlSpaceRegistration;
import org.flexiblepower.rai.values.Commodity;
import org.flexiblepower.rai.values.CommoditySet;
import org.flexiblepower.time.TimeService;

/**
 * The {@link TimeShifterRegistration} contains information about the commodities supported by the appliance.
 */
public class TimeShifterRegistration extends ControlSpaceRegistration {
    private final CommoditySet supportedCommodities;

    /**
     * @param resourceId
     *            The resource identifier
     * @param timestamp
     *            The moment when this constructor is called (should be {@link TimeService#getTime()})
     * @param allocationDelay
     *            The duration of the delay in communications channel from the moment of sending to the moment the
     *            command is executed up by the device.
     * @param supportedCommodities
     *            The set of all commodities that can be produced or consumed by the appliance.
     */
    public TimeShifterRegistration(String resourceId,
                                   Date timestamp,
                                   Measurable<Duration> allocationDelay,
                                   CommoditySet supportedCommodities) {
        super(resourceId, timestamp, allocationDelay);
        if (supportedCommodities == null) {
            throw new NullPointerException("supportedCommodities");
        }

        this.supportedCommodities = supportedCommodities;
    }

    /**
     * @return The set of all commodities that can be produced or consumed by the appliance.
     */
    public CommoditySet getSupportedCommodities() {
        return supportedCommodities;
    }

    /**
     * @return <code>true</code> if the commodity is supported
     */
    public boolean supportsCommodity(Commodity<?, ?> commodity) {
        return supportedCommodities.contains(commodity);
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + supportedCommodities.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        TimeShifterRegistration other = (TimeShifterRegistration) obj;
        return supportedCommodities.equals(other.supportedCommodities);
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append("supportedCommodities=").append(supportedCommodities).append(", ");
    }
}
