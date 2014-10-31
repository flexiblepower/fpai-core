package org.flexiblepower.efi.unconstrained;

import java.util.Date;

public class RunningModeSelector {
    private final int runningModeId;
    private final Date startTime;

    /**
     * @param runningModeId
     *            An id that uniquely refers to a running mode.
     * @param startTime
     *            The start time for the running mode that is referred to in the RunningModeSelector.
     */
    public RunningModeSelector(int runningModeId, Date startTime) {
        if (startTime == null) {
            throw new NullPointerException("startTime");
        }

        this.runningModeId = runningModeId;
        this.startTime = startTime;
    }

    /**
     * @return An id that uniquely refers to a running mode.
     */
    public int getRunningModeId() {
        return runningModeId;
    }

    /**
     * @return The start time for the running mode that is referred to in the RunningModeSelector.
     */
    public Date getStartTime() {
        return startTime;
    }

    @Override
    public int hashCode() {
        return 31 * runningModeId + startTime.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        RunningModeSelector other = (RunningModeSelector) obj;
        if (runningModeId != other.runningModeId) {
            return false;
        } else if (!startTime.equals(other.startTime)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RunningModeSelector [runningModeId=" + runningModeId + ", startTime=" + startTime + "]";
    }
}
