package org.flexiblepower.api.efi.bufferhelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;
import javax.measure.unit.Unit;

import org.flexiblepower.efi.buffer.Actuator;
import org.flexiblepower.efi.buffer.BufferRegistration;
import org.flexiblepower.efi.buffer.BufferRegistration.ActuatorCapabilities;
import org.flexiblepower.efi.buffer.BufferSystemDescription;
import org.flexiblepower.efi.buffer.LeakageFunction;

public class Buffer {

    private final String resourceId;
    private final String fillLevelLabel;
    private final Unit<?> fillLevelUnit;
    private final Measurable<Duration> allocationDelay;
    private final Map<Integer, BufferActuator> actuators;
    private LeakageFunction leakageFunction;

    private Buffer(String resourceId,
                   String getxLabel,
                   Unit<?> getxUnit,
                   Measurable<Duration> allocationDelay,
                   Set<ActuatorCapabilities> actuatorCapabilities) {
        this.resourceId = resourceId;
        fillLevelLabel = getxLabel;
        fillLevelUnit = getxUnit;
        this.allocationDelay = allocationDelay;
        actuators = new HashMap<Integer, BufferActuator>();
        for (ActuatorCapabilities ac : actuatorCapabilities) {
            actuators.put(ac.getActuatorId(), new BufferActuator(ac));
        }
    }

    /** A Buffer may only be constructed from a complete BufferRegistration message. */
    public Buffer(BufferRegistration br) {
        this(br.getResourceId(),
             br.getFillLevelLabel(),
             br.getFillLevelUnit(),
             br.getAllocationDelay(),
             br.getActuatorCapabilities());
    }

    public void ProcessSystemDescription(BufferSystemDescription bsd) {
        setLeakageFunction(bsd.getBufferLeakage());

        for (Actuator actuatorDescription : bsd.getActuators()) {
            if (actuators.containsKey(actuatorDescription.getId())) {
                actuators.get(actuatorDescription.getId()).setAllRunningModes(actuatorDescription.getRunningModes());
                actuators.get(actuatorDescription.getId()).setTimerList(actuatorDescription.getTimerList());
            } else {
                throw new IllegalArgumentException("The ActuatorId in the BufferSystemDescription is not known.");
            }
        }
    }

    private void setLeakageFunction(LeakageFunction bufferLeakage) {
        leakageFunction = bufferLeakage;
    }

}
