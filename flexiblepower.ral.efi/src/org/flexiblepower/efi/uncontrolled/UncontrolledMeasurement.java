package org.flexiblepower.efi.uncontrolled;

import java.util.Date;

import org.flexiblepower.ral.values.Commodity;
import org.flexiblepower.ral.values.CommodityMeasurables;

/**
 * The uncontrolled measurement update message is used for updating the consumption/production information of an
 * appliance form the appliance driver to the energy app. Since the energy behaviour of an uncontrolled appliance can be
 * very unpredictable (for instance the power output of a PV inverter), this measurement update message only contains a
 * measured value and does not provide any information on how long this value is valid. Typically the appliance driver
 * must send a new measurement update message to the energy app whenever the value of the measurement is updated.
 */
public final class UncontrolledMeasurement extends UncontrolledUpdate {
    private final CommodityMeasurables measurable;

    public UncontrolledMeasurement(String resourceId,
                                   Date timestamp,
                                   Date validFrom,
                                   CommodityMeasurables measurable) {
        super(resourceId, timestamp, validFrom);
        if (measurable == null) {
            throw new NullPointerException("measurable");
        }
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
        return 31 * super.hashCode() + measurable.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        UncontrolledMeasurement other = (UncontrolledMeasurement) obj;
        return other.measurable.equals(measurable);
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append("measurable=").append(measurable).append(", ");
    }
}
