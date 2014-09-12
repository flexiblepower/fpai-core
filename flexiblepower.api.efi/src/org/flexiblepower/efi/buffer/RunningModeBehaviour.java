package org.flexiblepower.efi.buffer;

import javax.measure.Measurable;
import javax.measure.quantity.MoneyFlow;

import org.flexiblepower.rai.values.CommodityMeasurables;

public class RunningModeBehaviour {
    private final double fillingRate;
    private final CommodityMeasurables commodityConsumption;
    private final Measurable<MoneyFlow> runningCosts;

    public RunningModeBehaviour(double fillingRate,
                                CommodityMeasurables commodityConsumption,
                                Measurable<MoneyFlow> runningCosts) {
        if (commodityConsumption == null) {
            throw new NullPointerException("commodityConsumption");
        } else if (runningCosts == null) {
            throw new NullPointerException("runningCosts");
        }

        this.fillingRate = fillingRate;
        this.commodityConsumption = commodityConsumption;
        this.runningCosts = runningCosts;
    }

    public double getFillingRate() {
        return fillingRate;
    }

    public CommodityMeasurables getCommodityConsumption() {
        return commodityConsumption;
    }

    public Measurable<MoneyFlow> getRunningCosts() {
        return runningCosts;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((commodityConsumption == null) ? 0 : commodityConsumption.hashCode());
        long temp;
        temp = Double.doubleToLongBits(fillingRate);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((runningCosts == null) ? 0 : runningCosts.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        RunningModeBehaviour other = (RunningModeBehaviour) obj;
        if (commodityConsumption == null) {
            if (other.commodityConsumption != null) {
                return false;
            }
        } else if (!commodityConsumption.equals(other.commodityConsumption)) {
            return false;
        }
        if (Double.doubleToLongBits(fillingRate) != Double.doubleToLongBits(other.fillingRate)) {
            return false;
        }
        if (runningCosts == null) {
            if (other.runningCosts != null) {
                return false;
            }
        } else if (!runningCosts.equals(other.runningCosts)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RunningModeBehaviour [fillingRate=" + fillingRate
               + ", commodityConsumption="
               + commodityConsumption
               + ", runningCosts="
               + runningCosts
               + "]";
    }
}
