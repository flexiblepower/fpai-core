package org.flexiblepower.efi.buffer;

import java.util.Map;
import java.util.Set;

import javax.measure.Measurable;
import javax.measure.quantity.Money;

import org.flexiblepower.efi.buffer.RunningMode.RunningModeRangeElement;
import org.flexiblepower.rai.values.Commodity;

public class RunningMode extends FillLevelFunction<RunningModeRangeElement> {

    private final int id;
    private final String name;
    private final Set<Transition> possibleTransitions;

    public RunningMode(int id, String name, RunningModeRangeElement[] elements, Set<Transition> possibleTransitions) {
        super(elements);
        this.id = id;
        this.name = name;
        this.possibleTransitions = possibleTransitions;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<Transition> getPossibleTransitions() {
        return possibleTransitions;
    }

    public static class RunningModeRangeElement extends RangeElement {

        /** Commodity consumed or produced */
        private final Map<Commodity<?, ?>, Measurable<?>> commodityConsumption;

        /** Optional: Running costs for this runningmode expressed per second */
        private final Measurable<Money> runningCostsPerSecond;

        public RunningModeRangeElement(double lowerBound,
                                       double upperBound,
                                       double fillingSpeed,
                                       Map<Commodity<?, ?>, Measurable<?>> commodityConsumption,
                                       Measurable<Money> runningCostsPerSecond) {
            super(lowerBound, upperBound, fillingSpeed);
            this.commodityConsumption = commodityConsumption;
            this.runningCostsPerSecond = runningCostsPerSecond;
        }

        public Map<Commodity<?, ?>, Measurable<?>> getCommodityConsumption() {
            return commodityConsumption;
        }

        public Measurable<Money> getRunningCostsPerSecond() {
            return runningCostsPerSecond;
        }

    }

}
