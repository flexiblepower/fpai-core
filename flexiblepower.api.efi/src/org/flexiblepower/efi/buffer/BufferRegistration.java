package org.flexiblepower.efi.buffer;

import java.util.Date;
import java.util.Set;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;
import javax.measure.unit.Unit;

import org.flexiblepower.rai.ControlSpaceRegistration;
import org.flexiblepower.rai.values.Commodity;

public class BufferRegistration extends ControlSpaceRegistration {

    private static final long serialVersionUID = -327039316450587840L;

    /**
     * The label of fill level for display purposes in the UI
     */
    private final String fillLevelLabel;

    /**
     * The unit of fill level for display purposes in the UI
     */
    private final Unit<?> fillLevelUnit;

    public Set<ActuatorCapabilities> getActuatorCapabilities() {
        return actuatorCapabilities;
    }

    public static class ActuatorCapabilities {
        private final int actuatorId;
        private final String actuatorLabel;
        private final Commodity.Set supportedCommodities;

        public ActuatorCapabilities(int actuatorId, String actuatorLabel, Commodity.Set commodities) {
            super();
            this.actuatorId = actuatorId;
            this.actuatorLabel = actuatorLabel;
            supportedCommodities = commodities;
        }

        public int getActuatorId() {
            return actuatorId;
        }

        public String getActuatorLabel() {
            return actuatorLabel;
        }

        public Commodity.Set getCommodities() {
            return supportedCommodities;
        }

        public boolean supportsCommodity(Commodity<?, ?> commodity) {
            return supportedCommodities.contains(commodity);
        }

    }

    private final Set<ActuatorCapabilities> actuatorCapabilities;

    public BufferRegistration(String resourceId,
                              Date timestamp,
                              Measurable<Duration> allocationDelay,
                              String fillLevelLabel,
                              Unit<?> fillLevelUnit,
                              Set<ActuatorCapabilities> actuatorCapabilities) {
        super(resourceId, timestamp, allocationDelay);
        this.fillLevelLabel = fillLevelLabel;
        this.fillLevelUnit = fillLevelUnit;
        this.actuatorCapabilities = actuatorCapabilities;
    }

    public String getFillLevelLabel() {
        return fillLevelLabel;
    }

    public Unit<?> getFillLevelUnit() {
        return fillLevelUnit;
    }
}
