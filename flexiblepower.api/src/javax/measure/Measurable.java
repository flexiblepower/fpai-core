/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2007 - JScience (http://jscience.org/)
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package javax.measure;

import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

/**
 * This interface represents the measurable, countable, or comparable property or aspect of a thing.
 *
 * Implementing instances are typically the result of a measurement:
 *
 * <pre>
 * Measurable&lt;Mass&gt; weight = Measure.valueOf(180.0, POUND);
 * </pre>
 *
 * They can also be created from custom classes:
 *
 * <pre>
 * class Delay implements Measurable&lt;Duration&gt; {
 *     private long nanoSeconds; // Implicit internal unit.
 *     public double doubleValue(Unit&lt;Velocity&gt; unit) { ... }
 *     public long longValue(Unit&lt;Velocity&gt; unit) { ... }
 * }
 * 
 * Thread.wait(new Delay(24, HOUR)); // Assuming Thread.wait(Measurable&lt;Duration&gt;) method.
 * </pre>
 *
 * Although measurable instances are for the most part scalar quantities; more complex implementations (e.g. vectors,
 * data set) are allowed as long as an aggregate magnitude can be determined. For example:
 *
 * <pre>
 * class Velocity3D implements Measurable&lt;Velocity&gt; {
 *     private double x, y, z; // Meter per seconds.
 *     public double doubleValue(Unit&lt;Velocity&gt; unit) { ... } // Returns vector norm.
 *     ...
 * }
 * 
 * class Sensors&lt;Q extends Quantity&gt; extends Measure&lt;double[], Q&gt; {
 *     public doubleValue(Unit&lt;Q&gt; unit) { ... } // Returns median value.
 *     ...
 * }
 * </pre>
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 4.1, June 8, 2007
 */
public interface Measurable<Q extends Quantity> extends Comparable<Measurable<Q>> {

    /**
     * Returns the value of this measurable stated in the specified unit as a <code>double</code>. If the measurable has
     * too great a magnitude to be represented as a <code>double</code>, it will be converted to
     * <code>Double.NEGATIVE_INFINITY</code> or <code>Double.POSITIVE_INFINITY</code> as appropriate.
     *
     * @param unit
     *            the unit in which this measurable value is stated.
     * @return the numeric value after conversion to type <code>double</code>.
     */
    double doubleValue(Unit<Q> unit);

    /**
     * Returns the estimated integral value of this measurable stated in the specified unit as a <code>long</code>.
     *
     * <p>
     * Note: This method differs from the <code>Number.longValue()</code> in the sense that the closest integer value is
     * returned and an ArithmeticException is raised instead of a bit truncation in case of overflow (safety critical).
     * </p>
     *
     * @param unit
     *            the unit in which the measurable value is stated.
     * @return the numeric value after conversion to type <code>long</code>.
     * @throws ArithmeticException
     *             if this quantity cannot be represented as a <code>long</code> number in the specified unit.
     */
    long longValue(Unit<Q> unit) throws ArithmeticException;

    /**
     * Adds the other Measurable to this one, returning a Measurable object of the same type as the original.
     *
     * @param other
     *            The measurable that should be added to this one.
     * @return A new measurable that contains the summed up value of this and the other one.
     */
    Measurable<Q> add(Measurable<Q> other);
}
