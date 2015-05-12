package flexiblepower.api.efi.bufferhelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
import org.flexiblepower.ral.values.CommodityMeasurables;
import org.flexiblepower.ral.values.CommoditySet;

public class BufferTest extends TestCase {
    private Buffer<Temperature> fullBuffer;
    private Buffer<Temperature> incompleteBuffer;
    private BufferRegistration<Temperature> br;
    private BufferStateUpdate<Temperature> bsu;
    private BufferSystemDescription bsd;

    @Override
    public void setUp() {
        incompleteBuffer = new Buffer<Temperature>(BufferTest.constructTestBufferRegistration());

        br = BufferTest.constructTestElectricalBufferRegistration();

        fullBuffer = new Buffer<Temperature>(br);

        bsd = BufferTest.constructBSD(br);

        bsu = BufferTest.constructBSU(br, 45);
    }

    private static BufferStateUpdate<Temperature> constructBSU(BufferRegistration<Temperature> br, double fillLevel) {
        // Make a BufferStateUpdate

        Set<ActuatorUpdate> actuatorUpdates = new HashSet<ActuatorUpdate>();
        Set<TimerUpdate> timerUpdates = new HashSet<TimerUpdate>();
        Set<TimerUpdate> emptyTimerUpdates = new HashSet<TimerUpdate>();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 5);

        // Set the minimum off timer (timer 2) to be finished 5 minutes from now.
        timerUpdates.add(new TimerUpdate(2, cal.getTime()));

        // Actuator 1 is on and in minimum run mode.
        actuatorUpdates.add(new ActuatorUpdate(1, 1, timerUpdates));
        actuatorUpdates.add(new ActuatorUpdate(2, 2, emptyTimerUpdates));

        return new BufferStateUpdate<Temperature>(br,
                                                  new Date(),
                                                  new Date(),
                                                  Measure.valueOf(fillLevel, SI.CELSIUS),
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
                                                                          .add(50,
                                                                               new RunningModeBehaviour(10,
                                                                                                        commodityConsumptionOn,
                                                                                                        Measure.valueOf(0.24,
                                                                                                                        NonSI.EUR_PER_HOUR)))
                                                                          .build();
        FillLevelFunction<RunningModeBehaviour> flf_Off = FillLevelFunction.<RunningModeBehaviour> create(0)
                                                                           .add(50,
                                                                                new RunningModeBehaviour(10,
                                                                                                         commodityConsumptionOff,
                                                                                                         Measure.valueOf(0.24,
                                                                                                                         NonSI.EUR_PER_HOUR)))
                                                                           .build();

        Timer minOnTimer = new Timer(1, "Minimum Run Timer", Measure.valueOf(2, SI.SECOND));
        Set<Timer> onTimerSet = new HashSet<Timer>();
        onTimerSet.add(minOnTimer);

        Timer minOffTimer = new Timer(2, "Minimum Off Timer", Measure.valueOf(2, SI.SECOND));
        Set<Timer> offTimerSet = new HashSet<Timer>();
        offTimerSet.add(minOffTimer);

        Set<Transition> transitionsFromOn = new HashSet<Transition>();
        transitionsFromOn.add(new Transition(2,
                                             onTimerSet,
                                             offTimerSet,
                                             Measure.valueOf(0, NonSI.EUR),
                                             Measure.valueOf(0, SI.SECOND)));

        Set<Transition> transitionsFromOff = new HashSet<Transition>();
        transitionsFromOff.add(new Transition(1,
                                              offTimerSet,
                                              onTimerSet,
                                              Measure.valueOf(0, NonSI.EUR),
                                              Measure.valueOf(0, SI.SECOND)));

        Set<RunningMode<FillLevelFunction<RunningModeBehaviour>>> runningModesOfActuator1 = new HashSet<RunningMode<FillLevelFunction<RunningModeBehaviour>>>();
        runningModesOfActuator1.add(new RunningMode<FillLevelFunction<RunningModeBehaviour>>(2,
                                                                                             "rmOn",
                                                                                             flf_On,
                                                                                             transitionsFromOff));
        runningModesOfActuator1.add(new RunningMode<FillLevelFunction<RunningModeBehaviour>>(1,
                                                                                             "rmOff",
                                                                                             flf_Off,
                                                                                             transitionsFromOn));

        Set<RunningMode<FillLevelFunction<RunningModeBehaviour>>> runningModesOfActuator2 = new HashSet<RunningMode<FillLevelFunction<RunningModeBehaviour>>>();
        runningModesOfActuator2.add(new RunningMode<FillLevelFunction<RunningModeBehaviour>>(2,
                                                                                             "rm2On",
                                                                                             flf_On,
                                                                                             transitionsFromOff));
        runningModesOfActuator2.add(new RunningMode<FillLevelFunction<RunningModeBehaviour>>(1,
                                                                                             "rm2Off",
                                                                                             flf_Off,
                                                                                             transitionsFromOn));

        Set<ActuatorBehaviour> actBeh = new HashSet<ActuatorBehaviour>();
        actBeh.add(new ActuatorBehaviour(1, runningModesOfActuator1));
        actBeh.add(new ActuatorBehaviour(2, runningModesOfActuator2));

        FillLevelFunction<LeakageRate> leakageFunction = FillLevelFunction.<LeakageRate> create(0)
                                                                          .add(100, new LeakageRate(14))
                                                                          .build();

        return (new BufferSystemDescription(br,
                                            new Date(),
                                            new Date(),
                                            actBeh,
                                            leakageFunction));
    }

    private static BufferSystemDescription constructNewBSD(BufferRegistration<Temperature> br) {
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

        FillLevelFunction<RunningModeBehaviour> flf_On = FillLevelFunction.<RunningModeBehaviour> create(-100)
                                                                          .add(-10,
                                                                               new RunningModeBehaviour(10,
                                                                                                        commodityConsumptionOn,
                                                                                                        Measure.valueOf(0.24,
                                                                                                                        NonSI.EUR_PER_HOUR)))
                                                                          .build();
        FillLevelFunction<RunningModeBehaviour> flf_Off = FillLevelFunction.<RunningModeBehaviour> create(-100)
                                                                           .add(-10,
                                                                                new RunningModeBehaviour(10,
                                                                                                         commodityConsumptionOff,
                                                                                                         Measure.valueOf(0.24,
                                                                                                                         NonSI.EUR_PER_HOUR)))
                                                                           .build();

        Timer minOnTimer = new Timer(1, "Minimum Run Timer", Measure.valueOf(2, SI.SECOND));
        Set<Timer> onTimerSet = new HashSet<Timer>();
        onTimerSet.add(minOnTimer);

        Timer minOffTimer = new Timer(2, "Minimum Off Timer", Measure.valueOf(2, SI.SECOND));
        Set<Timer> offTimerSet = new HashSet<Timer>();
        offTimerSet.add(minOffTimer);

        Set<Transition> transitionsFromOn = new HashSet<Transition>();
        transitionsFromOn.add(new Transition(2,
                                             onTimerSet,
                                             offTimerSet,
                                             Measure.valueOf(0, NonSI.EUR),
                                             Measure.valueOf(0, SI.SECOND)));

        Set<Transition> transitionsFromOff = new HashSet<Transition>();
        transitionsFromOff.add(new Transition(1,
                                              offTimerSet,
                                              onTimerSet,
                                              Measure.valueOf(0, NonSI.EUR),
                                              Measure.valueOf(0, SI.SECOND)));

        Set<RunningMode<FillLevelFunction<RunningModeBehaviour>>> runningModesOfActuator1 = new HashSet<RunningMode<FillLevelFunction<RunningModeBehaviour>>>();
        runningModesOfActuator1.add(new RunningMode<FillLevelFunction<RunningModeBehaviour>>(2,
                                                                                             "rmOn",
                                                                                             flf_On,
                                                                                             transitionsFromOff));
        runningModesOfActuator1.add(new RunningMode<FillLevelFunction<RunningModeBehaviour>>(1,
                                                                                             "rmOff",
                                                                                             flf_Off,
                                                                                             transitionsFromOn));

        Set<RunningMode<FillLevelFunction<RunningModeBehaviour>>> runningModesOfActuator2 = new HashSet<RunningMode<FillLevelFunction<RunningModeBehaviour>>>();
        runningModesOfActuator2.add(new RunningMode<FillLevelFunction<RunningModeBehaviour>>(2,
                                                                                             "rm2On",
                                                                                             flf_On,
                                                                                             transitionsFromOff));
        runningModesOfActuator2.add(new RunningMode<FillLevelFunction<RunningModeBehaviour>>(1,
                                                                                             "rm2Off",
                                                                                             flf_Off,
                                                                                             transitionsFromOn));

        Set<ActuatorBehaviour> actBeh = new HashSet<ActuatorBehaviour>();
        actBeh.add(new ActuatorBehaviour(1, runningModesOfActuator1));
        actBeh.add(new ActuatorBehaviour(2, runningModesOfActuator2));

        FillLevelFunction<LeakageRate> leakageFunction = FillLevelFunction.<LeakageRate> create(-100)
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
        Assert.assertTrue(fullBuffer.getElectricalActuators().size() == 1);
    }

    public void testGetReachableRunningModes() {
        for (BufferActuator<?> a : incompleteBuffer.getElectricalActuators()) {
            Assert.assertTrue(a.getReachableRunningModes(new Date()).isEmpty());
        }

        fullBuffer.processSystemDescription(bsd);
        // Actuator 1 is off (rm 1) and in minimum off time for 5 minutes.
        fullBuffer.processStateUpdate(bsu);

        BufferActuator<Temperature> a1 = fullBuffer.getActuatorById(1);
        Set<Integer> reachableRunningModes = a1.getReachableRunningModeIds(new Date());
        // Minimum Off timer restricts actuator 1 from going to rm 2 (on).
        Assert.assertTrue(reachableRunningModes.contains(a1.getCurrentRunningModeId()));
        Assert.assertEquals(a1.getCurrentRunningModeId(), 1);
        Assert.assertFalse(reachableRunningModes.contains(2));

        // Also in 4 minutes from now, the running mode with id 2 should not be reachable.
        Calendar cal2 = Calendar.getInstance();
        cal2.add(Calendar.MINUTE, 4);
        Assert.assertFalse(a1.getReachableRunningModeIds(cal2.getTime()).contains(2));

        // In 6 minutes from now, the running mode with id 2 should be reachable.
        Calendar cal3 = Calendar.getInstance();
        cal3.add(Calendar.MINUTE, 6);

        Assert.assertTrue(a1.getReachableRunningModeIds(cal3.getTime()).contains(2));

        // Actuator 2 has no running timers so should have both runningmodes as options.
        BufferActuator<Temperature> a2 = fullBuffer.getActuatorById(2);
        Set<Integer> reachableRunningModes2 = a2.getReachableRunningModeIds(new Date());
        Assert.assertTrue(reachableRunningModes2.contains(a2.getCurrentRunningModeId()));
        Assert.assertEquals(a2.getCurrentRunningModeId(), 2);
        Assert.assertTrue(reachableRunningModes2.contains(1));
    }

    public void testGetPossibleDemands() {
        fullBuffer.processSystemDescription(bsd);
        fullBuffer.processStateUpdate(bsu);
        BufferActuator<Temperature> a1 = fullBuffer.getActuatorById(1);
        List<Measurable<Power>> demandList = a1.getPossibleDemands(new Date(), .2);
        // First actuator is in must off state.
        Assert.assertTrue(demandList.size() == 1);
        Assert.assertEquals(demandList.get(0).doubleValue(SI.WATT), 0d);
        BufferActuator<Temperature> a2 = fullBuffer.getActuatorById(2);
        List<Measurable<Power>> demandList2 = a2.getPossibleDemands(new Date(), .2);
        // Second actuator should have two possible states.
        Assert.assertTrue(demandList2.size() == 2);
        List<Double> demands = new ArrayList<Double>();
        demands.add(demandList2.get(0).doubleValue(SI.WATT));
        demands.add(demandList2.get(1).doubleValue(SI.WATT));
        Assert.assertTrue(demands.contains(0d));
        Assert.assertTrue(demands.contains(1000d));
    }

    public void testReceivedMessages() {
        Assert.assertFalse(fullBuffer.hasReceivedSystemDescription());
        Assert.assertFalse(fullBuffer.hasReceivedStateUpdate());

        // Ignore state update if system description is not in yet.
        fullBuffer.processStateUpdate(bsu);
        Assert.assertFalse(fullBuffer.hasReceivedSystemDescription());
        Assert.assertFalse(fullBuffer.hasReceivedStateUpdate());

        fullBuffer.processSystemDescription(bsd);
        Assert.assertTrue(fullBuffer.hasReceivedSystemDescription());
        fullBuffer.processStateUpdate(bsu);
        Assert.assertTrue(fullBuffer.hasReceivedStateUpdate());
    }

    public void testFillLevelCalculations() {
        fullBuffer.processSystemDescription(bsd);
        fullBuffer.processStateUpdate(bsu);
        Assert.assertEquals(0.9, fullBuffer.getCurrentFillFraction());
        final BufferStateUpdate<Temperature> bsu2 = BufferTest.constructBSU(br, 50);
        fullBuffer.processStateUpdate(bsu2);
        Assert.assertEquals(1d, fullBuffer.getCurrentFillFraction());
        final BufferStateUpdate<Temperature> bsu3 = BufferTest.constructBSU(br, 0);
        fullBuffer.processStateUpdate(bsu3);
        Assert.assertEquals(0d, fullBuffer.getCurrentFillFraction());
        final BufferStateUpdate<Temperature> bsu4 = BufferTest.constructBSU(br, -1);
        fullBuffer.processStateUpdate(bsu4);
        Assert.assertEquals(-0.02d, fullBuffer.getCurrentFillFraction());

        Assert.assertEquals(50d, fullBuffer.getMaximumFillLevel());
        Assert.assertEquals(0d, fullBuffer.getMinimumFillLevel());
        Assert.assertEquals(-1d, fullBuffer.getCurrentFillLevel().doubleValue(fullBuffer.getUnit()));
        Assert.assertEquals(SI.CELSIUS, fullBuffer.getUnit());
    }

    public void testNewSysDescription() {
        fullBuffer.processSystemDescription(bsd);
        fullBuffer.processStateUpdate(bsu);
        Assert.assertEquals(0.9, fullBuffer.getCurrentFillFraction());
        fullBuffer.processSystemDescription(constructNewBSD(br));
        Assert.assertFalse(fullBuffer.hasReceivedStateUpdate());
    }
}
