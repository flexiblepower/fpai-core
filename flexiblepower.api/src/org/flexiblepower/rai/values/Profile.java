package org.flexiblepower.rai.values;

import java.util.AbstractList;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Duration;

public class Profile<PE extends ProfileElement<PE>> extends AbstractList<PE> {

    protected final PE[] elements;

    public Profile(PE[] elements) {
        super();
        this.elements = elements;
    }

    @Override
    public PE get(int index) {
        return elements[index];
    }

    @Override
    public int size() {
        return elements.length;
    }

    public Measurable<Duration> getTotalDuration() {
        double total = 0;
        for (final PE e : elements) {
            total += e.getDuration().doubleValue(Duration.UNIT);
        }
        return Measure.valueOf(total, Duration.UNIT);
    }

    public Profile<PE> subProfile(Measurable<Duration> offset, Measurable<Duration> duration) {
        // TODO
        return null;
    }

}
