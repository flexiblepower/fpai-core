package org.flexiblepower.ral.values;

import java.util.AbstractList;
import java.util.Arrays;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

/**
 * Throughout this specification different profile types are being used. These profiles are all derived from this
 * generic {@link Profile} class.
 *
 * @param <T>
 *            The type of the values stored in the elements
 */
public abstract class Profile<T> extends AbstractList<Profile.Element<T>> {
    /**
     * Represents each element in the profile.
     *
     * @param <T>
     *            The type of the value stored
     */
    public final static class Element<T> {
        private final Measurable<Duration> duration;
        private final T value;

        public Element(Measurable<Duration> duration, T value) {
            this.duration = duration;
            this.value = value;
        }

        /**
         * @return The duration of this element
         */
        public Measurable<Duration> getDuration() {
            return duration;
        }

        /**
         * @return The value corresponding to this element
         */
        public T getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "(" + value + " for " + duration + ")";
        }
    }

    protected final Element<T>[] elements;

    protected Profile(Element<T>... elements) {
        this.elements = Arrays.copyOf(elements, elements.length);
    }

    @Override
    public Element<T> get(int index) {
        return elements[index];
    }

    @Override
    public int size() {
        return elements.length;
    }

    /**
     * @return The total duration of the whole profile. This sums all the durations of the elements.
     */
    public Measurable<Duration> getTotalDuration() {
        double total = 0;
        for (final Element<T> e : elements) {
            total += e.getDuration().doubleValue(Duration.UNIT);
        }
        return Measure.valueOf(total, Duration.UNIT);
    }

    /**
     * Find the element in the {@link Profile} at a specific offset
     *
     * @param offset
     *            Measurable&lt;Duration&gt; of the offset to search for an element
     * @return The element of the profile at offset or null if the offset is bigger than the profile
     */
    public Element<T> getElementAtOffset(Measurable<Duration> offset) {
        Unit<Duration> MS = SI.MILLI(SI.SECOND);
        if (offset.longValue(MS) < 0) {
            throw new IllegalArgumentException("Offset connot be negativeF");
        }
        long offsetMs = offset.longValue(MS);
        for (Element<T> e : elements) {
            long elementDuration = e.getDuration().longValue(MS);
            if (offsetMs <= elementDuration) {
                return e;
            } else {
                offsetMs -= elementDuration;
            }
        }
        return null;
    }

    public abstract Profile<T> subProfile(Measurable<Duration> offset, Measurable<Duration> duration);
}
