package flexiblepower.api.efi.bufferhelper;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.measure.Measure;
import javax.measure.quantity.Temperature;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.flexiblepower.api.efi.bufferhelper.Buffer;
import org.flexiblepower.api.efi.bufferhelper.BufferActuator;
import org.flexiblepower.efi.buffer.Actuator;
import org.flexiblepower.efi.buffer.ActuatorBehaviour;
import org.flexiblepower.efi.buffer.ActuatorUpdate;
import org.flexiblepower.efi.buffer.BufferRegistration;
import org.flexiblepower.efi.buffer.BufferStateUpdate;
import org.flexiblepower.efi.buffer.BufferSystemDescription;
import org.flexiblepower.efi.buffer.LeakageRate;
import org.flexiblepower.efi.buffer.RunningModeBehaviour;
import org.flexiblepower.efi.util.FillLevelFunction;
import org.flexiblepower.efi.util.RunningMode;
import org.flexiblepower.efi.util.Timer;
import org.flexiblepower.efi.util.Transition;
import org.flexiblepower.rai.values.CommodityMeasurables;
import org.flexiblepower.rai.values.CommoditySet;

public class BufferTest extends TestCase {
    private Buffer<Temperature> buf;
    private Buffer<Temperature> buf2;
    private BufferStateUpdate<Temperature> bsu;
    private BufferSystemDescription bsd;

    @Override
    public void setUp() {
        buf2 = new Buffer<Temperature>(new BufferRegistration<Temperature>("BR2",
                                                                           new Date(),
                                                                           Measure.zero(SI.SECOND),
                                                                           SI.CELSIUS.toString(),
                                                                           SI.CELSIUS,
                                                                           Arrays.asList(new Actuator(1,
                                                                                                      "Primary2",
                                                                                                      CommoditySet.onlyElectricity),
                                                                                         new Actuator(2,
                                                                                                      "Secondary2",
                                                                                                      CommoditySet.onlyGas))));

        buf = new Buffer<Temperature>(new BufferRegistration<Temperature>("BR1",
                                                                          new Date(),
                                                                          Measure.zero(SI.SECOND),
                                                                          SI.CELSIUS.toString(),
                                                                          SI.CELSIUS,
                                                                          Arrays.asList(new Actuator(1,
                                                                                                     "Primary",
                                                                                                     CommoditySet.onlyHeat),
                                                                                        new Actuator(2,
                                                                                                     "Secondary",
                                                                                                     CommoditySet.onlyGas))));

        bsu = new BufferStateUpdate<Temperature>(new BufferRegistration<Temperature>("BR1",
                                                                                     new Date(),
                                                                                     Measure.zero(SI.SECOND),
                                                                                     SI.CELSIUS.toString(),
                                                                                     SI.CELSIUS,
                                                                                     Arrays.asList(new Actuator(1,
                                                                                                                "Primary",
                                                                                                                CommoditySet.onlyHeat),
                                                                                                   new Actuator(2,
                                                                                                                "Secondary",
                                                                                                                CommoditySet.onlyGas))),
                                                 new Date(),
                                                 new Date(),
                                                 Measure.valueOf(45, SI.CELSIUS),
                                                 new HashSet<ActuatorUpdate>());

        // This fictional device uses both gas and electricity.
        CommodityMeasurables commodityConsumption = CommodityMeasurables.create()
                                                                        .electricity(Measure.valueOf(1000,
                                                                                                     SI.WATT))
                                                                        .gas(Measure.valueOf(.00025,
                                                                                             NonSI.CUBIC_METRE_PER_SECOND))
                                                                        .build();

        FillLevelFunction<RunningModeBehaviour> flf_On = FillLevelFunction.<RunningModeBehaviour> create(0)
                                                                          .add(10,
                                                                               new RunningModeBehaviour(10,
                                                                                                        commodityConsumption,
                                                                                                        Measure.valueOf(0.24,
                                                                                                                        NonSI.EUR_PER_HOUR)))
                                                                          .build();
        FillLevelFunction<RunningModeBehaviour> flf_Off = FillLevelFunction.<RunningModeBehaviour> create(0)
                                                                           .add(10,
                                                                                new RunningModeBehaviour(10,
                                                                                                         commodityConsumption,
                                                                                                         Measure.valueOf(0.24,
                                                                                                                         NonSI.EUR_PER_HOUR)))
                                                                           .build();

        Set<Transition> transitionsFromOn = new HashSet<Transition>();
        transitionsFromOn.add(new Transition(2,
                                             new HashSet<Timer>(),
                                             new HashSet<Timer>(),
                                             Measure.valueOf(0, NonSI.EUR),
                                             Measure.valueOf(0, SI.SECOND)));

        Set<Transition> transitionsFromOff = new HashSet<Transition>();
        transitionsFromOff.add(new Transition(1,
                                              new HashSet<Timer>(),
                                              new HashSet<Timer>(),
                                              Measure.valueOf(0, NonSI.EUR),
                                              Measure.valueOf(0, SI.SECOND)));

        Set<RunningMode<FillLevelFunction<RunningModeBehaviour>>> runningModes = new HashSet<RunningMode<FillLevelFunction<RunningModeBehaviour>>>();
        runningModes.add(new RunningMode<FillLevelFunction<RunningModeBehaviour>>(2, "rmOn", flf_On, transitionsFromOff));
        runningModes.add(new RunningMode<FillLevelFunction<RunningModeBehaviour>>(1,
                                                                                  "rmOff",
                                                                                  flf_Off,
                                                                                  transitionsFromOn));

        Set<RunningMode<FillLevelFunction<RunningModeBehaviour>>> runningModes2 = new HashSet<RunningMode<FillLevelFunction<RunningModeBehaviour>>>();
        runningModes2.add(new RunningMode<FillLevelFunction<RunningModeBehaviour>>(2,
                                                                                   "rm2On",
                                                                                   flf_On,
                                                                                   transitionsFromOff));
        runningModes2.add(new RunningMode<FillLevelFunction<RunningModeBehaviour>>(1,
                                                                                   "rm2Off",
                                                                                   flf_Off,
                                                                                   transitionsFromOn));

        Set<ActuatorBehaviour> actBeh = new HashSet<ActuatorBehaviour>();
        actBeh.add(new ActuatorBehaviour(1, runningModes));
        actBeh.add(new ActuatorBehaviour(2, runningModes2));

        FillLevelFunction<LeakageRate> leakageFunction = FillLevelFunction.<LeakageRate> create(0)
                                                                          .add(100, new LeakageRate(14))
                                                                          .build();

        bsd = new BufferSystemDescription(new BufferRegistration<Temperature>("BR1",
                                                                              new Date(),
                                                                              Measure.zero(SI.SECOND),
                                                                              SI.CELSIUS.toString(),
                                                                              SI.CELSIUS,
                                                                              Arrays.asList(new Actuator(1,
                                                                                                         "Primary",
                                                                                                         CommoditySet.onlyHeat),
                                                                                            new Actuator(2,
                                                                                                         "Secondary",
                                                                                                         CommoditySet.onlyGas))),
                                          new Date(),
                                          new Date(),
                                          actBeh,
                                          leakageFunction);
    }

    public void testNoElectricalActuators() {
        Assert.assertTrue(buf.getElectricalActuators().isEmpty());
    }

    public void testGetReachableRunningModes() {
        for (BufferActuator a : buf2.getElectricalActuators()) {
            Assert.assertTrue(a.getPossibleDemands(new Date(), .2).isEmpty());
        }
    }

    public void testReceivedMessages() {
        Assert.assertFalse(buf.hasReceivedSystemDescription());
        Assert.assertFalse(buf.hasReceivedStateUpdate());
        // Ignore state update if system description is not in yet.
        buf.processStateUpdate(bsu);
        Assert.assertFalse(buf.hasReceivedSystemDescription());
        Assert.assertFalse(buf.hasReceivedStateUpdate());
        buf.processSystemDescription(bsd);
        Assert.assertTrue(buf.hasReceivedSystemDescription());
        buf.processStateUpdate(bsu);
        Assert.assertTrue(buf.hasReceivedStateUpdate());
    }
}
