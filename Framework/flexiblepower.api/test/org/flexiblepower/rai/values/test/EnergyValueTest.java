package org.flexiblepower.rai.values.test;

import junit.framework.TestCase;

import org.flexiblepower.rai.unit.EnergyUnit;
import org.flexiblepower.rai.unit.PowerUnit;
import org.flexiblepower.rai.unit.TimeUnit;
import org.flexiblepower.rai.values.Duration;
import org.flexiblepower.rai.values.EnergyValue;
import org.flexiblepower.rai.values.PowerValue;

public class EnergyValueTest extends TestCase {

    public void testConversions() {
        double c = EnergyUnit.KILO_WATTHOUR.convertTo(1, EnergyUnit.JOULE);
        assertEquals(3600000d, c);

        EnergyValue e = new EnergyValue(1, EnergyUnit.KILO_WATTHOUR);
        assertEquals(1d, e.getValue());
        assertEquals(EnergyUnit.KILO_WATTHOUR, e.getUnit());
        assertEquals(3600d, e.getValueAs(EnergyUnit.KILO_JOULE));
        assertEquals(3600000d, e.getValueAsDefaultUnit());
        assertTrue(e.equals(new EnergyValue(1, EnergyUnit.KILO_WATTHOUR),
                            new EnergyValue(1, EnergyUnit.JOULE)));
        assertTrue(e.equals(new EnergyValue(1.1, EnergyUnit.KILO_WATTHOUR),
                            new EnergyValue(3600001d, EnergyUnit.JOULE)));
        assertFalse(e.equals(new EnergyValue(2, EnergyUnit.KILO_WATTHOUR),
                             new EnergyValue(1, EnergyUnit.JOULE)));
        assertFalse(e.equals(new EnergyValue(2.1, EnergyUnit.KILO_WATTHOUR),
                             new EnergyValue(1000, EnergyUnit.JOULE)));
        assertTrue(e.getAveragePower(new Duration(1, TimeUnit.HOURS))
                    .equals(new PowerValue(1000, PowerUnit.WATT)));
    }
}
