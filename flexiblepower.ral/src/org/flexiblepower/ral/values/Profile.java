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
     * The unit for milliseconds.
     */
    public static final Unit<Duration> MS = SI.MILLI(SI.SECOND);

    /**
     * Represents each element in the profile.
     *
     * @param <T>
     *            The type of the value stored
     */
    public static final class Element<T> {
        private final Measurable<Duration> duration;
        private final T value;

        /**
         * Creates a new instance of element. This is a pair of duration with a value.
         *
         * @param duration
         *            The duration of the {@link Element} in the profile
         * @param value
         *            The value corresponding to that duration
         */
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

    /**
     * The array that contains all the elements.
     */
    protected final Element<T>[] elements;

    /**
     * Creates a new instance of the profile by using the given elements.
     *
     * @param elements
     *            The array of elements (in order!) that make up this profile.
     */
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
     * Find the element in the {@link Profile} at a specific offset.
     *
     * @param offset
     *            Measurable&lt;Duration&gt; of the offset to search for an element
     * @return The element of the profile at offset or null if the offset is bigger than the profile
     */
    public Element<T> getElementAtOffset(Measurable<Duration> offset) {
        if (offset.longValue(MS) < 0) {
            throw new IllegalArgumentException("Offset connot be negative");
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

    /**
     * An extension of the profile should implement this method to be able to split the profile up into parts. This
     * method should select a single part of the whole profile.
     *
     * @param offset
     *            The offset of where the subsection of the profile should start. This should always be >= 0.
     * @param duration
     *            The total duration of the new profile. The offset + duration should never be more than the total
     *            duration of this profile.
     * @return A new {@link Profile} implementation that represents the selected subprofile.
     */
    public abstract Profile<T> subProfile(Measurable<Duration> offset, Measurable<Duration> duration);
}
