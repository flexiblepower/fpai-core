package org.flexiblepower.efi.buffer;

import java.util.ArrayList;
import java.util.List;

import javax.measure.Measurable;

public class RunningMode {
    private final int id;

    private final String name;

    private final List<RunningModeRangeElement> runningModeRangeElements;

    private List<Transition> possibleTransition;

    public RunningMode(int id, String name) {
        this.id = id;
        this.name = name;
        runningModeRangeElements = new ArrayList<RunningMode.RunningModeRangeElement>();
    }

    public void addLine(RunningModeRangeElement line) {
        runningModeRangeElements.add(line);
    }

    public static class RunningModeRangeElement extends RangeElement {

        // Commodity consumed or produced
        // TODO
        private final List<Measurable<?>> commoditiesPerSecond;

        // Optional: Running costs for this runningmode expressed per second
        private final Double runningCostsPerSecond;

        public RunningModeRangeElement(double lowerBound,
                                       double upperBound,
                                       double xs,
                                       List<Measurable<?>> commoditiesPerSecond,
                                       Double runningCostsPerSecond) {
            super(lowerBound, upperBound, xs);
            this.commoditiesPerSecond = commoditiesPerSecond;
            this.runningCostsPerSecond = runningCostsPerSecond;
        }
    }
}
