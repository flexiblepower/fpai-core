package org.flexiblepower.rai.values;

import java.util.ArrayList;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Power;
import javax.measure.quantity.VolumetricFlowRate;

/**
 * This class is derived from {@link Profile}. By parameterizing this class with the billable quantity (BQ) and the flow
 * quantity (FQ) it can be validated whether all commodity profile elements are of the same commodity. CommodityProfile
 * does not have any additional attributes.
 */
public final class CommodityProfile extends Profile<CommodityMeasurables> {
    /**
     * The Builder object that will be used to easily create {@link CommodityProfile}s.
     */
    public static final class Builder {
        private final List<Element<CommodityMeasurables>> elements;
        private Measurable<Duration> duration;
        private Measurable<Power> electricityValue, heatValue;
        private Measurable<VolumetricFlowRate> gasValue;

        Builder() {
            elements = new ArrayList<Element<CommodityMeasurables>>();
        }

        /**
         * Set the duration and saves it for future creation of elements.
         *
         * @param duration
         *            The duration for the element
         * @return This builder
         * @see #next()
         */
        public Builder duration(Measurable<Duration> duration) {
            this.duration = duration;
            return this;
        }

        /**
         * Set the electricity value and saves it for future creation of elements.
         *
         * @param value
         *            The electricity value for the element
         * @return This builder
         * @see #next()
         */
        public Builder electricity(Measurable<Power> value) {
            electricityValue = value;
            return this;
        }

        /**
         * Set the gas value and saves it for future creation of elements.
         *
         * @param value
         *            The gas value for the element
         * @return This builder
         * @see #next()
         */
        public Builder gas(Measurable<VolumetricFlowRate> value) {
            gasValue = value;
            return this;
        }

        /**
         * Set the heat value and saves it for future creation of elements.
         *
         * @param value
         *            The heat value for the element
         * @return This builder
         * @see #next()
         */
        public Builder heat(Measurable<Power> value) {
            heatValue = value;
            return this;
        }

        /**
         * Uses the values as set by the {@link #duration(Measurable)}, {@link #electricity(Measurable)},
         * {@link #gas(Measurable)} and {@link #heat(Measurable)} methods to create a new element. This does not reset
         * the values, so if you call this method again it will create a second element that is equal to the first.
         *
         * @return This builder
         * @throws IllegalArgumentException
         *             when the duration has not been set using {@link #duration(Measurable)}
         */
        public Builder next() {
            if (duration == null) {
                throw new IllegalArgumentException("duration not set");
            }
            elements.add(new CommodityProfile.Element<CommodityMeasurables>(duration,
                                                                           new CommodityMeasurables(electricityValue,
                                                                                                   gasValue,
                                                                                                   heatValue)));
            return this;
        }

        /**
         * @param element
         *            The element that needs to be added.
         * @return This builder
         */
        public Builder add(Element<CommodityMeasurables> element) {
            elements.add(element);
            return this;
        }

        /**
         * @param commodityMeasurable
         *            The {@link CommodityMeasurables} that will be used (together with the set duration) to add a new
         *            element.
         * @return This builder
         * @throws IllegalArgumentException
         *             when the duration has not been set using {@link #duration(Measurable)}
         */
        public Builder add(CommodityMeasurables commodityMeasurable) {
            if (duration == null) {
                throw new IllegalArgumentException("duration not set");
            }
            elements.add(new Element<CommodityMeasurables>(duration, commodityMeasurable));
            return this;
        }

        @SuppressWarnings("unchecked")
        public CommodityProfile build() {
            return new CommodityProfile(elements.toArray(new Element[elements.size()]));
        }
    }

    /**
     * @return A builder object to easily create new {@link CommodityProfile}s.
     */
    public static Builder create() {
        return new Builder();
    }

    /**
     * Constructor of the {@link CommodityProfile}, using the elements given. The elements will be copied into a new
     * array.
     * 
     * @param elements
     *            The elements that are stored in this profile
     */
    public CommodityProfile(Element<CommodityMeasurables>... elements) {
        super(elements);

        // Check if profile is empty
        if (elements.length == 0) {
            throw new IllegalArgumentException("A CommodityProfile cannot be empty");
        }
        // Check if all the commodities are the same
        final CommoditySet set = elements[0].getValue().keySet();
        for (int i = 1; i < elements.length; i++) {
            if (!elements[i].getValue().keySet().equals(set)) {
                throw new IllegalArgumentException("A CommodityProfile can only consist of commodites of the same type");
            }
        }
    }

    /**
     * @return The set of commodities that are supported in this profile
     */
    public CommoditySet getCommodities() {
        return elements[0].getValue().keySet();
    }

    @Override
    public Profile<CommodityMeasurables> subProfile(Measurable<Duration> offset, Measurable<Duration> duration) {
        // TODO Needs to be implemented
        throw new UnsupportedOperationException();
    }
}
