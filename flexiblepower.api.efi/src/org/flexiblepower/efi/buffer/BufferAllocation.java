package org.flexiblepower.efi.buffer;

import java.util.Date;
import java.util.Set;

import org.flexiblepower.rai.comm.Allocation;
import org.flexiblepower.rai.comm.ControlSpaceUpdate;

public class BufferAllocation extends Allocation {

    public static class ActuatorAllocation {
        private int actuatorId;
        private int runningModeId;
        private Date startTime;
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
