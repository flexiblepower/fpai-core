package org.flexiblepower.efi.buffer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.measure.Measurable;
import javax.measure.quantity.Money;

import org.flexiblepower.efi.buffer.RunningMode.RunningModeRangeElement;
import org.flexiblepower.rai.values.Commodity;

public class RunningMode extends FillLevelFunction<RunningModeRangeElement> {
    public static Builder create(int id, String name) {
        return new Builder(id, name);
    }

    public static class Builder {
        private final int id;
        private final String name;
        private final Set<Transition> transitions;
        private final List<RunningModeRangeElement> elements;

        Builder(int id, String name) {
            this.id = id;
            this.name = name;
            transitions = new HashSet<Transition>();
            elements = new ArrayList<RunningMode.RunningModeRangeElement>();
        }

        public Builder addTransition(Transition transition) {
            transitions.add(transition);
            return this;
        }

        private double lowerBound = 0;

        public Builder setLowerBound(double lowerBound) {
            this.lowerBound = lowerBound;
            return this;
        }

        public Builder addElement(RunningModeRangeElement element) {
            elements.add(element);
            lowerBound = element.getUpperBound();
            return this;
        }

        public Builder addElement(double lowerBound,
                                  double upperBound,
                                  double fillingSpeed,
                                  Commodity.Measurements commodityConsumption,
                                  Measurable<Money> runningCostsPerSecond) {
            elements.add(new RunningModeRangeElement(lowerBound,
                                                     upperBound,
                                                     fillingSpeed,
                                                     commodityConsumption,
                                                     runningCostsPerSecond));
            this.lowerBound = upperBound;
            return this;
        }

        public Builder addElement(double upperBound,
                                  double fillingSpeed,
                                  Commodity.Measurements commodityConsumption,
                                  Measurable<Money> runningCostsPerSecond) {
            elements.add(new RunningModeRangeElement(lowerBound,
                                                     upperBound,
                                                     fillingSpeed,
                                                     commodityConsumption,
                                                     runningCostsPerSecond));
            lowerBound = upperBound;
            return this;
        }

        public RunningMode build() {
            return new RunningMode(id,
                                   name,
                                   transitions,
                                   elements.toArray(new RunningModeRangeElement[elements.size()]));
        }
    }

    private final int id;
    private final String name;
    private final Map<Integer, Transition> transitions;

    public RunningMode(int id, String name, Set<Transition> transitions, RunningModeRangeElement... elements) {
        // TODO: Check for empty elements?.
        super(elements);
        this.id = id;
        this.name = name;
        TreeMap<Integer, Transition> tempTransitions = new TreeMap<Integer, Transition>();
        for (Transition transition : transitions) {
            tempTransitions.put(transition.getToRunningMode(), transition);
        }
        this.transitions = Collections.unmodifiableMap(tempTransitions);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Collection<Transition> getTransitions() {
        return transitions.values();
    }

    public Transition getTransitionTo(RunningMode runningMode) {
        return getTransitionTo(runningMode.getId());
    }

    public Transition getTransitionTo(int runningModeId) {
        return transitions.get(runningModeId);
    }

    public static class RunningModeRangeElement extends RangeElement {

        /** Commodity consumed or produced */
        private final Commodity.Measurements commodityConsumption;

        /** Optional: Running costs for this runningmode expressed per second */
        private final Measurable<Money> runningCostsPerSecond;

        public RunningModeRangeElement(double lowerBound,
                                       double upperBound,
                                       double fillingSpeed,
                                       Commodity.Measurements commodityConsumption,
                                       Measurable<Money> runningCostsPerSecond) {
            super(lowerBound, upperBound, fillingSpeed);
            this.commodityConsumption = commodityConsumption;
            this.runningCostsPerSecond = runningCostsPerSecond;
        }

        public Commodity.Measurements getCommodityConsumption() {
            return commodityConsumption;
        }

        public Measurable<Money> getRunningCostsPerSecond() {
            return runningCostsPerSecond;
        }

    }
}
