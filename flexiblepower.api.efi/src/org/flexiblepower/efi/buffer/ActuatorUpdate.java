package org.flexiblepower.efi.buffer;

import java.util.Collections;
import java.util.Set;

import org.flexiblepower.efi.util.TimerUpdate;

/**
 * This class contains up to date information about the state of an actuator.
 */
public class ActuatorUpdate {
    private final int actuatorId;
    private final int currentRunningModeId;
    private final Set<TimerUpdate> timerUpdates;

    /**
     * @param actuatorId
     *            This id refers uniquely to an actuator in this buffer appliance.
     * @param currentRunningModeId
     *            This id refers uniquely to the current running mode of the actuator that is referred to in this
     *            update.
     * @param timerUpdates
     *            A set of zero or more TimerUpdate objects.
     */
    public ActuatorUpdate(int actuatorId, int currentRunningModeId, Set<TimerUpdate> timerUpdates) {
        this.actuatorId = actuatorId;
        this.currentRunningModeId = currentRunningModeId;
        this.timerUpdates = timerUpdates == null ? Collections.<TimerUpdate> emptySet() : timerUpdates;
    }

    /**
     * @return This id refers uniquely to an actuator in this buffer appliance.
     */
    public int getActuatorId() {
        return actuatorId;
    }

    /**
     * @return This id refers uniquely to the current running mode of the actuator that is referred to in this update.
     */
    public int getCurrentRunningModeId() {
        return currentRunningModeId;
    }

    /**
     * @return A set of zero or more TimerUpdate objects.
     */
    public Set<TimerUpdate> getTimerUpdates() {
        return timerUpdates;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + actuatorId;
        result = prime * result + currentRunningModeId;
        result = prime * result + timerUpdates.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        ActuatorUpdate other = (ActuatorUpdate) obj;
        if (actuatorId != other.actuatorId) {
            return false;
        } else if (currentRunningModeId != other.currentRunningModeId) {
            return false;
        } else if (!timerUpdates.equals(other.timerUpdates)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ActuatorUpdate [actuatorId=" + actuatorId
               + ", currentRunningModeId="
               + currentRunningModeId
               + ", timerUpdates="
               + timerUpdates
               + "]";
    }
}
