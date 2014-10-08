package flexiblepower.api.efi.bufferhelper;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Power;
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
import org.flexiblepower.efi.util.TimerUpdate;
import org.flexiblepower.efi.util.Transition;
import org.flexiblepower.rai.values.CommodityMeasurables;
import org.flexiblepower.rai.values.CommoditySet;

public class BufferTest extends TestCase {
    private Buffer<Temperature> buf;
    private Buffer<Temperature> incompleteBuffer;
    private BufferRegistration<Temperature> br;
    private BufferStateUpdate<Temperature> bsu;
    private BufferSystemDescription bsd;

    @Override
    public void setUp() {
        incompleteBuffer = new Buffer<Temperature>(BufferTest.constructTestBufferRegistration());

        br = BufferTest.constructTestElectricalBufferRegistration();

        buf = new Buffer<Temperature>(br);

        bsd = BufferTest.constructBSD(br);

        bsu = BufferTest.constructBSU(br);
    }

    private static BufferStateUpdate<Temperature> constructBSU(BufferRegistration<Temperature> br) {
        // Make a BufferStateUpdate

        Set<ActuatorUpdate> actuatorUpdates = new HashSet<ActuatorUpdate>();
        Set<TimerUpdate> timerUpdates = new HashSet<TimerUpdate>();

        actuatorUpdates.add(new ActuatorUpdate(1, 1, timerUpdates));
        actuatorUpdates.add(new ActuatorUpdate(2, 2, timerUpdates));

        return new BufferStateUpdate<Temperature>(br,
                                                  new Date(),
                                                  new Date(),
                                                  Measure.valueOf(45, SI.CELSIUS),
                                                  actuatorUpdates);
    }

    private static BufferSystemDescription constructBSD(BufferRegistration<Temperature> br) {
        // Make a BufferStateUpdate
        // This fictional device uses both gas and electricity.
        CommodityMeasurables commodityConsumptionOn = CommodityMeasurables.create()
                                                                          .electricity(Measure.valueOf(1000,
                                                                                                       SI.WATT))
                                                                          .gas(Measure.valueOf(.00025,
                                                                                               NonSI.CUBIC_METRE_PER_SECOND))
                                                                          .build();
        CommodityMeasurables commodityConsumptionOff = CommodityMeasurables.create()
                                                                           .electricity(Measure.valueOf(0,
                                                                                                        SI.WATT))
                                                                           .gas(Measure.valueOf(0,
                                                                                                NonSI.CUBIC_METRE_PER_SECOND))
                                                                           .build();

        FillLevelFunction<RunningModeBehaviour> flf_On = FillLevelFunction.<RunningModeBehaviour> create(0)
                                                                          .add(10,
                                                                               new RunningModeBehaviour(10,
                                                                                                        commodityConsumptionOn,
                                                                                                        Measure.valueOf(0.24,
                                                                                                                        NonSI.EUR_PER_HOUR)))
                                                                          .build();
        FillLevelFunction<RunningModeBehaviour> flf_Off = FillLevelFunction.<RunningModeBehaviour> create(0)
                                                                           .add(10,
                                                                                new RunningModeBehaviour(10,
                                                                                                         commodityConsumptionOff,
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

        return (new BufferSystemDescription(br,
                                            new Date(),
                                            new Date(),
                                            actBeh,
                                            leakageFunction));
    }

    private static BufferRegistration<Temperature> constructTestElectricalBufferRegistration() {
        return new BufferRegistration<Temperature>("BR1",
                                                   new Date(),
                                                   Measure.zero(SI.SECOND),
                                                   SI.CELSIUS.toString(),
                                                   SI.CELSIUS,
                                                   Arrays.asList(new Actuator(1,
                                                                              "Primary",
                                                                              CommoditySet.onlyElectricity),
                                                                 new Actuator(2,
                                                                              "Secondary",
                                                                              CommoditySet.onlyGas)));
    }

    private static BufferRegistration<Temperature> constructTestBufferRegistration() {
        return new BufferRegistration<Temperature>("BR2",
                                                   new Date(),
                                                   Measure.zero(SI.SECOND),
                                                   SI.CELSIUS.toString(),
                                                   SI.CELSIUS,
                                                   Arrays.asList(new Actuator(1,
                                                                              "Primary2",
                                                                              CommoditySet.onlyElectricity),
                                                                 new Actuator(2,
                                                                              "Secondary2",
                                                                              CommoditySet.onlyGas)));
    }

    public void testOneElectricalActuator() {
        Assert.assertTrue(buf.getElectricalActuators().size() == 1);
    }

    public void testGetReachableRunningModes() {
        for (BufferActuator a : incompleteBuffer.getElectricalActuators()) {
            Assert.assertTrue(a.getReachableRunningModes(new Date()).isEmpty());
        }

        buf.processSystemDescription(bsd);
        buf.processStateUpdate(bsu);

        for (BufferActuator a : buf.getElectricalActuators()) {
            Assert.assertFalse(a.getReachableRunningModes(new Date()).isEmpty());
        }
    }

    public void testGetPossibleDemands() {
        buf.processSystemDescription(bsd);
        buf.processStateUpdate(bsu);
        for (BufferActuator a : buf.getElectricalActuators()) {
            List<Measurable<Power>> demandList = a.getPossibleDemands(new Date(), .2);
            Assert.assertTrue(demandList.size() == 2);
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
