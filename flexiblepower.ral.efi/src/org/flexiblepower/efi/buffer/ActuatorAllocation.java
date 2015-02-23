package org.flexiblepower.efi.buffer;

import java.util.Date;

/**
 * This class contains allocation for a specific actuator at a specific time.
 */
public class ActuatorAllocation {
    private final int actuatorId;
    private final int runningModeId;
    private final Date startTime;

    /**
     * @param actuatorId
     *            An id that uniquely refers to an actuator within the buffer appliance.
     * @param runningModeId
     *            An id that uniquely refers to a running mode for this actuator. The actuator has to start this running
     *            mode.
     * @param startTime
     *            The start time for the running mode that is referred to in this actuator allocation.
     */
    public ActuatorAllocation(int actuatorId, int runningModeId, Date startTime) {
        if (startTime == null) {
            throw new NullPointerException("startTime");
        }

        this.actuatorId = actuatorId;
        this.runningModeId = runningModeId;
        this.startTime = startTime;
    }

    /**
     * @return An id that uniquely refers to an actuator within the buffer appliance.
     */
    public int getActuatorId() {
        return actuatorId;
    }

    /**
     * @return An id that uniquely refers to a running mode for this actuator. The actuator has to start this running
     *         mode.
     */
    public int getRunningModeId() {
        return runningModeId;
    }

    /**
     * @return The start time for the running mode that is referred to in this actuator allocation.
     */
    public Date getStartTime() {
        return startTime;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + actuatorId;
        result = prime * result + runningModeId;
        result = prime * result + startTime.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        ActuatorAllocation other = (ActuatorAllocation) obj;
        if (actuatorId != other.actuatorId) {
            return false;
        } else if (runningModeId != other.runningModeId) {
            return false;
        } else if (!startTime.equals(other.startTime)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ActuatorAllocation [actuatorId=" + actuatorId
               + ", runningModeId="
               + runningModeId
               + ", startTime="
               + startTime
               + "]";
    }
}
