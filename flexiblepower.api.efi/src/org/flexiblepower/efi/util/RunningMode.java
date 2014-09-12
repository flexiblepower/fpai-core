package org.flexiblepower.efi.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * This class is used to describe different modes in which a device can run. This is used in both the buffer and the
 * unconstrained abstractions.
 */
public class RunningMode<T> {
    private final int id;
    private final String name;
    private final T value;
    private final Map<Integer, Transition> possibleTransitions;

    protected RunningMode(int id,
                          String name,
                          T value,
                          Set<Transition> possibleTransitions) {
        if (name == null) {
            throw new NullPointerException("name");
        } else if (value == null) {
            throw new NullPointerException("value");
        } else if (possibleTransitions == null) {
            throw new NullPointerException("possibleTransitions");
        }

        this.id = id;
        this.name = name;
        this.value = value;

        TreeMap<Integer, Transition> tempTransitions = new TreeMap<Integer, Transition>();
        for (Transition transition : possibleTransitions) {
            tempTransitions.put(transition.getToRunningMode(), transition);
        }
        this.possibleTransitions = Collections.unmodifiableMap(tempTransitions);
    }

    /**
     * @return A unique identifier (within the context of this device) for this running mode.
     */
    public int getId() {
        return id;
    }

    /**
     * @return A human readable name for this running mode. E.g. “full power”.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The value associated with this {@link RunningMode}
     */
    public T getValue() {
        return value;
    }

    /**
     * @return A list of zero or more {@link Transition} objects that contain constraints for switching from this
     *         RunningMode to another.
     */
    public Collection<Transition> getTransitions() {
        return possibleTransitions.values();
    }

    /**
     * @param runningMode
     *            The running mode to switch to
     * @return The {@link Transition} that can be used to move to that {@link RunningMode}, or <code>null</code> if that
     *         transition is not possible
     */
    public Transition getTransitionTo(RunningMode<T> runningMode) {
        return getTransitionTo(runningMode.getId());
    }

    /**
     * @param runningModeId
     *            The identifier of the running mode to switch to
     * @return The {@link Transition} that can be used to move to the corresponding {@link RunningMode}, or
     *         <code>null</code> if that transition is not possible
     */
    public Transition getTransitionTo(int runningModeId) {
        return possibleTransitions.get(runningModeId);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + name.hashCode();
        result = prime * result + possibleTransitions.hashCode();
        result = prime * result + value.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        @SuppressWarnings("unchecked")
        RunningMode<T> other = (RunningMode<T>) obj;
        if (id != other.id) {
            return false;
        } else if (!name.equals(other.name)) {
            return false;
        } else if (!possibleTransitions.equals(other.possibleTransitions)) {
            return false;
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }
}
