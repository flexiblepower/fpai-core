package org.flexiblepower.rai.values;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

/**
 * 
 * 
 * @author TNO
 * 
 */
public class TargetProfileElement implements ProfileElement<TargetProfileElement> {

    /** The duration of this element. */
    private final Measurable<Duration> duration;
    /** This attribute indicates the minimum value for the fill level of a buffer. */
    private final double fillLevelLowerLimit;
    /** This attribute indicates the maximum value for the fill level of a buffer. */
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
