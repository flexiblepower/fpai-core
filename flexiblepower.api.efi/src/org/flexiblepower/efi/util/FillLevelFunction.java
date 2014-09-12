package org.flexiblepower.efi.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.flexiblepower.efi.util.FillLevelFunction.RangeElement;

/**
 * This class approximates a function where the value is dependent on the fill level of the buffer. It implements a
 * {@link List} of {@link RangeElement}s and can be used in that manner.
 *
 * @param <T>
 *            The type of value that is stored at each element
 */
public class FillLevelFunction<T> extends AbstractList<RangeElement<T>> {
    /**
     * @param lowerBound
     *            The lowest bound
     * @return a new {@link Builder} object that can be used to easily create the {@link FillLevelFunction}
     */
    public static <T> Builder<T> create(double lowerBound) {
        return new Builder<T>(lowerBound);
    }

    /**
     * This helper class should be used to easily define a {@link FillLevelFunction}. See the
     * {@link FillLevelFunction#create(double)} method to get a new instance of this class.
     *
     * @param <T>
     *            The type of the value that is stored at each element
     */
    public static class Builder<T> {
        private double lowerBound;
        private final List<RangeElement<T>> elements;

        Builder(double lowerBound) {
            this.lowerBound = lowerBound;
            elements = new ArrayList<RangeElement<T>>();
        }

        /**
         * Adds a new element to the {@link FillLevelFunction} that we are creating. It will use the upperBound of the
         * last element (or the lowerBound that we started with if it does not exist) as the lowerBound for the new
         * element.
         *
         * @param upperBound
         *            The upperBound of the new element. This must always be larger than the lowerBound.
         * @param value
         *            The value that is to be associated with this element
         * @return This {@link Builder}
         */
        public Builder<T> add(double upperBound, T value) {
            elements.add(new RangeElement<T>(lowerBound, upperBound, value));
            lowerBound = upperBound;
            return this;
        }

        /**
         * @return A new immutable {@link FillLevelFunction} object that contains all the elements that have been added
         *         until now.
         */
        public FillLevelFunction<T> build() {
            return new FillLevelFunction<T>(elements);
        }
    }

    /**
     * This class stores the values for each range.
     *
     * @param <T>
     *            The type of the value
     */
    public static class RangeElement<T> {
        // Defines the range of the line
        private final double lowerBound;
        private final double upperBound;

        private final T value;

        RangeElement(double lowerBound, double upperBound, T value) {
            if (value == null) {
                throw new NullPointerException("value");
            } else if (upperBound <= lowerBound) {
                throw new IllegalArgumentException("the upperbound (" + upperBound
                                                   + ") should be higher that the lower bound ("
                                                   + lowerBound
                                                   + ")");
            }

            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            this.value = value;
        }

        /**
         * @return The lowerBound for which the value is a valid approximation of the function
         */
        public double getLowerBound() {
            return lowerBound;
        }

        /**
         * @return The upperBound for which the value is a valid approximation of the function
         */
        public double getUpperBound() {
            return upperBound;
        }

        /**
         * @return The corresponding value that is valid within the given bounds
         */
        public T getValue() {
            return value;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            long temp;
            temp = Double.doubleToLongBits(lowerBound);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(upperBound);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            result = prime * result + value.hashCode();
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            } else if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            @SuppressWarnings("unchecked")
            RangeElement<T> other = (RangeElement<T>) obj;
            if (Double.doubleToLongBits(lowerBound) != Double.doubleToLongBits(other.lowerBound)) {
                return false;
            } else if (Double.doubleToLongBits(upperBound) != Double.doubleToLongBits(other.upperBound)) {
                return false;
            } else if (!value.equals(other.value)) {
                return false;
            }
            return true;
        }
    }

    protected final RangeElement<T>[] elements;

    @SuppressWarnings("unchecked")
    public FillLevelFunction(List<RangeElement<T>> elements) {
        // Check if not empty
        if (elements.isEmpty()) {
            throw new IllegalArgumentException("A FillLevelFunction should have at least one RangeElement");
        }
        // Check if all elements are connected
        for (int i = 1; i < elements.size(); i++) {
            if (elements.get(i - 1).getUpperBound() != elements.get(i).getLowerBound()) {
                throw new IllegalArgumentException("All RangeElements in a FillLevelFunction should be connected");
            }
        }

        this.elements = elements.toArray(new RangeElement[elements.size()]);
    }

    @Override
    public RangeElement<T> get(int index) {
        return elements[index];
    }

    @Override
    public int size() {
        return elements.length;
    }

    /**
     * @return the lower bound for which this function is valid
     */
    public double getLowerBound() {
        return this.elements[0].getLowerBound();
    }

    /**
     * @return the upper bound for which this function is valid
     */
    public double getUpperBound() {
        return this.elements[elements.length - 1].getUpperBound();
    }

    /**
     * @param fillLevel
     *            The fill level of the buffer for which to get the corresponding value
     * @return the value that corresponds to the fill level
     * @throws IllegalArgumentException
     *             when the fillLevel is outside the bounds (see {@link #getLowerBound()} and {@link #getUpperBound()})
     */
    public T getValueForFillLevel(double fillLevel) {
        return getRangeElementForFillLevel(fillLevel).getValue();
    }

    /**
     * @param fillLevel
     *            The fill level of the buffer for which to get the corresponding element
     * @return the element that corresponds to the fill level
     * @throws IllegalArgumentException
     *             when the fillLevel is outside the bounds (see {@link #getLowerBound()} and {@link #getUpperBound()})
     */
    public RangeElement<T> getRangeElementForFillLevel(double fillLevel) {
        for (RangeElement<T> re : elements) {
            if (fillLevel <= re.getUpperBound()) {
                return re;
            }
        }
        throw new IllegalArgumentException("FillLevel is not in range of the fill level function");
    }
}
