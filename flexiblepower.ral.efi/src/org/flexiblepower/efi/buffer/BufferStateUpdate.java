package org.flexiblepower.efi.buffer;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

import javax.measure.Measurable;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

/**
 * This class contains up to date information about the state of the buffer.
 *
 * @param <Q>
 *            The quantity that describes what is stored in the buffer (e.g. temperature or electricity)
 */
public class BufferStateUpdate<Q extends Quantity> extends BufferUpdate {
    private final Measurable<Q> currentFillLevel;
    private final Set<ActuatorUpdate> currentRunningMode;

    /**
     * Constructs a new {@link BufferStateUpdate} message with the specific validFrom
     *
     * @param bufferRegistration
     *            The registration message to which this update is referring to
     * @param timestamp
     *            The moment when this constructor is called
     * @param validFrom
     *            This timestamp indicates from which moment on this update is valid.
     * @param currentFillLevel
     *            This value represents the current fill level of the buffer.
     * @param currentRunningMode
     *            This is a set of zero or more current running modes. For every actuator there will be one current
     *            running mode.
     */
    public BufferStateUpdate(BufferRegistration<Q> bufferRegistration,
                             Date timestamp,
                             Date validFrom,
                             Measurable<Q> currentFillLevel,
                             Set<ActuatorUpdate> currentRunningMode) {
        super(bufferRegistration.getResourceId(), timestamp, validFrom);
        if (currentFillLevel == null) {
            throw new NullPointerException("currentFillLevel");
        }

        this.currentFillLevel = currentFillLevel;
        this.currentRunningMode = currentRunningMode == null ? Collections.<ActuatorUpdate> emptySet()
                                                            : currentRunningMode;
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
     * @return This is a set of zero or more current running modes. For every actuator there will be one current running
     *         mode.
     */
    public Set<ActuatorUpdate> getCurrentRunningMode() {
        return currentRunningMode;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + 67 * currentFillLevel.hashCode() + currentRunningMode.hashCode();
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
        } else if (!currentRunningMode.equals(other.currentRunningMode)) {
            return false;
        }
        return true;
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append("currentFillLevel=").append(currentFillLevel).append(", ");
        sb.append("currentRunningMode=").append(currentRunningMode).append(", ");
    }
}
