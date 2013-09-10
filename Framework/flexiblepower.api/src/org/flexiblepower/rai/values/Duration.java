package org.flexiblepower.rai.values;

import java.util.Date;

import org.flexiblepower.rai.unit.TimeUnit;

public class Duration extends Value<TimeUnit> {
    public static final Duration ZERO = new Duration(0, TimeUnit.SECONDS);

    public Duration(double value, TimeUnit unit) {
        super(value, unit);
    }

    /** Creates a duration of to - from in milliseconds. This duration is negative in case to is before from. */
    public Duration(Date from, Date to) {
        super(to.getTime() - from.getTime(), TimeUnit.MILLISECONDS);
    }

    public long getMilliseconds() {
        return (long) getValueAs(TimeUnit.MILLISECONDS);
    }

    public Date addTo(Date date) {
        return new Date(date.getTime() + getMilliseconds());
    }

    public Date removeFrom(Date date) {
        return new Date(date.getTime() - getMilliseconds());
    }

    @Override
    public double getValueAsDefaultUnit() {
        return getValueAs(TimeUnit.SECONDS);
    }
}
