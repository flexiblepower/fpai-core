package org.flexiblepower.efi.unconstrained;

import java.util.Date;
import java.util.Set;

import org.flexiblepower.rai.comm.ControlSpaceRegistration;
import org.flexiblepower.rai.values.Commodity;

public class UnconstrainedRegistration extends ControlSpaceRegistration {

    private final Set<Commodity> supportedCommodities;

    public UnconstrainedRegistration(String resourceId, Date timestamp, Set<Commodity> supportedCommodities) {
        super(resourceId, timestamp);
        this.supportedCommodities = supportedCommodities;
    }

    public Set<Commodity> getSupportedCommodities() {
        return supportedCommodities;
    }

}
