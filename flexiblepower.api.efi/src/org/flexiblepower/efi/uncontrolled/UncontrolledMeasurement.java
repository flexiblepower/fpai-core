package org.flexiblepower.efi.uncontrolled;

import java.util.Date;

import org.flexiblepower.rai.values.Commodity;
import org.flexiblepower.rai.values.CommodityMeasurables;

/**
 * The uncontrolled measurement update message is used for updating the consumption/production information of an
 * appliance form the appliance driver to the energy app. Since the energy behaviour of an uncontrolled appliance can be
 * very unpredictable (for instance the power output of a PV inverter), this measurement update message only contains a
 * measured value and does not provide any information on how long this value is valid. Typically the appliance driver
 * must send a new measurement update message to the energy app whenever the value of the measurement is updated.
 */
public final class UncontrolledMeasurement extends UncontrolledUpdate {
    private static final long serialVersionUID = -2685007932788218012L;

    private final CommodityMeasurables measurable;

    public UncontrolledMeasurement(String resourceId,
                                   Date timestamp,
                                   Date validFrom,
                                   CommodityMeasurables measurable) {
        super(resourceId, timestamp, validFrom);
        this.measurable = measurable;
    }

    /**
     * @return A map with the as key the {@link Commodity} which describes the commodity of the Measurable in key. The
     *         key in the map is the new measurement that the appliance driver wants to send towards the energy app.
     */
    public CommodityMeasurables getMeasurable() {
        return measurable;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((measurable == null) ? 0 : measurable.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        UncontrolledMeasurement other = (UncontrolledMeasurement) obj;
        if (measurable == null) {
            if (other.measurable != null) {
                return false;
            }
        } else if (!measurable.equals(other.measurable)) {
            return false;
        }
        return true;
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append("measurable=").append(measurable).append(", ");
    }
}
