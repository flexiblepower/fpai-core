package org.flexiblepower.api.efi.bufferhelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.flexiblepower.efi.buffer.BufferRegistration.ActuatorCapabilities;
import org.flexiblepower.efi.buffer.RunningMode;
import org.flexiblepower.efi.buffer.Transition;
import org.flexiblepower.efi.util.Timer;
import org.flexiblepower.rai.values.Commodity;

public class BufferActuator {

    private final int actuatorId;
    private final String actuatorLabel;
    private final Set<Commodity<?, ?>> commodities;
    private List<RunningMode> allRunningModes;
    private List<Timer> timerList;
    private int currentRunningModeId;

    public List<RunningMode> getAllRunningModes() {
        return allRunningModes;
    }

    public void setAllRunningModes(List<RunningMode> allRunningModes) {
        this.allRunningModes = allRunningModes;
    }

    public Set<RunningMode> getPossibleRunningModes() {
        Set<RunningMode> targets = new HashSet<RunningMode>();
        for (Transition i : allRunningModes.get(currentRunningModeId).getPossibleTransitions()) {
            targets.add(i.getToRunningMode());
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

    public void setTimerList(List<Timer> timerList) {
        this.timerList = timerList;
    }

}
