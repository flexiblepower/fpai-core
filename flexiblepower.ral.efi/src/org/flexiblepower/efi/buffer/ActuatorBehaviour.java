package org.flexiblepower.efi.buffer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.flexiblepower.efi.util.FillLevelFunction;
import org.flexiblepower.efi.util.RunningMode;
import org.flexiblepower.efi.util.Transition;

/**
 * This class describes how an actuator can affect the buffer. It contains a collection of {@link RunningMode}s that use
 * a {@link FillLevelFunction} to describe each one. Each {@link FillLevelFunction} contains for every fill level of the
 * buffer a {@link RunningModeBehaviour}. The {@link RunningModeBehaviour} describes the effect on the buffer when this
 * {@link RunningMode} is selected and the consumed or produced commodities.
 */
public class ActuatorBehaviour {

    /**
     * @param id
     *            Identifier of this Actuator
     * @return a new {@link Builder} object that can be used to easily create the {@link ActuatorBehaviour}
     */
    public static Builder create(int id) {
        return new Builder(id);
    }

    /**
     * This helper class should be used to easily define a {@link ActuatorBehaviour}. Use the
     * {@link ActuatorBehaviour#create(double)} method to get a new instance of this class.
     */
    public static class Builder {
        private final int id;
        private final Set<RunningMode<FillLevelFunction<RunningModeBehaviour>>> runningModes;

        Builder(int id) {
            this.id = id;
            runningModes = new HashSet<RunningMode<FillLevelFunction<RunningModeBehaviour>>>();
        }

        /**
         * Adds a {@link RunningMode} to the {@link ActuatorBehaviour} object that we are creating.
         *
         * @param runningMode
         *            The {@link RunningMode} to add to the {@link ActuatorBehaviour} object that we are creating
         * @return This {@link Builder}
         */
        public Builder add(RunningMode<FillLevelFunction<RunningModeBehaviour>> runningMode) {
            runningModes.add(runningMode);
            return this;
        }

        /**
         * @return A new immutable {@link ActuatorBehaviour} object that contains all the elements that have been added
         *         until now.
         */
        public ActuatorBehaviour build() {
            return new ActuatorBehaviour(id, runningModes);
        }
    }

    private final int id;
    private final Map<Integer, RunningMode<FillLevelFunction<RunningModeBehaviour>>> runningModes;

    /**
     * Construct an ActuatorBehaviour instance.
     *
     * @param id
     *            Device unique identifier for this actuator
     * @param runningModes
     *            Collection describing all the supported runningModes of this
     *
     * @throws IllegalArgumentException
     *             Thrown when there are multiple RunningMode with the same identifier or when a {@link Transition} has
     *             an unknown RunningMode as target
     */
    public ActuatorBehaviour(int id,
                             Collection<RunningMode<FillLevelFunction<RunningModeBehaviour>>> runningModes) {
        this.id = id;
        // Test for duplicate RunningMode Id's
        TreeMap<Integer, RunningMode<FillLevelFunction<RunningModeBehaviour>>> tempRunningModes = new TreeMap<Integer, RunningMode<FillLevelFunction<RunningModeBehaviour>>>();
        for (RunningMode<FillLevelFunction<RunningModeBehaviour>> runningMode : runningModes) {
            if (tempRunningModes.containsKey(runningMode.getId())) {
                throw new IllegalArgumentException("Cannot add multiple RunningModes with the same Id (" + runningMode.getId()
                                                   + ") to this ActuatorBehaviour instance");
            }
            tempRunningModes.put(runningMode.getId(), runningMode);
        }
        // Test for Transitions with non-existing destinations
        for (RunningMode<FillLevelFunction<RunningModeBehaviour>> runningMode : tempRunningModes.values()) {
            for (Transition transition : runningMode.getTransitions()) {
                if (!tempRunningModes.containsKey(transition.getToRunningMode())) {
                    throw new IllegalArgumentException("There is a Transition that has a target (toRunningMode) with a RunningMode Id which does not exist: " + transition.getToRunningMode());
                }
            }
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
