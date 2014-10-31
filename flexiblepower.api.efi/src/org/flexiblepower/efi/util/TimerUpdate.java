package org.flexiblepower.efi.util;

import java.util.Date;

/**
 * This class contains up to date information about the state of the timers. A class sends updates of the timers that
 * have a new finishedAt time. Timers that are not mentioned are assumed to have the same finishedAt time is the most
 * recent TimerUpdate.
 */
public class TimerUpdate {
    private final int timerId;
    private final Date finishedAt;

    /**
     * @param timerId
     *            This id refers uniquely to a timer that is associated to an actuator within a buffer appliance.
     *
     * @param finishedAt
     *            The timestamp that indicates when this timer will be finished. May lie in the past to indicate this
     *            timer has finished.
     */
    public TimerUpdate(int timerId, Date finishedAt) {
        if (finishedAt == null) {
            throw new NullPointerException("Must mention a new finishedAt time, otherwise no TimerUpdate should be made.");
        }

        this.timerId = timerId;
        this.finishedAt = finishedAt;
    }

    /**
     * @return This id refers uniquely to a timer that is associated to an actuator within a buffer appliance.
     */
    public int getTimerId() {
        return timerId;
    }

    /**
     * @return The timestamp that indicates when this timer will be finished.
     */
    public Date getFinishedAt() {
        return finishedAt;
    }

    @Override
    public int hashCode() {
        return 31 * finishedAt.hashCode() + timerId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        TimerUpdate other = (TimerUpdate) obj;
        if (!finishedAt.equals(other.finishedAt)) {
            return false;
        } else if (timerId != other.timerId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TimerUpdate [timerId=" + timerId + ", finishedAt=" + finishedAt + "]";
    }
}
