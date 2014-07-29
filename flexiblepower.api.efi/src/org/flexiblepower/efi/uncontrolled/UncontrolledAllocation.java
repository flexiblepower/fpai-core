package org.flexiblepower.efi.uncontrolled;

import java.util.Date;
import java.util.Map;

import org.flexiblepower.rai.comm.Allocation;
import org.flexiblepower.rai.comm.ControlSpaceUpdate;
import org.flexiblepower.rai.values.Commodity;

public class UncontrolledAllocation extends Allocation {

    private static final long serialVersionUID = -6113496967677840815L;

    private final Date startTime;
    private final Map<Commodity, CurtailmentProfile> curtailmentProfiles;

    public UncontrolledAllocation(String resourceId,
                                  ControlSpaceUpdate controlSpaceUpdate,
                                  Date timestamp,
                                  boolean isEmergencyAllocation,
                                  Date startTime,
                                  Map<Commodity, CurtailmentProfile> curtailmentProfiles) {
        super(resourceId, controlSpaceUpdate, timestamp, isEmergencyAllocation);
        this.startTime = startTime;
        this.curtailmentProfiles = curtailmentProfiles;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Map<Commodity, CurtailmentProfile> getCurtailmentProfiles() {
        return curtailmentProfiles;
    }

}
