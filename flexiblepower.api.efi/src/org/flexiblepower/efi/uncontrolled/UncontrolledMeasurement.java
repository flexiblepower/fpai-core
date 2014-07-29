package org.flexiblepower.efi.uncontrolled;

import java.util.Date;
import java.util.Map;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

import org.flexiblepower.rai.values.Commodity;

public final class UncontrolledMeasurement extends UncontrolledUpdate {

    private static final long serialVersionUID = -2685007932788218012L;

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
