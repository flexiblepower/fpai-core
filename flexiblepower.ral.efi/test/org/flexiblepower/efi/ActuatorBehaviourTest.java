package org.flexiblepower.efi;

import java.util.ArrayList;
import java.util.Collections;

import javax.measure.Measure;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;

import junit.framework.TestCase;

import org.flexiblepower.efi.buffer.ActuatorBehaviour;
import org.flexiblepower.efi.buffer.RunningModeBehaviour;
import org.flexiblepower.efi.util.FillLevelFunction;
import org.flexiblepower.efi.util.RunningMode;
import org.flexiblepower.efi.util.Transition;
import org.flexiblepower.ral.values.CommodityMeasurables;

public class ActuatorBehaviourTest extends TestCase {

    /**
     * Test if the ActuatorBehaviour constructor throws an IllegalArgumentExecption when two RunningModes with the same
     * Id are added
     */
    public void testDuplicateRunningMode() {
        RunningModeBehaviour rmb = new RunningModeBehaviour(1,
                                                            CommodityMeasurables.create()
                                                                                .electricity(Measure.valueOf(10,
                                                                                                             SI.WATT))
                                                                                .build(),
                                                            Measure.zero(NonSI.EUR_PER_HOUR));
        FillLevelFunction<RunningModeBehaviour> fll1 = FillLevelFunction.<RunningModeBehaviour> create(0)
                                                                        .add(100, rmb)
                                                                        .build();
        RunningMode<FillLevelFunction<RunningModeBehaviour>> rm1 = new RunningMode<FillLevelFunction<RunningModeBehaviour>>(0,
                                                                                                                            "First",
                                                                                                                            fll1,
                                                                                                                            Collections.<Transition> emptySet());
        RunningMode<FillLevelFunction<RunningModeBehaviour>> rm2 = new RunningMode<FillLevelFunction<RunningModeBehaviour>>(0,
                                                                                                                            "Second",
                                                                                                                            fll1,
                                                                                                                            Collections.<Transition> emptySet());

        ArrayList<RunningMode<FillLevelFunction<RunningModeBehaviour>>> runningModes = new ArrayList<RunningMode<FillLevelFunction<RunningModeBehaviour>>>();
        runningModes.add(rm1);
        runningModes.add(rm2);

        try {
            new ActuatorBehaviour(0, runningModes);
            fail("ActuatorBehavioun constructor should throw an exception");
        } catch (IllegalArgumentException e) {
            // Expected
        }

    }

    /**
     * Test if the ActuatorBehaviour constructor throws an IllegalArgumentExecption when there is a Transition which has
     * a non existing destination
     */
    public void testTransitionWithoutDestination() {
        RunningModeBehaviour rmb = new RunningModeBehaviour(1,
                                                            CommodityMeasurables.create()
                                                                                .electricity(Measure.valueOf(10,
                                                                                                             SI.WATT))
                                                                                .build(),
                                                            Measure.zero(NonSI.EUR_PER_HOUR));
        FillLevelFunction<RunningModeBehaviour> flf = FillLevelFunction.<RunningModeBehaviour> create(0)
                                                                       .add(100, rmb)
                                                                       .build();
        // runningmode 1 does'n exist
        RunningMode<FillLevelFunction<RunningModeBehaviour>> rm = new RunningMode<FillLevelFunction<RunningModeBehaviour>>(0,
                                                                                                                           "First",
                                                                                                                           flf,
                                                                                                                           Collections.singleton(Transition.create(1)
                                                                                                                                                           .build()));

        try {
            new ActuatorBehaviour(0, Collections.singleton(rm));
            fail("ActuatorBehavioun constructor should throw an exception");
        } catch (IllegalArgumentException e) {
            // Expected
        }

    }

    /**
     * Test if the ActuatorBehaviour constructor works with transitions
     */
    public void testWithTransitions() {
        RunningModeBehaviour rmb = new RunningModeBehaviour(1,
                                                            CommodityMeasurables.create()
                                                                                .electricity(Measure.valueOf(10,
                                                                                                             SI.WATT))
                                                                                .build(),
                                                            Measure.zero(NonSI.EUR_PER_HOUR));
        FillLevelFunction<RunningModeBehaviour> flf = FillLevelFunction.<RunningModeBehaviour> create(0)
                                                                       .add(100, rmb)
                                                                       .build();
        // runningmode 1 does'n exist
        RunningMode<FillLevelFunction<RunningModeBehaviour>> rm1 = new RunningMode<FillLevelFunction<RunningModeBehaviour>>(0,
                                                                                                                            "First",
                                                                                                                            flf,
                                                                                                                            Collections.singleton(Transition.create(1)
                                                                                                                                                            .build()));

        RunningMode<FillLevelFunction<RunningModeBehaviour>> rm2 = new RunningMode<FillLevelFunction<RunningModeBehaviour>>(1,
                                                                                                                            "Second",
                                                                                                                            flf,
                                                                                                                            Collections.singleton(Transition.create(0)
                                                                                                                                                            .build()));

        ArrayList<RunningMode<FillLevelFunction<RunningModeBehaviour>>> runningModes = new ArrayList<RunningMode<FillLevelFunction<RunningModeBehaviour>>>();
        runningModes.add(rm1);
        runningModes.add(rm2);
        new ActuatorBehaviour(0, runningModes);

    }
}
