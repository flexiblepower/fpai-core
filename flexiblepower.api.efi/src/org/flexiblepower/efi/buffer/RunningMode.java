package org.flexiblepower.efi.buffer;

import java.util.List;

import javax.measure.Measurable;
import javax.measure.quantity.Money;

import org.flexiblepower.efi.buffer.RunningMode.RunningModeRangeElement;

public class RunningMode extends FillLevelFunction<RunningModeRangeElement> {
    private final int id;

    private final String name;

    private final Transition[] possibleTransitions;

    public RunningMode(int id, String name, RunningModeRangeElement[] elements, Transition[] possibleTransitions) {
        super(elements);
        this.id = id;
        this.name = name;
        this.possibleTransitions = possibleTransitions;
    }

    public static class RunningModeRangeElement extends RangeElement {

        // Commodity consumed or produced
        // TODO
        private final List<Measurable<?>> commoditiesPerSecond;

        // Optional: Running costs for this runningmode expressed per second
        private final Measurable<Money> runningCostsPerSecond;

        public RunningModeRangeElement(double lowerBound,
                                       double upperBound,
                                       double xs,
                                       List<Measurable<?>> commoditiesPerSecond,
                                       Measurable<Money> runningCostsPerSecond) {
            super(lowerBound, upperBound, xs);
            this.commoditiesPerSecond = commoditiesPerSecond;
            this.runningCostsPerSecond = runningCostsPerSecond;
        }
    }

}
