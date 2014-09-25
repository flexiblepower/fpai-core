package org.flexiblepower.efi.unconstrained;

import javax.measure.Measurable;
import javax.measure.quantity.MoneyFlow;

import org.flexiblepower.rai.values.CommodityMeasurables;

public class RunningModeBehaviour {
    private final CommodityMeasurables commodityConsumption;

    private final Measurable<MoneyFlow> runningCosts;

    public RunningModeBehaviour(CommodityMeasurables commodityConsumption,
                                Measurable<MoneyFlow> runningCosts) {
        if (commodityConsumption == null) {
            throw new NullPointerException("commodityFlowAmounts");
        } else if (runningCosts == null) {
            throw new NullPointerException("runningCosts");
        }

        this.commodityConsumption = commodityConsumption;
        this.runningCosts = runningCosts;
    }

    public CommodityMeasurables getCommodityConsumption() {
        return commodityConsumption;
    }

    public Measurable<MoneyFlow> getRunningCosts() {
        return runningCosts;
    }

    @Override
    public int hashCode() {
        return 31 * commodityConsumption.hashCode() + runningCosts.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        RunningModeBehaviour other = (RunningModeBehaviour) obj;
        if (!commodityConsumption.equals(other.commodityConsumption)) {
            return false;
        } else if (!runningCosts.equals(other.runningCosts)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RunningModeBehaviour [commodityConsumption=" + commodityConsumption
               + ", runningCosts="
               + runningCosts
               + "]";
    }
}
