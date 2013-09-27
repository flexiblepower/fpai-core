package org.flexiblepower.rai.values.test;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.flexiblepower.rai.unit.EnergyUnit;
import org.flexiblepower.rai.unit.TimeUnit;
import org.flexiblepower.rai.values.Duration;
import org.flexiblepower.rai.values.EnergyProfile;
import org.flexiblepower.rai.values.EnergyProfile.Builder;

public class EnergyProfileTest extends TestCase {

    public void testGetDuration() {
        // we want a profile of one minute with 30 elements of 2 seconds
        Duration totalDuration = new Duration(1, TimeUnit.MINUTES);
        Duration elementDuration = new Duration(2, TimeUnit.SECONDS);
        long elementCount = totalDuration.getMilliseconds() / elementDuration.getMilliseconds();

        // build the profile
        Builder builder = new EnergyProfile.Builder().setDuration(elementDuration);
        for (int i = 0; i < elementCount; i++) {
            builder.add(1, EnergyUnit.JOULE);
        }

        // assert that the duration matches one minute
        Assert.assertEquals(totalDuration, builder.build().getDuration());
    }
}
