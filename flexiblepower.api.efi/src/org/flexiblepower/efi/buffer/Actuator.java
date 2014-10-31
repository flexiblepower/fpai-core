package org.flexiblepower.efi.buffer;

import org.flexiblepower.rai.values.Commodity;
import org.flexiblepower.rai.values.CommoditySet;

/**
 * This class describes the capabilities of an actuator within an appliance of the buffer category.
 */
public class Actuator {
    private final int actuatorId;
    private final String actuatorLabel;
    private final CommoditySet supportedCommodities;

    /**
     * @param actuatorId
     *            A unique identifier for this actuator. The identifier only has to be unique within the context of the
     *            appliance, therefore a simple integer suffices.
     * @param actuatorLabel
     *            A human readable label for this actuator. E.g. Stirling engine.
     * @param commodities
     *            A set of zero or more commodities that are being consumed and/or produced by this actuator.
     */
    public Actuator(int actuatorId, String actuatorLabel, CommoditySet commodities) {
        if (actuatorLabel == null) {
            throw new NullPointerException("actuatorLabel");
        } else if (commodities == null) {
            throw new NullPointerException("commodities");
        } else if (commodities.isEmpty()) {
            throw new IllegalArgumentException("commodities is empty");
        }

        this.actuatorId = actuatorId;
        this.actuatorLabel = actuatorLabel;
        supportedCommodities = commodities;
    }

    /**
     * @return A unique identifier for this actuator. The identifier only has to be unique within the context of the
     *         appliance, therefore a simple integer suffices.
     */
    public int getActuatorId() {
        return actuatorId;
    }

    /**
     * @return A human readable label for this actuator. E.g. Stirling engine.
     */
    public String getActuatorLabel() {
        return actuatorLabel;
    }

    /**
     * @return A set of zero or more commodities that are being consumed and/or produced by this actuator.
     */
    public CommoditySet getCommodities() {
        return supportedCommodities;
    }

    /**
     * @param commodity
     *            The commodity to check
     * @return <code>true</code> if the given commodity is in the commodities set (see {@link #getCommodities()}.
     */
    public boolean supportsCommodity(Commodity<?, ?> commodity) {
        return supportedCommodities.contains(commodity);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + actuatorId;
        result = prime * result + actuatorLabel.hashCode();
        result = prime * result + supportedCommodities.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Actuator other = (Actuator) obj;
        if (actuatorId != other.actuatorId) {
            return false;
        } else if (!actuatorLabel.equals(other.actuatorLabel)) {
            return false;
        } else if (!supportedCommodities.equals(other.supportedCommodities)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Actuator [actuatorId=" + actuatorId
               + ", actuatorLabel="
               + actuatorLabel
               + ", supportedCommodities="
               + supportedCommodities
               + "]";
    }
}
