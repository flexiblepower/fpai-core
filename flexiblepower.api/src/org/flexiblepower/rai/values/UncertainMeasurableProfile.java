package org.flexiblepower.rai.values;

import java.util.ArrayList;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

/**
 * This class is derived from {@link Profile}. By parameterizing this class with the billable quantity (BQ) and the flow
 * quantity (FQ) it can be validated whether all commodity profile elements are of the same commodity. CommodityProfile
 * does not have any additional attributes.
 */
public final class UncertainMeasurableProfile<Q extends Quantity> extends Profile<UncertainMeasure<Q>> {
    /**
     * The Builder object that will be used to easily create {@link UncertainMeasurableProfile}s.
     */
    public static final class Builder<Q extends Quantity> {
        private final List<Element<UncertainMeasure<Q>>> elements;
        private Measurable<Duration> duration;
        private final Unit<Q> unit;

        Builder(Unit<Q> unit) {
            elements = new ArrayList<Element<UncertainMeasure<Q>>>();
            this.unit = unit;
        }

        /**
         * Set the duration and saves it for future creation of elements.
         *
         * @param duration
         *            The duration for the element
         * @return This builder
         * @see #add(UncertainMeasure)
         */
        public Builder<Q> duration(Measurable<Duration> duration) {
            this.duration = duration;
            return this;
        }

        public Builder<Q> add(double mean, double standardDeviation) {
            return add(new UncertainMeasure<Q>(mean, standardDeviation, unit));
        }

        public Builder<Q> add(double the95pprLowerBound,
                              double the68pprLowerBound,
                              double mean,
                              double the68pprUpperBound,
                              double the95pprUpperBound) {
            return add(new UncertainMeasure<Q>(the95pprLowerBound,
                                               the68pprLowerBound,
                                               mean,
                                               the68pprUpperBound,
                                               the95pprUpperBound,
                                               unit));
        }

        public Builder<Q> add(UncertainMeasure<Q> measure) {
            elements.add(new Element<UncertainMeasure<Q>>(duration, measure));
            return this;
        }

        @SuppressWarnings("unchecked")
        public UncertainMeasurableProfile<Q> build() {
            return new UncertainMeasurableProfile<Q>(elements.toArray(new Element[elements.size()]));
        }
    }

    /**
     * @return A builder object to easily create new {@link UncertainMeasurableProfile}s.
     */
    public static <Q extends Quantity> Builder<Q> create(Unit<Q> defaultUnit) {
        return new Builder<Q>(defaultUnit);
    }

    /**
     * Constructor of the {@link UncertainMeasurableProfile}, using the elements given. The elements will be copied into
     * a new array.
     *
     * @param elements
     *            The elements that are stored in this profile
     */
    public UncertainMeasurableProfile(Element<UncertainMeasure<Q>>... elements) {
        super(elements);

        // Check if profile is empty
        if (elements.length == 0) {
            throw new IllegalArgumentException("A CommodityProfile cannot be empty");
        }
    }

    @Override
    public Profile<UncertainMeasure<Q>> subProfile(Measurable<Duration> offset, Measurable<Duration> duration) {
        // TODO Needs to be implemented
        throw new UnsupportedOperationException();
    }
}
