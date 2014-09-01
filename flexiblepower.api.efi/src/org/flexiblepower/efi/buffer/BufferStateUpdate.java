package org.flexiblepower.efi.buffer;

import java.util.Date;
import java.util.Set;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Duration;

public class BufferStateUpdate extends BufferUpdate {

    private static final long serialVersionUID = 899987142667364534L;

    /*
     * May be sent only after a {@link BufferSystemDescription} has taken place.
     */
    public static class ActuatorUpdate {
        private final int actuatorId;
        private final int currentRunningModeId;
        private final Set<TimerUpdate> timerUpdates;

        public ActuatorUpdate(int actuatorId, int currentRunningModeId, Set<TimerUpdate> timerUpdates) {
            super();
            this.actuatorId = actuatorId;
            this.currentRunningModeId = currentRunningModeId;
            this.timerUpdates = timerUpdates;
        }

        public int getActuatorId() {
            return actuatorId;
        }

        public int getCurrentRunningModeId() {
            return currentRunningModeId;
        }

        public Set<TimerUpdate> getTimerUpdates() {
            return timerUpdates;
        }

    }

    public static class TimerUpdate {
        private final int timerId;
        private final Date finishedAt;

        public TimerUpdate(int timerId, Date finishedAt) {
            this.timerId = timerId;
            this.finishedAt = finishedAt;
        }

        public int getTimerId() {
            return timerId;
        }

        public Date getFinishedAt() {
            return finishedAt;
        }
    }

    private final Measure<Double, ?> currentFillLevel;
    private final Set<ActuatorUpdate> currentRunningMode;

    public BufferStateUpdate(String resourceId,
                             Date timestamp,
                             Date validFrom,
                             Measurable<Duration> allocationDelay,
                             Measure<Double, ?> currentFillLevel,
                             Set<ActuatorUpdate> currentRunningMode) {
        super(resourceId, timestamp, validFrom, allocationDelay);
        this.currentFillLevel = currentFillLevel;
        this.currentRunningMode = currentRunningMode;
    }

    public Measure<Double, ?> getCurrentFillLevel() {
        return currentFillLevel;
    }

    public double getCurrentFillLevelAsDouble() {
        return currentFillLevel.getValue();
    }

    public Set<ActuatorUpdate> getCurrentRunningMode() {
        return currentRunningMode;
    }

}
