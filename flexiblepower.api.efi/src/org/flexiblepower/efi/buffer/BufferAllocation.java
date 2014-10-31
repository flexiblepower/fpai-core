package org.flexiblepower.efi.buffer;

import java.util.Date;
import java.util.Set;

import org.flexiblepower.rai.Allocation;
import org.flexiblepower.rai.ControlSpaceUpdate;

/**
 * This class is derived from {@link Allocation} and contains specific allocation information for a buffer appliance.
 */
public class BufferAllocation extends Allocation {
    private final Set<ActuatorAllocation> actuatorAllocations;

    /**
     * A buffer allocation contains allocations for the actuators that it wishes to change. The unmentioned actuators of
     * the buffer may do as they please.
     * */
    public BufferAllocation(ControlSpaceUpdate resourceUpdate,
                            Date timestamp,
                            boolean isEmergencyAllocation,
                            Set<ActuatorAllocation> actuatorAllocations) {
        super(timestamp, resourceUpdate, isEmergencyAllocation);
        if (actuatorAllocations == null) {
            throw new NullPointerException("actuatorAllocations");
        } else if (actuatorAllocations.isEmpty()) {
            throw new IllegalArgumentException("There must be at least one actuator allocation in this buffer allocation.");
        }

        this.actuatorAllocations = actuatorAllocations;
    }

    /**
     * @return A set of zero or more {@link ActuatorAllocation} objects.
     */
    public Set<ActuatorAllocation> getActuatorAllocations() {
        return actuatorAllocations;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + actuatorAllocations.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        BufferAllocation other = (BufferAllocation) obj;
        return actuatorAllocations.equals(other.actuatorAllocations);
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append("actuatorAllocations=").append(actuatorAllocations).append(", ");
    }
}
