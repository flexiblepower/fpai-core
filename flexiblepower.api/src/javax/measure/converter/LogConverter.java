/*
 * JScience - Java(TM) Tools and Libraries for the Advancement of Sciences.
 * Copyright (C) 2006 - JScience (http://jscience.org/)
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package javax.measure.converter;

/**
 * This class represents a logarithmic converter. Such converter is typically used to create logarithmic unit. For
 * example:
 *
 * <pre>
 * Unit&lt;Dimensionless&gt; BEL = Unit.ONE.transform(new LogConverter(10).inverse());
 * </pre>
 *
 * Instances of this class are immutable.
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @version 3.1, April 22, 2006
 */
public final class LogConverter extends UnitConverter {

    /**
     * Holds the logarithmic base.
     */
    private final double base;

    /**
     * Holds the natural logarithm of the base.
     */
    private final double logBase;

    /**
     * Holds the inverse of the natural logarithm of the base.
     */
    private final double invLogBase;

    /**
     * Holds the inverse of this converter.
     */
    private final Inverse inverse = new Inverse();

    /**
     * Creates a logarithmic converter having the specified base.
     *
     * @param base
     *            the logarithmic base (e.g. <code>Math.E</code> for the Natural Logarithm).
     */
    public LogConverter(double base) {
        this.base = base;
        logBase = Math.log(base);
        invLogBase = 1.0 / logBase;
    }

    /**
     * Returns the logarithmic base of this converter.
     *
     * @return the logarithmic base (e.g. <code>Math.E</code> for the Natural Logarithm).
     */
    public double getBase() {
        return base;
    }

    @Override
    public UnitConverter inverse() {
        return inverse;
    }

    @Override
    public double convert(double amount) {
        return invLogBase * Math.log(amount);
    }

    @Override
    public boolean isLinear() {
        return false;
    }

    /**
     * This inner class represents the inverse of the logarithmic converter (exponentiation converter).
     */
    private class Inverse extends UnitConverter {
        @Override
        public UnitConverter inverse() {
            return LogConverter.this;
        }

        @Override
        public double convert(double amount) {
            return Math.exp(logBase * amount);
        }

        @Override
        public boolean isLinear() {
            return false;
        }

        private static final long serialVersionUID = 1L;
    }

    private static final long serialVersionUID = 1L;
}
