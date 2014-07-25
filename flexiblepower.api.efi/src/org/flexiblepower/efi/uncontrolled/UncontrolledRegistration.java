package org.flexiblepower.efi.uncontrolled;

import java.util.Date;
import java.util.Set;

import org.flexiblepower.rai.comm.ControlSpaceRegistration;
import org.flexiblepower.rai.values.Commodity;

public class UncontrolledRegistration extends ControlSpaceRegistration {

    private final Set<Commodity> supportedCommodities;

    private final Set<CurtailmentInfo> curtailmentInfoSet;

    public UncontrolledRegistration(String resourceId,
                                    Date timestamp,
                                    Set<Commodity> supportedCommodities,
                                    Set<CurtailmentInfo> curtailmentInfoSet) {
        super(resourceId, timestamp);
        this.supportedCommodities = supportedCommodities;
        this.curtailmentInfoSet = curtailmentInfoSet;
    }

    public Set<Commodity> getSupportedCommodities() {
        return supportedCommodities;
    }

}
