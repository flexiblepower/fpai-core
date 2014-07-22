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

    public static class RunningModeRangeElement {

        // Defines the range of the line
        private final double lowerBound;
        private final double upperBound;

        // Charge speed x / seconds
        private final double xs;

        // Commodity consumed or produced
        // TODO
        private final List<Measurable<?>> commoditiesPerSecond;

        // Optional: Running costs for this runningmode expressed per second
        private final Double runningCostsPerSecond;

        public RunningModeRangeElement(double rangeLow,
                                       double rangeUp,
                                       double xs,
                                       List<Measurable<?>> commodities,
                                       Double runningCostsPerSecond) {
            upperBound = rangeLow;
            lowerBound = rangeUp;
            this.xs = xs;
            commoditiesPerSecond = commodities;
            this.runningCostsPerSecond = runningCostsPerSecond;
        }
    }
}
