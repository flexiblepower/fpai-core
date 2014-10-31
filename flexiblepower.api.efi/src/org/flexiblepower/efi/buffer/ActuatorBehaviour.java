package org.flexiblepower.efi.buffer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.flexiblepower.efi.util.FillLevelFunction;
import org.flexiblepower.efi.util.RunningMode;

/**
 * This class describes how an actuator can affect the buffer. It contains a collection of {@link RunningMode}s that use
 * a {@link FillLevelFunction} to describe each one. Each {@link FillLevelFunction} contains a single double value that
 */
public class ActuatorBehaviour {
    public static Builder create(int id) {
        return new Builder(id);
    }

    public static class Builder {
        private final int id;
        private final Set<RunningMode<FillLevelFunction<RunningModeBehaviour>>> runningModes;

        Builder(int id) {
            this.id = id;
            runningModes = new HashSet<RunningMode<FillLevelFunction<RunningModeBehaviour>>>();
        }

        public Builder add(RunningMode<FillLevelFunction<RunningModeBehaviour>> runningMode) {
            runningModes.add(runningMode);
            return this;
        }

        public ActuatorBehaviour build() {
            return new ActuatorBehaviour(id, runningModes);
        }
    }

    private final int id;
    private final Map<Integer, RunningMode<FillLevelFunction<RunningModeBehaviour>>> runningModes;

    public ActuatorBehaviour(int id,
                             Collection<RunningMode<FillLevelFunction<RunningModeBehaviour>>> runningModes) {
        this.id = id;
        TreeMap<Integer, RunningMode<FillLevelFunction<RunningModeBehaviour>>> tempRunningModes = new TreeMap<Integer, RunningMode<FillLevelFunction<RunningModeBehaviour>>>();
        for (RunningMode<FillLevelFunction<RunningModeBehaviour>> runningMode : runningModes) {
            if (tempRunningModes.containsKey(runningMode.getId())) {
                throw new IllegalArgumentException(String.format("Cannot add another RunningMode with the same Id {0} to this ActuatorBehaviour instance.",
                                                                 runningMode.getId()));
            }
            tempRunningModes.put(runningMode.getId(), runningMode);
        }
        this.runningModes = Collections.unmodifiableMap(tempRunningModes);
    }

    /**
     * @return A unique identifier (within the context of this buffer) for this actuator. This should always respond to
     *         a {@link Actuator} that has been registered.
     */
    public int getId() {
        return id;
    }

    /**
     * @return A list of zero or more possible running modes for this actuator.
     */
    public Collection<RunningMode<FillLevelFunction<RunningModeBehaviour>>> getRunningModes() {
        return runningModes.values();
    }

    /**
     * @param id
     *            The identifier of the {@link RunningMode}
     * @return The corresponding {@link RunningMode}
     */
    public RunningMode<FillLevelFunction<RunningModeBehaviour>> getRunningMode(int id) {
        return runningModes.get(id);
    }

    /**
     * @return The minimum fill level for which this actuator can operate
     */
    public double getLowerBound() {
        double min = Double.MAX_VALUE;
        for (RunningMode<FillLevelFunction<RunningModeBehaviour>> rm : runningModes.values()) {
            double rmLowerBound = rm.getValue().getLowerBound();
            if (min > rmLowerBound) {
                min = rmLowerBound;
            }
        }
        return min;
    }

    /**
     * @return The maximum fill level for which this actuator can operate
     */
    public double getUpperBound() {
        double max = Double.MIN_VALUE;
        for (RunningMode<FillLevelFunction<RunningModeBehaviour>> rm : runningModes.values()) {
            double rmUpperBound = rm.getValue().getUpperBound();
            if (max < rmUpperBound) {
                max = rmUpperBound;
            }
        }
        return max;
    }
}
