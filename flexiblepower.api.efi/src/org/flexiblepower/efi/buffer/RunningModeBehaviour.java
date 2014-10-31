package org.flexiblepower.efi.buffer;

import javax.measure.Measurable;
import javax.measure.quantity.MoneyFlow;

import org.flexiblepower.efi.util.FillLevelFunction;
import org.flexiblepower.efi.util.RunningMode;
import org.flexiblepower.rai.values.CommodityMeasurables;

/**
 * Describes the behavior of a {@link RunningMode}. This class is usually used as the value of a
 * {@link FillLevelFunction}, since the behavior may change acoording to the fill level. This class describes the effect
 * on the buffer and the consumed or produced commodities.
 */
public class RunningModeBehaviour {

    /**
     * The effect on the buffer, expressed in the amount of units as specified in the {@link BufferRegistration} per
     * second, either positive or negative.
     */
    private final double fillingRate;

    /**
     * The consumed (positive value) or produced (negative value) energy for every commodity involved in this
     * {@link RunningMode}.
     */
    private final CommodityMeasurables commodityConsumption;

    /**
     * The cost associated with this {@link RunningMode}.
     */
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

    /**
     * Gets the effect on the buffer, expressed in the amount of units as specified in the {@link BufferRegistration}
     * per second, either positive or negative.
     *
     * For example, if this buffer is a thermal buffer with the unit degrees Celsius (this is specified in the
     * {@link BufferRegistration}), and this {@link RunningMode} increases the temperature of the buffer with 0.01
     * degrees Celsius per second, than the value of fillingRate is 0.01.
     *
     * @return The effect on the buffer, expressed in the amount of units as specified in the {@link BufferRegistration}
     */
    public double getFillingRate() {
        return fillingRate;
    }

    /**
     * Gets the consumption (positive number) or production (negative value) that belongs to this RunningMode.
     *
     * @return The Consumption (positive) or production (negative) of the commodities for this RunningMode
     */
    public CommodityMeasurables getCommodityConsumption() {
        return commodityConsumption;
    }

    /**
     * @return The cost associated with this {@link RunningMode}.
     */
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
