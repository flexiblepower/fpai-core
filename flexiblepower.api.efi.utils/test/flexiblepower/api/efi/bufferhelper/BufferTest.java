package flexiblepower.api.efi.bufferhelper;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.measure.Measure;
import javax.measure.quantity.Temperature;
import javax.measure.unit.SI;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.flexiblepower.api.efi.bufferhelper.Buffer;
import org.flexiblepower.efi.buffer.Actuator;
import org.flexiblepower.efi.buffer.BufferRegistration;
import org.flexiblepower.rai.values.CommoditySet;

public class BufferTest extends TestCase {
    public void testNoElectricalActuators() {

        List<Actuator> actList = Arrays.asList(new Actuator(1, "Primary", CommoditySet.onlyHeat),
                                               new Actuator(2, "Secondary", CommoditySet.onlyGas));

        BufferRegistration<Temperature> br = new BufferRegistration<Temperature>("BR1",
                                                                                 new Date(),
                                                                                 Measure.zero(SI.SECOND),
                                                                                 "Celsius",
                                                                                 SI.CELSIUS,
                                                                                 actList);

        Buffer<Temperature> buf = new Buffer<Temperature>(br);
        Assert.assertTrue(buf.getElectricalActuators().isEmpty());
    }
}
