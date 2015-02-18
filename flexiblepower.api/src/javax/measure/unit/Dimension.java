/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2006 - JScience (http://jscience.org/)
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package javax.measure.unit;

import java.io.Serializable;

import javax.measure.converter.RationalConverter;
import javax.measure.converter.UnitConverter;
import javax.measure.quantity.Dimensionless;

/**
 * This class represents the dimension of an unit. Two units <code>u1</code> and <code>u2</code> are
 * {@link Unit#isCompatible compatible} if and only if <code>(u1.getDimension().equals(u2.getDimension())))</code>
 *
 * Instances of this class are immutable.
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 3.1, April 22, 2006
 * @see <a href="http://en.wikipedia.org/wiki/Dimensional_analysis"> Wikipedia: Dimensional Analysis</a>
 */
public final class Dimension implements Serializable {

    /**
     * Holds the current physical model.
     */
    private static Model currentModel = Model.STANDARD;

    /**
     * Holds dimensionless.
     */
    public static final Dimension NONE = new Dimension(Unit.ONE);

    /**
     * Holds length dimension (L).
     */
    public static final Dimension LENGTH = new Dimension('L');

    /**
     * Holds mass dimension (M).
     */
    public static final Dimension MASS = new Dimension('M');

    /**
     * Holds time dimension (T).
     */
    public static final Dimension TIME = new Dimension('T');

    /**
     * Holds electric current dimension (I).
     */
    public static final Dimension ELECTRIC_CURRENT = new Dimension('I');

    /**
     * Holds temperature dimension (θ).
     */
    public static final Dimension TEMPERATURE = new Dimension('θ');

    /**
     * Holds amount of substance dimension (N).
     */
    public static final Dimension AMOUNT_OF_SUBSTANCE = new Dimension('N');

    /**
     * Holds the pseudo unit associated to this dimension.
     */
    private final Unit<?> pseudoUnit;

    /**
     * Creates a new dimension associated to the specified symbol.
     *
     * @param symbol
     *            the associated symbol.
     */
    public Dimension(char symbol) {
        pseudoUnit = new BaseUnit<Dimensionless>("[" + symbol + "]");
    }

    /**
     * Creates a dimension having the specified pseudo-unit (base unit or product of base unit).
     *
     * @param pseudoUnit
     *            the pseudo-unit identifying this dimension.
     */
    private Dimension(Unit<?> pseudoUnit) {
        this.pseudoUnit = pseudoUnit;
    }

    /**
     * Returns the product of this dimension with the one specified.
     *
     * @param that
     *            the dimension multiplicand.
     * @return <code>this * that</code>
     */
    public Dimension times(Dimension that) {
        return new Dimension(pseudoUnit.times(that.pseudoUnit));
    }

    /**
     * Returns the quotient of this dimension with the one specified.
     *
     * @param that
     *            the dimension divisor.
     * @return <code>this / that</code>
     */
    public Dimension divide(Dimension that) {
        return new Dimension(pseudoUnit.divide(that.pseudoUnit));
    }

    /**
     * Returns this dimension raised to an exponent.
     *
     * @param n
     *            the exponent.
     * @return the result of raising this dimension to the exponent.
     */
    public Dimension pow(int n) {
        return new Dimension(pseudoUnit.pow(n));
    }

    /**
     * Returns the given root of this dimension.
     *
     * @param n
     *            the root's order.
     * @return the result of taking the given root of this dimension.
     * @throws ArithmeticException
     *             if <code>n == 0</code>.
     */
    public Dimension root(int n) {
        return new Dimension(pseudoUnit.root(n));
    }

    /**
     * Returns the representation of this dimension.
     *
     * @return the representation of this dimension.
     */
    @Override
    public String toString() {
        return pseudoUnit.toString();
    }

    /**
     * Indicates if the specified dimension is equals to the one specified.
     *
     * @param that
     *            the object to compare to.
     * @return <code>true</code> if this dimension is equals to that dimension; <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        return (that instanceof Dimension) && pseudoUnit.equals(((Dimension) that).pseudoUnit);
    }

    /**
     * Returns the hash code for this dimension.
     *
     * @return this dimension hashcode value.
     */
    @Override
    public int hashCode() {
        return pseudoUnit.hashCode();
    }

    /**
     * Sets the model used to determinate the units dimensions.
     *
     * @param model
     *            the new model to be used when calculating unit dimensions.
     */
    public static void setModel(Model model) {
        Dimension.currentModel = model;
    }

    /**
     * Returns the model used to determinate the units dimensions (default {@link Model#STANDARD STANDARD}).
     *
     * @return the model used when calculating unit dimensions.
     */
    public static Model getModel() {
        return Dimension.currentModel;
    }

    /**
     * This interface represents the mapping between {@link BaseUnit base units} and {@link Dimension dimensions}.
     * Custom models may allow conversions not possible using the {@link #STANDARD standard} model. For example:
     *
     * <pre>
     * public static void main(String[] args) {
     *     Dimension.Model relativistic = new Dimension.Model() { 
     *         RationalConverter meterToSecond = new RationalConverter(1, 299792458); // 1/c
     *         
     *         public Dimension getDimension(BaseUnit unit) { 
     *             if (unit.equals(SI.METER)) return Dimension.TIME; 
     *             else return Dimension.Model.STANDARD.getDimension(unit); 
     *         }
     * 
     *         public UnitConverter getTransform(BaseUnit unit) { 
     *             if (unit.equals(SI.METER)) return meterToSecond; 
     *             else return Dimension.Model.STANDARD.getTransform(unit); 
     *         }
     *     };
     *     
     *     Dimension.setModel(relativistic);
     * 
     *     // Converts 1.0 GeV (energy) to kg (mass).
     *     System.out.println(Unit.valueOf("GeV").getConverterTo(KILOGRAM).convert(1.0)); 
     * }
     * 
     * &gt; 1.7826617302520883E-27
     * </pre>
     */
    public interface Model {

        /**
         * Holds the standard model (default).
         */
        Model STANDARD = new Model() {
            @Override
            public Dimension getDimension(BaseUnit<?> unit) {
                if (unit.equals(SI.METRE)) {
                    return Dimension.LENGTH;
                }
                if (unit.equals(SI.KILOGRAM)) {
                    return Dimension.MASS;
                }
                if (unit.equals(SI.KELVIN)) {
                    return Dimension.TEMPERATURE;
                }
                if (unit.equals(SI.SECOND)) {
                    return Dimension.TIME;
                }
                if (unit.equals(SI.AMPERE)) {
                    return Dimension.ELECTRIC_CURRENT;
                }
                if (unit.equals(SI.MOLE)) {
                    return Dimension.AMOUNT_OF_SUBSTANCE;
                }
                if (unit.equals(SI.CANDELA)) {
                    return SI.WATT.getDimension();
                }
                return new Dimension(new BaseUnit<Dimensionless>("[" + unit.getSymbol() + "]"));
            }

            @Override
            public UnitConverter getTransform(BaseUnit<?> unit) {
                if (unit.equals(SI.CANDELA)) {
                    return new RationalConverter(1, 683);
                }
                return UnitConverter.IDENTITY;
            }
        };

        /**
         * Returns the dimension of the specified base unit (a dimension particular to the base unit if the base unit is
         * not recognized).
         *
         * @param unit
         *            the base unit for which the dimension is returned.
         * @return the dimension of the specified unit.
         */
        Dimension getDimension(BaseUnit<?> unit);

        /**
         * Returns the normalization transform of the specified base unit ({@link UnitConverter#IDENTITY IDENTITY} if
         * the base unit is not recognized).
         *
         * @param unit
         *            the base unit for which the transform is returned.
         * @return the normalization transform.
         */
        UnitConverter getTransform(BaseUnit<?> unit);
    }

    private static final long serialVersionUID = 1L;
}
