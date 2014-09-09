package org.flexiblepower.efi.buffer;

import java.util.Date;
import java.util.Set;

import org.flexiblepower.rai.Allocation;
import org.flexiblepower.rai.ControlSpaceUpdate;

public class BufferAllocation extends Allocation {

    private static final long serialVersionUID = -1885176370564725847L;

    public static class ActuatorAllocation {
        private final int actuatorId;
        private final int runningModeId;
        private final Date startTime;

        public ActuatorAllocation(int actuatorId, int runningModeId, Date startTime) {
            super();
            this.actuatorId = actuatorId;
            this.runningModeId = runningModeId;
            this.startTime = startTime;
        }

        public int getActuatorId() {
            return actuatorId;
        }

        public int getRunningModeId() {
            return runningModeId;
        }

        public Date getStartTime() {
            return startTime;
        }

    }

    private final Set<ActuatorAllocation> actuatorAllocations;

    /**
     * A buffer allocation contains allocations for the actuators that it wishes to change. The unmentioned actuators of
     * the buffer may do as they please.
     * */
    public BufferAllocation(String resourceId,
                            ControlSpaceUpdate resourceUpdate,
                            Date timestamp,
                            boolean isEmergencyAllocation,
                            Set<ActuatorAllocation> actuatorAllocations) {
        super(resourceId, resourceUpdate, timestamp, isEmergencyAllocation);

        this.actuatorAllocations = actuatorAllocations;

        validate();
    }

    /**
     * Checks the internal consistency of this message. It does not check whether the actuator ids are really part of
     * the buffer with this resource id.
     */
    private void validate() {
        if (actuatorAllocations == null) {
            throw new NullPointerException("Field runningModeSelectors cannot be null.");
        }
        if (actuatorAllocations.isEmpty()) {
            throw new IllegalArgumentException("There must be at least one actuator allocation in this buffer allocation.");
        }
    }

}
