package org.flexiblepower.efi.uncontrolled;

import java.util.Date;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Quantity;

import org.flexiblepower.ral.messages.ControlSpaceRegistration;
import org.flexiblepower.ral.values.Commodity;
import org.flexiblepower.ral.values.CommoditySet;
import org.flexiblepower.ral.values.ConstraintList;
import org.flexiblepower.ral.values.ConstraintListMap;

/**
 * The UncontrolledRegistration object registers the uncontrolled resource manager to the energy app, the message is
 * describes the commodities that are consumed or produced by the uncontrolled appliance with the Commodity attribute.
 * Furthermore if the modelled appliance has features that allow curtailing the consumption or production of the device,
 * the curtail options can be expressed in a ConstraintList for every commodity.
 */
public final class UncontrolledRegistration extends ControlSpaceRegistration {

    /**
     * A map of every applicable Commodity for the appliance.
     */
    private final CommoditySet supportedCommodities;

    /**
     * A map of every applicable Commodity for the appliance as key and a ConstriantList representing the list of
     * possible curtail steps as an value. The Commodity must be supported by the appliance in order to support
     * curtailment.
     */
    private final ConstraintListMap supportedCommodityCurtailments;

    /**
     * @param resourceId
     *            The resource identifier
     * @param timestamp
     *            The moment when this constructor is called)
     * @param allocationDelay
     *            The duration of the delay in communications channel from the moment of sending to the moment the
     *            command is executed up by the device.
     * @param supportedCommodityCurtailments
     *            A map of every applicable Commodity for the appliance as key and a ConstriantList representing the
     *            list of possible curtail steps as an value. The value of the map is optional and will only be provided
     *            if the appliance support curtailing, otherwise it must be null.
     */
    public UncontrolledRegistration(String resourceId,
                                    Date timestamp,
                                    Measurable<Duration> allocationDelay,
                                    CommoditySet supportedCommodities,
                                    ConstraintListMap supportedCommodityCurtailments) {
        super(resourceId, timestamp, allocationDelay);
        this.supportedCommodities = supportedCommodities == null ? CommoditySet.empty : supportedCommodities;
        this.supportedCommodityCurtailments = supportedCommodityCurtailments == null ? ConstraintListMap.EMPTY
                                                                                    : supportedCommodityCurtailments;
        for (Commodity<?, ?> commodity : supportedCommodityCurtailments.keySet()) {
            if (!supportedCommodities.contains(commodity)) {
                throw new IllegalArgumentException("Only supported commodities can support curtailment");
            }
        }
    }

    /**
     * @param commodity
     *            The commodity for which the {@link ConstraintList} will be returned
     * @return The curtailment for the specific commodity or <code>null</code> if not available
     */
    public <FQ extends Quantity> ConstraintList<FQ> getCurtailment(Commodity<?, FQ> commodity) {
        return supportedCommodityCurtailments.get(commodity);
    }

    /**
     * @return The commodities that are supported by the device
     */
    public CommoditySet getSupportedCommodities() {
        return supportedCommodities;
    }

    /**
     * @return <code>true</code> if the given commodity is supported
     */
    public boolean supportsCommodity(Commodity<?, ?> commodity) {
        return supportedCommodities.contains(commodity);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + supportedCommodityCurtailments.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        UncontrolledRegistration other = (UncontrolledRegistration) obj;
        return other.supportedCommodityCurtailments.equals(supportedCommodityCurtailments);
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append("supportedCommodityCurtailments=").append(supportedCommodityCurtailments).append(", ");
    }
}
