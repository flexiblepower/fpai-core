package org.flexiblepower.efi.buffer;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

import javax.measure.Measurable;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

import org.flexiblepower.time.TimeService;

/**
 * This class contains up to date information about the state of the buffer.
 *
 * @param <Q>
 *            The quantity that describes what is stored in the buffer (e.g. temperature or electricity)
 */
public class BufferStateUpdate<Q extends Quantity> extends BufferUpdate {
    private final Measurable<Q> currentFillLevel;
    private final Set<ActuatorUpdate> actuatorUpdates;

    /**
     * Constructs a new {@link BufferStateUpdate} message with the specific validFrom
     *
     * @param resourceId
     *            The resource identifier
     * @param timestamp
     *            The moment when this constructor is called (should be {@link TimeService#getTime()}
     * @param validFrom
     *            This timestamp indicates from which moment on this update is valid.
     * @param currentFillLevel
     *            This value represents the current fill level of the buffer.
     * @param actuatorUpdates
     *            This is a set of zero or more actuator updates. For every actuator there will be one current running
     *            mode and timer update information.
     */
    public BufferStateUpdate(BufferRegistration<Q> bufferRegistration,
                             Date timestamp,
                             Date validFrom,
                             Measurable<Q> currentFillLevel,
                             Set<ActuatorUpdate> actuatorUpdates) {
        super(bufferRegistration.getResourceId(), timestamp, validFrom);
        if (currentFillLevel == null) {
            throw new NullPointerException("currentFillLevel");
        }

        this.currentFillLevel = currentFillLevel;
        this.actuatorUpdates = actuatorUpdates == null ? Collections.<ActuatorUpdate> emptySet()
                                                      : actuatorUpdates;
    }

    /**
     * @return This value represents the current fill level of the buffer.
     */
    public Measurable<Q> getCurrentFillLevel() {
        return currentFillLevel;
    }

    /**
     * @param unit
     *            The unit in which we want the current fill level to be expressed
     * @return This value represents the current fill level of the buffer
     */
    public double getCurrentFillLevelAsDouble(Unit<Q> unit) {
        return currentFillLevel.doubleValue(unit);
    }

    /**
     * This gives the actuator updates that are in this system update message. TODO: Change this name to
     * getActuatorUpdates because it is not only RunningMode, but also TimerUpdate information that is in here.
     *
     * @return This is a set of zero or more actuator updates (both running mode and timer info). For every actuator
     *         there will be one current running mode and timer updates for those timers that have changed.
     */
    public Set<ActuatorUpdate> getCurrentRunningMode() {
        return actuatorUpdates;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + 67 * currentFillLevel.hashCode() + actuatorUpdates.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        }

        @SuppressWarnings("rawtypes")
        BufferStateUpdate other = (BufferStateUpdate) obj;
        if (!currentFillLevel.equals(other.currentFillLevel)) {
            return false;
        } else if (!actuatorUpdates.equals(other.actuatorUpdates)) {
            return false;
        }
        return true;
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append("currentFillLevel=").append(currentFillLevel).append(", ");
        sb.append("currentRunningMode=").append(actuatorUpdates).append(", ");
    }
}
