package org.flexiblepower.efi.buffer;

import java.io.Serializable;
import java.util.List;

import org.flexiblepower.efi.util.Timer;

public class Actuator implements Serializable {

    private static final long serialVersionUID = -7888970166563077855L;

    private final int id;

    // Timers associated with this actuator
    private final List<Timer> timerList;

    // List of running modes
    private final List<RunningMode> runningModes;

    public Actuator(int id, List<Timer> timerList, List<RunningMode> runningModes) {
        super();
        this.id = id;
        this.timerList = timerList;
        this.runningModes = runningModes;
    }

    public int getId() {
        return id;
    }

    public List<Timer> getTimerList() {
        return timerList;
    }

    public List<RunningMode> getRunningModes() {
        return runningModes;
    }

    /**
     * Determines the minimum fill level for which this actuator can operate.
     * 
     * @return The minimum fill level for which this actuator can operate
     */
    public double minFillLevel() {
        double min = Double.MAX_VALUE;
        for (RunningMode rm : runningModes) {
            double rmLowerBound = rm.getLowerBound();
            if (min > rmLowerBound) {
                min = rmLowerBound;
            }
        }
        return min;
    }

    /**
     * Determines the maximum fill level for which this actuator can operate.
     * 
     * @return The maximum fill level for which this actuator can operate
     */
    public double maxFillLevel() {
        double max = Double.MIN_VALUE;
        for (RunningMode rm : runningModes) {
            double rmUpperBound = rm.getUpperBound();
            if (max < rmUpperBound) {
                max = rmUpperBound;
            }
        }
        return max;
    }

}
