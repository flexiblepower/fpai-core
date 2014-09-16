package org.flexiblepower.rai.values;

import java.util.AbstractList;
import java.util.Arrays;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Duration;

/**
 * Throughout this specification different profile types are being used. These profiles are all derived from this
 * generic {@link Profile} class.
 *
 * @param <T>
 *            The type of the values stored in the elements
 * @param <PE>
 *            The type of the {@link ProfileElement}
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

    public abstract Profile<T> subProfile(Measurable<Duration> offset, Measurable<Duration> duration);
}
