package org.flexiblepower.efi.uncontrolled;

import java.util.Date;

import org.flexiblepower.rai.Allocation;
import org.flexiblepower.rai.ControlSpaceUpdate;
import org.flexiblepower.rai.values.Commodity;
import org.flexiblepower.rai.values.ConstraintProfileMap;

/**
 * An allocation will can be sent to appliances which support curtailing production/consumption.
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
    private final ConstraintProfileMap curtailmentProfiles;

    public UncontrolledAllocation(String resourceId,
                                  ControlSpaceUpdate controlSpaceUpdate,
                                  Date timestamp,
                                  boolean isEmergencyAllocation,
                                  Date startTime,
                                  ConstraintProfileMap curtailmentProfiles) {
        super(timestamp, controlSpaceUpdate, isEmergencyAllocation);
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
    public ConstraintProfileMap getCurtailmentProfiles() {
        return curtailmentProfiles;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        UncontrolledAllocation other = (UncontrolledAllocation) obj;
        if (startTime == null) {
            if (other.startTime != null) {
                return false;
            }
        } else if (!startTime.equals(other.startTime)) {
            return false;
        }
        return true;
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append("startTime=").append(startTime).append(", ");
        sb.append("curtailmentProfiles=").append(curtailmentProfiles).append(", ");
    }
}
