package org.flexiblepower.efi.unconstrained;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.flexiblepower.efi.util.RunningMode;

public class UnconstrainedSystemDescription extends UnconstrainedUpdate {
    private final Map<Integer, RunningMode<RunningModeBehaviour>> runningModes;

    public UnconstrainedSystemDescription(String resourceId,
                                          Date timestamp,
                                          Date validFrom,
                                          Collection<RunningMode<RunningModeBehaviour>> runningModes) {
        super(resourceId, timestamp, validFrom);
        if (runningModes == null) {
            throw new NullPointerException("runningModes");
        } else if (runningModes.isEmpty()) {
            throw new IllegalArgumentException("runningModes is empty");
        }

        TreeMap<Integer, RunningMode<RunningModeBehaviour>> temp = new TreeMap<Integer, RunningMode<RunningModeBehaviour>>();
        for (RunningMode<RunningModeBehaviour> runningMode : runningModes) {
            if (temp.containsKey(runningMode.getId())) {
                throw new IllegalArgumentException("Multiple running modes with the same id: " + runningMode.getId());
            }

            temp.put(runningMode.getId(), runningMode);
        }
        this.runningModes = Collections.unmodifiableMap(temp);
    }

    public Collection<RunningMode<RunningModeBehaviour>> getRunningModes() {
        return runningModes.values();
    }

    public RunningMode<RunningModeBehaviour> getRunningMode(int id) {
        return runningModes.get(id);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + runningModes.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        UnconstrainedSystemDescription other = (UnconstrainedSystemDescription) obj;
        return other.runningModes.equals(runningModes);
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append("runningModes=").append(runningModes).append(", ");
    }
}
