package org.flexiblepower.efi.uncontrolled;

import java.util.Date;
import java.util.Map;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

import org.flexiblepower.rai.values.Commodity;

/**
 * The uncontrolled measurement update message is used for updating the consumption/production information of an
 * appliance form the appliance driver to the energy app. Since the energy behaviour of an uncontrolled appliance can be
 * very unpredictable (for instance the power output of a PV inverter), this measurement update message only contains a
 * measured value and does not provide any information on how long this value is valid. Typically the appliance driver
 * must send a new measurement update message to the energy app whenever the value of the measurement is updated.
 * 
 * @author TNO
 * 
 */
public final class UncontrolledMeasurement extends UncontrolledUpdate {

    private static final long serialVersionUID = -2685007932788218012L;

    /**
     * A map with the as key the Commodity which describes the commodity of the Measurable in key. The key in the map is
     * the new measurement that the appliance driver wants to send towards the energy app.
     */
    private final Map<Commodity<?, ?>, Measurable<?>> measurements;

    public UncontrolledMeasurement(String resourceId,
                                   Date timestamp,
                                   Date validFrom,
                                   Measurable<Duration> allocationDelay,
                                   Map<Commodity<?, ?>, Measurable<?>> measurements) {
        super(resourceId, timestamp, validFrom, allocationDelay);
        this.measurements = measurements;
    }

    public Map<Commodity<?, ?>, Measurable<?>> getMeasurements() {
        return measurements;
    }

}
