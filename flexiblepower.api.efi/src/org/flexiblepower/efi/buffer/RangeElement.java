package org.flexiblepower.efi.buffer;

public class RangeElement {

    // Defines the range of the line
    private final double lowerBound;
    private final double upperBound;

    // Charge speed x / seconds
    private final double xs;

    public RangeElement(double lowerBound, double upperBound, double xs) {
        super();
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.xs = xs;
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public double getUpperBound() {
        return upperBound;
    }

    public double getXs() {
        return xs;
    }

}
