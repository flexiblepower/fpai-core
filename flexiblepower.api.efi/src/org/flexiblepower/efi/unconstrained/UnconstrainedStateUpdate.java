package org.flexiblepower.efi.unconstrained;

import java.util.Date;
import java.util.Set;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

public class UnconstrainedStateUpdate extends UnconstrainedUpdate {

    private final int currentRunningModeId;
    private final Set<TimerUpdate> timerUpdates;

    public UnconstrainedStateUpdate(String resourceId,
                                    Date timestamp,
                                    Date validFrom,
                                    Measurable<Duration> allocationDelay,
                                    int currentRunningModeId,
                                    Set<TimerUpdate> timerUpdates) {
        super(resourceId, timestamp, validFrom, allocationDelay);
        this.currentRunningModeId = currentRunningModeId;
        this.timerUpdates = timerUpdates;
    }

    public static class TimerUpdate {
        public int timerId;
        public Date finishedAt;
    }
}
