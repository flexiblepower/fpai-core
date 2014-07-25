package org.flexiblepower.rai.values;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

public class ForecastProfileElement implements ProfileElement<ForecastProfileElement> {

    private final UncertainMeasure<Duration> duration;
    private final double FillingSpeed;

    public ForecastProfileElement(UncertainMeasure<Duration> duration, double fillingSpeed) {
        super();
        this.duration = duration;
        FillingSpeed = fillingSpeed;
    }

    @Override
    public Measurable getDuration() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ForecastProfileElement subProfile(Measurable offset, Measurable duration) {
        // TODO Auto-generated method stub
        return null;
    }

}
