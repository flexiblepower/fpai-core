package org.flexiblepower.efi.uncontrolled;

import java.util.Date;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Quantity;

import org.flexiblepower.rai.comm.ControlSpaceRegistration;
import org.flexiblepower.rai.values.Commodity;
import org.flexiblepower.rai.values.ConstraintList;

/**
 * The UncontrolledRegistration object registers the uncontrolled resource manager to the energy app, the message is
 * describes the commodities that are consumed or produced by the uncontrolled appliance with the Commodity attribute.
 * Furthermore if the modelled appliance has features that allow curtailing the consumption or production of the device,
 * the curtail options can be expressed in a ConstraintList for every commodity.
 *
 * @author TNO
 *
 */
public final class UncontrolledRegistration extends ControlSpaceRegistration {

    private static final long serialVersionUID = 5264443341456636488L;

    /**
     * A map of every applicable Commodity for the appliance as key and a ConstriantList representing the list of
     * possible curtail steps as an value. The ConstraintList in the map is optional and will only be provided if the
     * appliance support curtailing, otherwise it must be null.
     */
    private final ConstraintList.Map supportedCommodityCurtailments;

    public UncontrolledRegistration(String resourceId,
                                    Date timestamp,
                                    Measurable<Duration> allocationDelay,
                                    ConstraintList.Map supportedCommodityCurtailments) {
        super(resourceId, timestamp, allocationDelay);
        this.supportedCommodityCurtailments = supportedCommodityCurtailments;
    }

    public <FQ extends Quantity> ConstraintList<FQ> getCurtailment(Commodity<?, FQ> commodity) {
        return supportedCommodityCurtailments.get(commodity);
    }

    public Commodity.Set getSupportedCommodities() {
        return supportedCommodityCurtailments.keySet();
    }

    public boolean supportsCommodity(Commodity<?, ?> commodity) {
        return supportedCommodityCurtailments.containsKey(commodity);
    }
}
