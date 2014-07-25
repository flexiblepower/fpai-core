package org.flexiblepower.rai.values;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

public class TargetProfileElement implements ProfileElement<TargetProfileElement> {

    private final Measurable<Duration> duration;
    private final double fillLevelLowerLimit;
    private final double fillLevelUpperLimit;

    public TargetProfileElement(Measurable<Duration> duration, double fillLevelLowerLimit, double fillLevelUpperLimit) {
        super();
        this.duration = duration;
        this.fillLevelLowerLimit = fillLevelLowerLimit;
        this.fillLevelUpperLimit = fillLevelUpperLimit;
    }

    @Override
    public Measurable<Duration> getDuration() {
        return duration;
    }

    @Override
    public TargetProfileElement subProfile(Measurable<Duration> offset, Measurable<Duration> duration) {
        return new TargetProfileElement(duration, fillLevelLowerLimit, fillLevelUpperLimit);
    }

}
