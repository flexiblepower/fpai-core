package org.flexiblepower.api.efi.bufferhelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.flexiblepower.efi.buffer.BufferRegistration.ActuatorCapabilities;
import org.flexiblepower.efi.buffer.RunningMode;
import org.flexiblepower.efi.buffer.Transition;
import org.flexiblepower.efi.util.Timer;
import org.flexiblepower.rai.values.Commodity;

public class BufferActuator {

    private final int actuatorId;
    private final String actuatorLabel;
    private final Set<Commodity<?, ?>> commodities;
    private Map<Integer, RunningMode> allRunningModes;
    private List<Timer> timerList;
    private int currentRunningModeId;

    public Map<Integer, RunningMode> getAllRunningModes() {
        return allRunningModes;
    }

    public void setAllRunningModes(Collection<RunningMode> allRunningModes) {
        this.allRunningModes = new TreeMap<Integer, RunningMode>();
        for (RunningMode mode : allRunningModes) {
            this.allRunningModes.put(mode.getId(), mode);
        }
    }

    public Set<RunningMode> getPossibleRunningModes() {
        Set<RunningMode> targets = new HashSet<RunningMode>();
        for (Transition i : allRunningModes.get(currentRunningModeId).getTransitions()) {
            targets.add(allRunningModes.get(i.getToRunningMode()));
        }
        return targets;
    }

    private BufferActuator(int actuatorId, String actuatorLabel, Set<Commodity<?, ?>> commodities) {
        this.actuatorId = actuatorId;
        this.actuatorLabel = actuatorLabel;
        this.commodities = commodities;
    }

    public BufferActuator(ActuatorCapabilities ac) {
        this(ac.getActuatorId(), ac.getActuatorLabel(), ac.getCommodities());
    }

    public List<Timer> getTimerList() {
        return timerList;
    }

    public void setTimerList(Collection<Timer> timerList) {
        this.timerList = new ArrayList<Timer>(timerList);
    }

}
