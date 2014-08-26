package org.flexiblepower.efi.unconstrained;

import java.util.List;

import javax.measure.Measurable;
import javax.measure.quantity.Money;

public class RunningMode {
    private final int id;

    private final String name;

    // Commodity consumed or produced
    // TODO
    private final List<Measurable<?>> commodityFlowAmounts;

    private final Measurable<Money> runningCostsPerSecond;

    private List<Transition> possibleTransitions;

    public RunningMode(int id,
                       String name,
                       List<Measurable<?>> commodityFlowAmounts,
                       Measurable<Money> runningCostsPerSecond) {
        this.id = id;
        this.name = name;
        this.commodityFlowAmounts = commodityFlowAmounts;
        this.runningCostsPerSecond = runningCostsPerSecond;
    }
}
