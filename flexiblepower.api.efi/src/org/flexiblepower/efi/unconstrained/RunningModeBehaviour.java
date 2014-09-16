package org.flexiblepower.efi.unconstrained;

import javax.measure.Measurable;
import javax.measure.quantity.Money;

import org.flexiblepower.rai.values.CommodityMeasurables;

public class RunningModeBehaviour {
    private final CommodityMeasurables commodityFlowAmounts;

    private final Measurable<Money> runningCostsPerSecond;

    public RunningModeBehaviour(CommodityMeasurables commodityFlowAmounts,
                                Measurable<Money> runningCostsPerSecond) {
        if (commodityFlowAmounts == null) {
            throw new NullPointerException("commodityFlowAmounts");
        } else if (runningCostsPerSecond == null) {
            throw new NullPointerException("runningCostsPerSecond");
        }

        this.commodityFlowAmounts = commodityFlowAmounts;
        this.runningCostsPerSecond = runningCostsPerSecond;
    }

    public CommodityMeasurables getCommodityFlowAmounts() {
        return commodityFlowAmounts;
    }

    public Measurable<Money> getRunningCostsPerSecond() {
        return runningCostsPerSecond;
    }

    @Override
    public int hashCode() {
        return 31 * commodityFlowAmounts.hashCode() + runningCostsPerSecond.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        RunningModeBehaviour other = (RunningModeBehaviour) obj;
        if (!commodityFlowAmounts.equals(other.commodityFlowAmounts)) {
            return false;
        } else if (!runningCostsPerSecond.equals(other.runningCostsPerSecond)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RunningModeBehaviour [commodityFlowAmounts=" + commodityFlowAmounts
               + ", runningCostsPerSecond="
               + runningCostsPerSecond
               + "]";
    }
}
