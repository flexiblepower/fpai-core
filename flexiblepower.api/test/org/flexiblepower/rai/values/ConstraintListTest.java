package org.flexiblepower.rai.values;

import static javax.measure.unit.SI.KILO;
import static javax.measure.unit.SI.WATT;

import javax.measure.Measure;
import javax.measure.quantity.Power;

import junit.framework.TestCase;

import org.flexiblepower.rai.values.Constraint;
import org.flexiblepower.rai.values.ConstraintList;

public class ConstraintListTest extends TestCase {
    public void testBuild() {
        ConstraintList<Power> list = ConstraintList.create(KILO(WATT)).addSingle(1).addRange(2, 5).build();

        assertEquals(2, list.size());
        assertEquals(new Constraint<Power>(Measure.valueOf(1000, WATT)), list.get(0));
        assertEquals(Measure.valueOf(2000, WATT), list.get(1).getClosestValue(Measure.valueOf(0, WATT)));
        assertEquals(Measure.valueOf(2500, WATT), list.get(1).getClosestValue(Measure.valueOf(2500, WATT)));
        assertEquals(Measure.valueOf(5000, WATT), list.get(1).getClosestValue(Measure.valueOf(10000, WATT)));
    }
}
