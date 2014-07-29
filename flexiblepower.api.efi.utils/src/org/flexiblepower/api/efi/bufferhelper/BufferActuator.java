package org.flexiblepower.api.efi.bufferhelper;

import java.util.List;
import java.util.Set;

import org.flexiblepower.efi.buffer.BufferRegistration.ActuatorCapabilities;
import org.flexiblepower.efi.buffer.RunningMode;
import org.flexiblepower.rai.values.Commodity;

public class BufferActuator {

    private final int actuatorId;
    private final String actuatorLabel;
    private final Set<Commodity> commodities;
    private List<RunningMode> allRunningModes;

    public List<RunningMode> getAllRunningModes() {
        return allRunningModes;
    }

    public void setAllRunningModes(List<RunningMode> possibleRunningModes) {
        allRunningModes = possibleRunningModes;
    }

    private BufferActuator(int actuatorId, String actuatorLabel, Set<Commodity> commodities) {
        this.actuatorId = actuatorId;
        this.actuatorLabel = actuatorLabel;
        this.commodities = commodities;
    }

    public BufferActuator(ActuatorCapabilities ac) {
        this(ac.getActuatorId(), ac.getActuatorLabel(), ac.getCommodities());
    }
}
