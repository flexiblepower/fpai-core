package org.flexiblepower.efi.unconstrained;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.flexiblepower.efi.util.TimerUpdate;

/**
 * This class contains up to date information about the state of the buffer.
 */
public class UnconstrainedStateUpdate extends UnconstrainedUpdate {
    private final int currentRunningModeId;
    private final Set<TimerUpdate> timerUpdates;

    /**
     * Constructs a new {@link UnconstrainedUpdate} message with the specific validFrom
     *
     * @param resourceId
     *            The resource identifier
     * @param timestamp
     *            The moment when this constructor is called (should be {@link TimeService#getTime()}
     * @param validFrom
     *            This timestamp indicates from which moment on this update is valid.
     * @param currentRunningModeId
     *            This is a set of zero or more current running modes. For every actuator there will be one current
     *            running mode.
     * @param timerUpdates
     *            A set of zero or more {@link TimerUpdate} objects.
     */
    public UnconstrainedStateUpdate(String resourceId,
                                    Date timestamp,
                                    Date validFrom,
                                    int currentRunningModeId,
                                    Collection<TimerUpdate> timerUpdates) {
        super(resourceId, timestamp, validFrom);
        if (timerUpdates == null) {
            throw new NullPointerException("timerUpdates");
        }

        this.currentRunningModeId = currentRunningModeId;
        this.timerUpdates = Collections.unmodifiableSet(new HashSet<TimerUpdate>(timerUpdates));
    }

    /**
     * @return This is a set of zero or more current running modes. For every actuator there will be one current running
     *         mode.
     */
    public int getCurrentRunningModeId() {
        return currentRunningModeId;
    }

    /**
     * @return A set of zero or more {@link TimerUpdate} objects.
     */
    public Set<TimerUpdate> getTimerUpdates() {
        return timerUpdates;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + 67 * currentRunningModeId + timerUpdates.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        UnconstrainedStateUpdate other = (UnconstrainedStateUpdate) obj;
        if (currentRunningModeId != other.currentRunningModeId) {
            return false;
        } else if (!timerUpdates.equals(other.timerUpdates)) {
            return false;
        }
        return true;
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append("currentRunningModeId=").append(currentRunningModeId).append(", ");
        sb.append("timerUpdates=").append(timerUpdates).append(", ");
    }
}
