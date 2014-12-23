package org.flexiblepower.efi.uncontrolled;

import java.util.Date;

import org.flexiblepower.rai.Allocation;
import org.flexiblepower.rai.values.Commodity;
import org.flexiblepower.rai.values.ConstraintProfile;
import org.flexiblepower.rai.values.ConstraintProfileMap;

/**
 * An allocation will can be sent to appliances which support curtailing production/consumption.
 */
public final class UncontrolledAllocation extends Allocation {
    /**
     * The time at which the commodity profile should start.
     */
    private final Date startTime;

    /**
     * For every applicable {@link Commodity} a profile that needs to be followed by the appliance after receiving an
     * uncontrolled allocation.
     */
    private final ConstraintProfileMap curtailmentProfiles;

    public UncontrolledAllocation(String resourceId,
                                  UncontrolledUpdate controlSpaceUpdate,
                                  Date timestamp,
                                  boolean isEmergencyAllocation,
                                  Date startTime,
                                  ConstraintProfileMap curtailmentProfiles) {
        super(timestamp, controlSpaceUpdate, isEmergencyAllocation);
        if (startTime == null) {
            throw new NullPointerException("startTime");
        } else if (curtailmentProfiles == null) {
            throw new NullPointerException("curtailmentProfiles");
        }
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
     * @return a map of {@link ConstraintProfile} for every applicable {@link Commodity}
     */
    public ConstraintProfileMap getCurtailmentProfiles() {
        return curtailmentProfiles;
    }

    @Override
    public int hashCode() {
        return 31 * (31 * super.hashCode() + startTime.hashCode()) + curtailmentProfiles.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        UncontrolledAllocation other = (UncontrolledAllocation) obj;
        return other.startTime.equals(startTime) && other.curtailmentProfiles.equals(curtailmentProfiles);
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append("startTime=").append(startTime).append(", ");
        sb.append("curtailmentProfiles=").append(curtailmentProfiles).append(", ");
    }
}
