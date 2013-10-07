package org.flexiblepower.time;

import static javax.measure.unit.SI.MILLI;
import static javax.measure.unit.SI.SECOND;

import java.util.Date;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.unit.Unit;

public class TimeUtil {
    public static final Unit<Duration> MS = MILLI(SECOND);
    public static final Measurable<Duration> ZERO = Measure.zero();

    private TimeUtil() {
    }

    public static Date add(Date startDate, Measurable<Duration> duration) {
        long ms = duration.longValue(MS);
        return new Date(startDate.getTime() + ms);
    }

    public static Date subtract(Date startDate, Measurable<Duration> duration) {
        long ms = duration.longValue(MS);
        return new Date(startDate.getTime() - ms);
    }
}
