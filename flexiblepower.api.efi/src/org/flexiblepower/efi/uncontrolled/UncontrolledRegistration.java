package org.flexiblepower.efi.uncontrolled;

import java.util.Date;
import java.util.Map;

import org.flexiblepower.rai.comm.ControlSpaceRegistration;
import org.flexiblepower.rai.values.Commodity;
import org.flexiblepower.rai.values.ConstraintList;

public class UncontrolledRegistration extends ControlSpaceRegistration {

    private final Map<Commodity, ConstraintList> supportedCommodityCurtailments;

    public UncontrolledRegistration(String resourceId,
                                    Date timestamp,
                                    Map<Commodity, ConstraintList> supportedCommodityCurtailments) {
        super(resourceId, timestamp);
        this.supportedCommodityCurtailments = supportedCommodityCurtailments;
    }

    public Map<Commodity, ConstraintList> getSupportedCommodities() {
        return supportedCommodityCurtailments;
    }

}
