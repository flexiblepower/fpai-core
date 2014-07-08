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

import org.flexiblepower.rai.old.StorageControlSpace;
import org.flexiblepower.rai.values.ConstraintList;
import org.flexiblepower.time.TimeUtil;

public class StorageControlSpaceTest extends TestCase {
    private static final int NEG_NR = -100;
    private static final int POS_NR = 100;

    public void testValidationTests() {
        ConstraintList<Power> zero = ConstraintList.create(SI.WATT).addSingle(0).build();
        ConstraintList<Power> zeroPos = ConstraintList.create(SI.WATT).addSingle(0).addSingle(POS_NR).build();
        ConstraintList<Power> zeroNeg = ConstraintList.create(SI.WATT).addSingle(0).addSingle(NEG_NR).build();
        ConstraintList<Power> pos = ConstraintList.create(SI.WATT).addSingle(POS_NR).build();
        ConstraintList<Power> neg = ConstraintList.create(SI.WATT).addSingle(NEG_NR).build();
        ConstraintList<Power> posRange = ConstraintList.create(SI.WATT).addRange(0, POS_NR).build();
        ConstraintList<Power> negRange = ConstraintList.create(SI.WATT).addRange(NEG_NR, 0).build();

        create(zero, zero);
        create(pos, zero);
        create(pos, pos);
        create(zero, pos);
        create(zeroPos, zero);
        create(zeroPos, zeroPos);
        create(zero, zeroPos);
        create(posRange, zero);
        create(posRange, posRange);
        create(zero, posRange);

        createError(neg, pos);
        createError(pos, neg);
        createError(neg, zero);
        createError(zero, neg);
        createError(zeroPos, zeroNeg);
        createError(zeroNeg, zeroPos);
        createError(zeroNeg, zero);
        createError(zero, zeroNeg);
        createError(posRange, negRange);
        createError(negRange, posRange);
        createError(negRange, zero);
        createError(zero, negRange);
    }

    private void create(ConstraintList<Power> chargeSpeed, ConstraintList<Power> dischargeSpeed) {
        Date now = new Date();
        Date future = TimeUtil.add(now, Measure.valueOf(1, NonSI.HOUR));
        Measurable<Energy> capacity = Measure.valueOf(1, NonSI.KWH);
        Measurable<Power> selfDischarge = Measure.valueOf(1, SI.WATT);
        Measurable<Duration> minute = Measure.valueOf(1, NonSI.MINUTE);

        // First we try to create a couple of valid ones
        new StorageControlSpace("xxx",
                                now,
                                future,
                                future,
                                capacity,
                                0,
                                chargeSpeed,
                                dischargeSpeed,
                                selfDischarge,
                                1,
                                1,
                                minute,
                                minute,
                                future,
                                1.);
    }

    private void createError(ConstraintList<Power> chargeSpeed, ConstraintList<Power> dischargeSpeed) {
        try {
            create(chargeSpeed, dischargeSpeed);
            fail("Expected failure for arguments: " + chargeSpeed);
        } catch (IllegalArgumentException ex) {
            // OK
        }
    }
}
