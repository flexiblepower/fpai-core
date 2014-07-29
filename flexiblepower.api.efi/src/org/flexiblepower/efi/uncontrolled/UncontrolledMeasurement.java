package org.flexiblepower.efi.uncontrolled;

import java.util.Map;

import javax.measure.Measurable;

import org.flexiblepower.rai.values.Commodity;

public class UncontrolledMeasurement {

    private final Map<Commodity, Measurable> measurements;

    public UncontrolledMeasurement(Map<Commodity, Measurable> measurements) {
        super();
        this.measurements = measurements;
    }

}
