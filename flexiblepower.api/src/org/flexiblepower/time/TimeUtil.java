package org.flexiblepower.time;

import static javax.measure.unit.SI.MILLI;
import static javax.measure.unit.SI.SECOND;

import java.util.Date;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.unit.Unit;

/**
 * This is a utility class for calculations between <code>Date</code> objects and
 * <code>Measurable&lt;Duration&gt;</code> objects.
 */
public final class TimeUtil {
    /**
     * Represents Milliseconds.
     */
    public static final Unit<Duration> MS = MILLI(SECOND);
    /**
     * Represents a duration with no length.
     */
    public static final Measurable<Duration> ZERO = Measure.zero();

    private TimeUtil() {
    }

    /**
     * Adds a duration to a date to get another {@link Date} object.
     * 
     * @param startDate
     *            The starting time
     * @param duration
     *            The duration to add
     * @return A new {@link Date} object that represents the time that is <code>duration</code> after the
     *         <code>startDate</code>.
     * @throws NullPointerException
     *             when any of the parameters is <code>null</code>
     */
    public static Date add(Date startDate, Measurable<Duration> duration) {
        long ms = duration.longValue(MS);
        return new Date(startDate.getTime() + ms);
    }

    /**
     * Subtracts a duration from a date to get another {@link Date} object.
     * 
     * @param startDate
     *            The starting time
     * @param duration
     *            The duration to subtract
     * @return A new {@link Date} object that represents the time that is <code>duration</code> before the
     *         <code>startDate</code>.
     * @throws NullPointerException
     *             when any of the parameters is <code>null</code>
     */
    public static Date subtract(Date startDate, Measurable<Duration> duration) {
        long ms = duration.longValue(MS);
        return new Date(startDate.getTime() - ms);
    }

    /**
     * Calculates the difference between 2 dates.
     * 
     * @param startDate
     *            The starting date
     * @param endDate
     *            The ending date
     * @return A new {@link Measurable} object that represents the difference between the 2 dates.
     * @throws NullPointerException
     *             when any of the parameters is <code>null</code>
     */
    public static Measurable<Duration> difference(Date startDate, Date endDate) {
        long end = endDate.getTime();
        long start = startDate.getTime();
        return Measure.valueOf(end - start, MS);
    }
}
