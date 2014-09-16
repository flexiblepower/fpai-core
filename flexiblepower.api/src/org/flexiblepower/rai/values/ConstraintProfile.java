package org.flexiblepower.rai.values;

import java.util.ArrayList;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Quantity;

/**
 * This class is derived from {@link Profile} and is parameterized with a {@link Constraint}.
 */
public final class ConstraintProfile<Q extends Quantity> extends Profile<Constraint<Q>> {
    /**
     * The Builder object that will be used to easily create {@link ConstraintProfile}s.
     */
    public static final class Builder<Q extends Quantity> {
        private final List<Element<Constraint<Q>>> elements;
        private Measurable<Duration> duration;

        Builder() {
            elements = new ArrayList<Element<Constraint<Q>>>();
        }

        /**
         * Set the duration and saves it for future creation of elements.
         *
         * @param duration
         *            The duration for the element
         * @return This builder
         */
        public Builder<Q> duration(Measurable<Duration> duration) {
            this.duration = duration;
            return this;
        }

        /**
         * @param constraint
         *            The {@link Constraint} that will be used (together with the set duration) to add a new element.
         * @return This builder
         * @throws IllegalArgumentException
         *             when the duration has not been set using {@link #duration(Measurable)}
         */
        public Builder<Q> add(Constraint<Q> constraint) {
            if (duration == null) {
                throw new IllegalArgumentException("duration not set");
            }
            return add(new Element<Constraint<Q>>(duration, constraint));
        }

        /**
         * @param element
         *            The element that needs to be added.
         * @return This builder
         */
        public Builder<Q> add(Element<Constraint<Q>> element) {
            elements.add(element);
            return this;
        }

        @SuppressWarnings("unchecked")
        public ConstraintProfile<Q> build() {
            return new ConstraintProfile<Q>(elements.toArray(new Element[elements.size()]));
        }
    }

    /**
     * @return A builder object to easily create new {@link ConstraintProfile}s.
     */
    public static <Q extends Quantity> Builder<Q> create() {
        return new Builder<Q>();
    }

    /**
     * Constructor of the {@link ConstraintProfile}, using the elements given. The elements will be copied into a new
     * array.
     *
     * @param elements
     *            The elements that are stored in this profile
     */
    public ConstraintProfile(Element<Constraint<Q>>... elements) {
        super(elements);

        // Check if profile is empty
        if (elements.length == 0) {
            throw new IllegalArgumentException("A CommodityProfile cannot be empty");
        }
    }

    @Override
    public ConstraintProfile<Q> subProfile(Measurable<Duration> offset, Measurable<Duration> duration) {
        // TODO Needs to be implemented
        throw new UnsupportedOperationException();
    }
}
