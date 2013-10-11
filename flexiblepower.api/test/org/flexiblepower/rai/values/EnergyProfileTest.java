package org.flexiblepower.rai.values.test;

import static javax.measure.unit.NonSI.MINUTE;
import static javax.measure.unit.SI.JOULE;
import static javax.measure.unit.SI.SECOND;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Duration;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.flexiblepower.rai.values.EnergyProfile;
import org.flexiblepower.rai.values.EnergyProfile.Builder;

public class EnergyProfileTest extends TestCase {

    public void testGetDuration() {
        // we want a profile of one minute with 30 elements of 2 seconds
        Measurable<Duration> totalDuration = Measure.valueOf(1, MINUTE);
        Measurable<Duration> elementDuration = Measure.valueOf(2, SECOND);
        long elementCount = (long) (totalDuration.doubleValue(SECOND) / elementDuration.doubleValue(SECOND));

        // build the profile
        Builder builder = new EnergyProfile.Builder().setDuration(elementDuration);
        for (int i = 0; i < elementCount; i++) {
            builder.add(Measure.valueOf(1, JOULE));
        }

        // assert that the duration matches one minute
        Assert.assertEquals(totalDuration.doubleValue(SECOND), builder.build().getDuration().doubleValue(SECOND), 0.01);
    }
}
