package org.flexiblepower.rai.values.test;

import java.util.Date;

import junit.framework.TestCase;

import org.flexiblepower.rai.unit.TimeUnit;
import org.flexiblepower.rai.values.Duration;

public class DurationTest extends TestCase {

    public void testConstructor() {
        Duration d = new Duration(new Date(1000), new Date(2000));
        assertEquals(1000, d.getMilliseconds());
        d = new Duration(new Date(10000), new Date(9000));
        assertEquals(-1000, d.getMilliseconds());
        d = new Duration(10, TimeUnit.MILLISECONDS);
        assertEquals(10, d.getMilliseconds());
    }

    public void testConversion() {
        Duration d = new Duration(1, TimeUnit.MINUTES);
        assertEquals(60d, d.getValueAs(TimeUnit.SECONDS));
        assertEquals(60d, d.getValueAsDefaultUnit());
        assertEquals(60000d, d.getValueAs(TimeUnit.MILLISECONDS));
        assertEquals(1d / 60, d.getValueAs(TimeUnit.HOURS), 0.001);
        assertEquals(1d / (24 * 60), d.getValueAs(TimeUnit.DAYS), 0.001);
        assertEquals(1d, d.getValue());
        assertEquals(0d, Duration.ZERO.getValueAsDefaultUnit());
    }

    public void testAdd() {
        Duration d = new Duration(1, TimeUnit.DAYS);
        assertEquals(24 * 3600 * 1000 + 1000, d.addTo(new Date(1000)).getTime());
    }

    public void testRemoveFrom() {
        Duration d = new Duration(1, TimeUnit.DAYS);
        assertEquals(1000, d.removeFrom(new Date(24 * 3600 * 1000 + 1000))
                            .getTime());

    }

    public void testEquals() {
        assertEquals(new Duration(1, TimeUnit.MINUTES),
                     new Duration(60, TimeUnit.SECONDS));
    }
}
