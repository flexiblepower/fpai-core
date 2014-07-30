package org.flexiblepower.efi.buffer;

import java.util.AbstractList;

public abstract class FillLevelFunction<E extends RangeElement> extends AbstractList<E> {

    protected final E[] elements;

    public FillLevelFunction(E[] elements) {
        super();
        this.elements = elements;
        validate();
    }

    private void validate() {
        // Check if not empty
        if (elements.length == 0) {
            throw new IllegalArgumentException("A FillLevelFunction should have at least one RangeElement");
        }
        // Check if all elements are connected
        for (int i = 1; i < elements.length; i++) {
            if (elements[i - 1].getUpperBound() != elements[i].getLowerBound()) {
                throw new IllegalArgumentException("All RangeElements in a FillLevelFunction should be connected");
            }
        }
    }

    @Override
    public E get(int index) {
        return elements[index];
    }

    @Override
    public int size() {
        return elements.length;
    }

    public double getLowerBound() {
        return this.elements[0].getLowerBound();
    }

    public double getUpperBound() {
        return this.elements[elements.length - 1].getUpperBound();
    }

    public double getFillingSpeedForFillLevel(double fillLevel) {
        for (RangeElement re : elements) {
            if (re.getLowerBound() <= fillLevel && re.getUpperBound() >= fillLevel) {
                return re.getFillingSpeed();
            }
        }
        throw new IllegalArgumentException("FillLevel is not in range of the leakageFunction");
    }

}
