package org.flexiblepower.efi.unconstrained;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.measure.Measurable;
import javax.measure.quantity.Money;

import org.flexiblepower.rai.values.Commodity;
import org.flexiblepower.rai.values.Commodity.Measurements;

public class RunningMode {
    private final int id;

    private final String name;

    private final Commodity.Measurements commodityFlowAmounts;

    private final Measurable<Money> runningCostsPerSecond;

    private final Map<Integer, Transition> possibleTransitions;

    public RunningMode(int id,
                       String name,
                       Measurements commodityFlowAmounts,
                       Measurable<Money> runningCostsPerSecond,
                       Set<Transition> possibleTransitions) {
        this.id = id;
        this.name = name;
        this.commodityFlowAmounts = commodityFlowAmounts;
        this.runningCostsPerSecond = runningCostsPerSecond;

        TreeMap<Integer, Transition> tempTransitions = new TreeMap<Integer, Transition>();
        for (Transition transition : possibleTransitions) {
            tempTransitions.put(transition.getToRunningMode(), transition);
        }
        this.possibleTransitions = tempTransitions;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Commodity.Measurements getCommodityFlowAmounts() {
        return commodityFlowAmounts;
    }

    public Measurable<Money> getRunningCostsPerSecond() {
        return runningCostsPerSecond;
    }

    public Collection<Transition> getTransitions() {
        return possibleTransitions.values();
    }

    public Transition getTransitionTo(RunningMode runningMode) {
        return getTransitionTo(runningMode.getId());
    }

    public Transition getTransitionTo(int runningModeId) {
        return possibleTransitions.get(runningModeId);
    }
}
