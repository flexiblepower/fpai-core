package org.flexiblepower.efi.buffer;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

public class TargetProfileElement {

    private final Measurable<Duration> duration;
    private final double xLowerLimit;
    private final double xUpperLimit;

    public TargetProfileElement(Measurable<Duration> duration, double xLowerLimit, double xUpperLimit) {
        super();
        this.duration = duration;
        this.xLowerLimit = xLowerLimit;
        this.xUpperLimit = xUpperLimit;
    }

    public Measurable<Duration> getDuration() {
        return duration;
    }

    public double getLowerLimit() {
        return xLowerLimit;
    }

    public double getUpperLimit() {
        return xUpperLimit;
    }
}
