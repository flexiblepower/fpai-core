package org.flexiblepower.efi.buffer;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.flexiblepower.efi.util.Timer;

public class Actuator implements Serializable {

    private static final long serialVersionUID = -7888970166563077855L;

    public static Builder create(int id) {
        return new Builder(id);
    }

    public static class Builder {
        private final int id;
        private final Set<Timer> timers;
        private final Set<RunningMode> runningModes;

        Builder(int id) {
            this.id = id;
            timers = new HashSet<Timer>();
            runningModes = new HashSet<RunningMode>();
        }

        Builder add(Timer timer) {
            timers.add(timer);
            return this;
        }

        Builder add(RunningMode runningMode) {
            runningModes.add(runningMode);
            return this;
        }

        Actuator build() {
            return new Actuator(id, timers, runningModes);
        }
    }

    private final int id;

    // Timers associated with this actuator
    private final Map<Integer, Timer> timers;

    // List of all the possible running modes of this actuator.
    private final Map<Integer, RunningMode> runningModes;

    public Actuator(int id,
                    Collection<Timer> timers,
                    Collection<RunningMode> runningModes) {
        this.id = id;

        TreeMap<Integer, Timer> tempTimers = new TreeMap<Integer, Timer>();
        for (Timer timer : timers) {
            tempTimers.put(timer.getId(), timer);
        }
        this.timers = Collections.unmodifiableMap(tempTimers);

        TreeMap<Integer, RunningMode> tempRunningModes = new TreeMap<Integer, RunningMode>();
        for (RunningMode runningMode : runningModes) {
            tempRunningModes.put(runningMode.getId(), runningMode);
        }
        this.runningModes = Collections.unmodifiableMap(tempRunningModes);
    }

    public int getId() {
        return id;
    }

    public Collection<Timer> getTimerList() {
        return timers.values();
    }

    public Collection<RunningMode> getRunningModes() {
        return runningModes.values();
    }

    public RunningMode getRunningMode(int id) {
        return runningModes.get(id);
    }

    /**
     * Determines the minimum fill level for which this actuator can operate.
     *
     * @return The minimum fill level for which this actuator can operate
     */
    public double minFillLevel() {
        double min = Double.MAX_VALUE;
        for (RunningMode rm : runningModes.values()) {
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
        for (RunningMode rm : runningModes.values()) {
            double rmUpperBound = rm.getUpperBound();
            if (max < rmUpperBound) {
                max = rmUpperBound;
            }
        }
        return max;
    }
}
