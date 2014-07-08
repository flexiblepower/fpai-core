package org.flexiblepower.rai;

import java.util.Date;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;

import junit.framework.TestCase;

import org.flexiblepower.rai.old.BufferControlSpace;
import org.flexiblepower.rai.values.ConstraintList;
import org.flexiblepower.time.TimeUtil;

public class BufferControlSpaceTest extends TestCase {
    private static final int NEG_NR = -100;
    private static final int POS_NR = 100;

    public void testValidationTests() {
        create(ConstraintList.create(SI.WATT).addSingle(0).build());
        create(ConstraintList.create(SI.WATT).addSingle(0).addSingle(POS_NR).build());
        create(ConstraintList.create(SI.WATT).addSingle(0).addSingle(NEG_NR).build());
        create(ConstraintList.create(SI.WATT).addSingle(POS_NR).build());
        create(ConstraintList.create(SI.WATT).addSingle(NEG_NR).build());
        create(ConstraintList.create(SI.WATT).addRange(0, POS_NR).build());
        create(ConstraintList.create(SI.WATT).addRange(NEG_NR, 0).build());

        createError(ConstraintList.create(SI.WATT).addSingle(NEG_NR).addSingle(POS_NR).build());
        createError(ConstraintList.create(SI.WATT).addRange(NEG_NR, POS_NR).build());
    }

    private void create(ConstraintList<Power> chargeSpeed) {
        Date now = new Date();
        Date future = TimeUtil.add(now, Measure.valueOf(1, NonSI.HOUR));
        Measurable<Energy> capacity = Measure.valueOf(1, NonSI.KWH);
        Measurable<Power> selfDischarge = Measure.valueOf(1, SI.WATT);
        Measurable<Duration> minute = Measure.valueOf(1, NonSI.MINUTE);

        // First we try to create a couple of valid ones
        new BufferControlSpace("xxx",
                               now,
                               future,
                               future,
                               capacity,
                               0,
                               chargeSpeed,
                               selfDischarge,
                               minute,
                               minute,
                               future,
                               1.);
    }

    private void createError(ConstraintList<Power> chargeSpeed) {
        try {
            create(chargeSpeed);
            fail("Expected failure for arguments: " + chargeSpeed);
        } catch (IllegalArgumentException ex) {
            // OK
        }
    }
}
