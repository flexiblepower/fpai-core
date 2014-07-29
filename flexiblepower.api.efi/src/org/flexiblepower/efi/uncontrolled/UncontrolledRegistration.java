package org.flexiblepower.efi.uncontrolled;

import java.util.Date;
import java.util.Map;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

import org.flexiblepower.rai.comm.ControlSpaceRegistration;
import org.flexiblepower.rai.values.Commodity;
import org.flexiblepower.rai.values.ConstraintList;

public class UncontrolledRegistration extends ControlSpaceRegistration {

    private final Map<Commodity, ConstraintList> supportedCommodityCurtailments;

    public UncontrolledRegistration(String resourceId,
                                    Date timestamp,
                                    Measurable<Duration> allocationDelay,
                                    Map<Commodity, ConstraintList> supportedCommodityCurtailments) {
        super(resourceId, timestamp, allocationDelay);
        this.supportedCommodityCurtailments = supportedCommodityCurtailments;
    }

    public Map<Commodity, ConstraintList> getSupportedCommodities() {
        return supportedCommodityCurtailments;
    }

    public boolean supportsCommodity(Commodity commodity) {
        return supportedCommodityCurtailments.containsKey(commodity);
    }

}
