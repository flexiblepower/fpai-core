package org.flexiblepower.efi.uncontrolled;

import java.util.Date;
import java.util.Map;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

import org.flexiblepower.rai.values.Commodity;

public class UncontrolledMeasurement extends UncontrolledUpdate {

    private final Map<Commodity, Measurable> measurements;

    public UncontrolledMeasurement(String resourceId,
                                   Date timestamp,
                                   Date validFrom,
                                   Measurable<Duration> allocationDelay,
                                   Map<Commodity, Measurable> measurements) {
        super(resourceId, timestamp, validFrom, allocationDelay);
        this.measurements = measurements;
    }

}
