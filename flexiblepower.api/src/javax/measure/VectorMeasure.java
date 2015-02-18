/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2007 - JScience (http://jscience.org/)
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package javax.measure;

import javax.measure.converter.UnitConverter;
import javax.measure.quantity.Quantity;
import javax.measure.unit.CompoundUnit;
import javax.measure.unit.Unit;

/**
 * This class represents a measurement vector of two or more dimensions. For example:
 *
 * <pre>
 * VectorMeasure&lt;Length&gt; dimension = VectorMeasure.valueOf(12.0, 30.0, 40.0, MILLIMETER);
 * VectorMeasure&lt;Velocity&gt; v2d = VectorMeasure.valueOf(-2.2, -3.0, KNOTS);
 * VectorMeasure&lt;ElectricCurrent&gt; c2d = VectorMeasure.valueOf(-7.3, 3.5, NANOAMPERE);
 * </pre>
 * 
 * Subclasses may provide fixed dimensions specializations:
 * 
 * <pre>
 * class Velocity2D extends VectorMeasure&lt;Velocity&gt; {
 *     public Velocity2D(double x, double y, Unit&lt;Velocity&gt; unit) { ... }
 * }
 * 
 * </pre>
 *
 * Measurement vectors may use {@link CompoundUnit compound units}:
 *
 * <pre>
 * VectorMeasure&lt;Angle&gt; latLong = VectorMeasure.valueOf(12.345, 22.23, DEGREE_ANGLE);
 * Unit&lt;Angle&gt; HOUR_MINUTE_SECOND_ANGLE = DEGREE_ANGLE.compound(MINUTE_ANGLE).compound(SECOND_ANGLE);
 * System.out.println(latLong.to(HOUR_MINUTE_SECOND_ANGLE));
 *
 * &gt; [12°19'42", 22°12'48"]
 * </pre>
 * 
 * Instances of this class (and sub-classes) are immutable.
 *
 * @param <Q>
 *            The quantity of the vector (e.g. Power)
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 4.3, October 3, 2007
 */
public abstract class VectorMeasure<Q extends Quantity> extends Measure<double[], Q> {
    private static final long serialVersionUID = -6559201419685544465L;

    /**
     * Default constructor (for sub-classes).
     */
    protected VectorMeasure() {
    }

    @Override
    public Measurable<Q> add(Measurable<Q> other) {
        if (other instanceof VectorMeasure) {
            VectorMeasure<Q> otherVector = (VectorMeasure<Q>) other;
            double[] left = getValue();
            double[] right = otherVector.to(getUnit()).getValue();
            if (left.length != right.length) {
                throw new UnsupportedOperationException("The 2 vectors have different lengths");
            }
            double[] result = new double[left.length];
            for (int ix = 0; ix < result.length; ix++) {
                result[ix] = left[ix] + right[ix];
            }
            return valueOf(result, getUnit());
        } else {
            return Measure.valueOf(doubleValue(getUnit()) + other.doubleValue(getUnit()), getUnit());
        }
    }

    /**
     * Returns a 2-dimensional measurement vector.
     *
     * @param x
     *            the first vector component value.
     * @param y
     *            the second vector component value.
     * @param unit
     *            the measurement unit.
     */
    public static <Q extends Quantity> VectorMeasure<Q> valueOf(double x, double y, Unit<Q> unit) {
        return new TwoDimensional<Q>(x, y, unit);
    }

    /**
     * Returns a 3-dimensional measurement vector.
     *
     * @param x
     *            the first vector component value.
     * @param y
     *            the second vector component value.
     * @param z
     *            the third vector component value.
     * @param unit
     *            the measurement unit.
     */
    public static <Q extends Quantity> VectorMeasure<Q> valueOf(double x, double y, double z, Unit<Q> unit) {
        return new ThreeDimensional<Q>(x, y, z, unit);
    }

    /**
     * Returns a multi-dimensional measurement vector.
     *
     * @param components
     *            the vector component values.
     * @param unit
     *            the measurement unit.
     */
    public static <Q extends Quantity> VectorMeasure<Q> valueOf(double[] components, Unit<Q> unit) {
        return new MultiDimensional<Q>(components, unit);
    }

    /**
     * Returns the measurement vector equivalent to this one but stated in the specified unit.
     *
     * @param unit
     *            the new measurement unit.
     * @return the vector measure stated in the specified unit.
     */
    @Override
    public abstract VectorMeasure<Q> to(Unit<Q> unit);

    /**
     * Returns the norm of this measurement vector stated in the specified unit.
     *
     * @param unit
     *            the unit in which the norm is stated.
     * @return <code>|this|</code>
     */
    @Override
    public abstract double doubleValue(Unit<Q> unit);

    /**
     * Returns the <code>String</code> representation of this measurement vector (for example
     * <code>[2.3 m/s, 5.6 m/s]</code>).
     *
     * @return the textual representation of the measurement vector.
     */
    @Override
    public String toString() {
        double[] values = getValue();
        Unit<Q> unit = getUnit();
        StringBuffer tmp = new StringBuffer();
        tmp.append('[');
        for (double v : values) {
            if (tmp.length() > 1) {
                tmp.append(", ");
            }
            if (unit instanceof CompoundUnit) {
                MeasureFormat.DEFAULT.formatCompound(v, unit, tmp, null);
            } else {
                tmp.append(v).append(" ").append(unit);
            }
        }
        tmp.append("] ");
        return tmp.toString();
    }

    // Holds 2-dimensional implementation.
    private static final class TwoDimensional<Q extends Quantity> extends VectorMeasure<Q> {

        private final double x;

        private final double y;

        private final Unit<Q> unit;

        private TwoDimensional(double x, double y, Unit<Q> unit) {
            this.x = x;
            this.y = y;
            this.unit = unit;
        }

        @Override
        public double doubleValue(Unit<Q> unit) {
            double norm = Math.sqrt(this.x * this.x + y * y);
            if ((this.unit == unit) || (this.unit.equals(unit))) {
                return norm;
            }
            return this.unit.getConverterTo(unit).convert(norm);
        }

        @Override
        public Unit<Q> getUnit() {
            return unit;
        }

        @Override
        public double[] getValue() {
            return new double[] { this.x, y };
        }

        @Override
        public TwoDimensional<Q> to(Unit<Q> unit) {
            if ((this.unit == unit) || (this.unit.equals(unit))) {
                return this;
            }
            UnitConverter cvtr = this.unit.getConverterTo(unit);
            return new TwoDimensional<Q>(cvtr.convert(this.x), cvtr.convert(y), unit);
        }

        private static final long serialVersionUID = 1L;

    }

    // Holds 3-dimensional implementation.
    private static final class ThreeDimensional<Q extends Quantity> extends VectorMeasure<Q> {

        private final double x;

        private final double y;

        private final double z;

        private final Unit<Q> unit;

        private ThreeDimensional(double x, double y, double z, Unit<Q> unit) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.unit = unit;
        }

        @Override
        public double doubleValue(Unit<Q> unit) {
            double norm = Math.sqrt(x * x + y * y + z * z);
            if ((this.unit == unit) || (this.unit.equals(unit))) {
                return norm;
            }
            return this.unit.getConverterTo(unit).convert(norm);
        }

        @Override
        public Unit<Q> getUnit() {
            return unit;
        }

        @Override
        public double[] getValue() {
            return new double[] { x, y, z };
        }

        @Override
        public ThreeDimensional<Q> to(Unit<Q> unit) {
            if ((this.unit == unit) || (this.unit.equals(unit))) {
                return this;
            }
            UnitConverter cvtr = this.unit.getConverterTo(unit);
            return new ThreeDimensional<Q>(cvtr.convert(x), cvtr.convert(y), cvtr.convert(z), unit);
        }

        private static final long serialVersionUID = 1L;

    }

    // Holds multi-dimensional implementation.
    private static final class MultiDimensional<Q extends Quantity> extends VectorMeasure<Q> {

        private final double[] components;

        private final Unit<Q> unit;

        private MultiDimensional(double[] components, Unit<Q> unit) {
            this.components = components.clone();
            this.unit = unit;
        }

        @Override
        public double doubleValue(final Unit<Q> unit) {
            double normSquare = this.components[0] * this.components[0];
            for (int i = 1, n = this.components.length; i < n;) {
                double d = this.components[i++];
                normSquare += d * d;
            }
            if ((unit == this.unit) || (unit.equals(this.unit))) {
                return Math.sqrt(normSquare);
            }
            return this.unit.getConverterTo(unit).convert(Math.sqrt(normSquare));
        }

        @Override
        public Unit<Q> getUnit() {
            return this.unit;
        }

        @Override
        public double[] getValue() {
            return this.components.clone();
        }

        @Override
        public MultiDimensional<Q> to(Unit<Q> unit) {
            if ((unit == this.unit) || (unit.equals(this.unit))) {
                return this;
            }
            UnitConverter cvtr = this.unit.getConverterTo(unit);
            double[] newValues = new double[this.components.length];
            for (int i = 0; i < this.components.length; i++) {
                newValues[i] = cvtr.convert(this.components[i]);
            }
            return new MultiDimensional<Q>(newValues, unit);
        }

        private static final long serialVersionUID = 1L;

    }
}
