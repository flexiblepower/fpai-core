package org.flexiblepower.efi.buffer;

import java.util.Date;
import java.util.Set;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;
import javax.measure.unit.Unit;

import org.flexiblepower.rai.comm.ControlSpaceRegistration;
import org.flexiblepower.rai.values.Commodity;

public class BufferRegistration extends ControlSpaceRegistration {

    /**
     * The label of fill level for display purposes in the UI
     */
    private final String fillLevelLabel;

    /**
     * The unit of fill level for display purposes in the UI
     */
    private final Unit<?> fillLevelUnit;

    /**
     * The duration of the delay in communications channel from the moment of sending to the moment the command is
     * executed up by the device.
     */
    private Measurable<Duration> allocationDelay;

    public Measurable<Duration> getAllocationDelay() {
        return allocationDelay;
    }

    public Set<ActuatorCapabilities> getActuatorCapabilities() {
        return actuatorCapabilities;
    }

    public static class ActuatorCapabilities {
        private final int actuatorId;
        private final String actuatorLabel;
        private final Set<Commodity> commodities;

        public ActuatorCapabilities(int actuatorId, String actuatorLabel, Set<Commodity> commodities) {
            super();
            this.actuatorId = actuatorId;
            this.actuatorLabel = actuatorLabel;
            this.commodities = commodities;
        }

        public int getActuatorId() {
            return actuatorId;
        }

        public String getActuatorLabel() {
            return actuatorLabel;
        }

        public Set<Commodity> getCommodities() {
            return commodities;
        }

    }

    private final Set<ActuatorCapabilities> actuatorCapabilities;

    public BufferRegistration(String resourceId,
                              Date timestamp,
                              String label,
                              Unit<?> unit,
                              Set<ActuatorCapabilities> actuatorCapabilities) {
        super(resourceId, timestamp);
        fillLevelLabel = label;
        fillLevelUnit = unit;
        this.actuatorCapabilities = actuatorCapabilities;
    }

    public String getxLabel() {
        return fillLevelLabel;
    }

    public Unit<?> getxUnit() {
        return fillLevelUnit;
    }
}
