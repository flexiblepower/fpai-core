package org.flexiblepower.efi.uncontrolled;

import java.util.Date;

import org.flexiblepower.rai.Allocation;
import org.flexiblepower.rai.ControlSpaceUpdate;
import org.flexiblepower.rai.values.Commodity;

/**
 * An allocation will can be sent to appliances which support curtailing production/consumption.
 *
 * @author TNO
 *
 */
public final class UncontrolledAllocation extends Allocation {

    private static final long serialVersionUID = -6113496967677840815L;

    /**
     * The time at which the commodity profile should start.
     */
    private final Date startTime;

    /**
     * For every applicable {@link Commodity} a profile that needs to be followed by the appliance after receiving an
     * uncontrolled allocation.
     */
    private final CurtailmentProfile.Map curtailmentProfiles;

    public UncontrolledAllocation(String resourceId,
                                  ControlSpaceUpdate controlSpaceUpdate,
                                  Date timestamp,
                                  boolean isEmergencyAllocation,
                                  Date startTime,
                                  CurtailmentProfile.Map curtailmentProfiles) {
        super(resourceId, controlSpaceUpdate, timestamp, isEmergencyAllocation);
        this.startTime = startTime;
        this.curtailmentProfiles = curtailmentProfiles;
    }

    /**
     *
     * @return the desired start time of the curtailmentProfiles
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     *
     * @return a map of {@link CurtialmentProfile} for every applicable {@link Commodity}
     */
    public CurtailmentProfile.Map getCurtailmentProfiles() {
        return curtailmentProfiles;
    }

}
