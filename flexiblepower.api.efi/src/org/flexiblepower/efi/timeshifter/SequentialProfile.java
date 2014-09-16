package org.flexiblepower.efi.timeshifter;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;

import org.flexiblepower.rai.values.CommodityForecast;

/**
 * A SequentialProfile is typically used by a time shifter appliance driver to indicate the indicate the order of
 * multiple commodity profiles. (e.g. a wahing/drying machine where program “Cotton 40 + Tumble dry” can be scheduled
 * and both parts of the program can be allocated individually but sequentially)
 */
public class SequentialProfile {
    private final int id;

    private final Measurable<Duration> maxIntervalBefore;

    private final CommodityForecast commodityForecast;

    public SequentialProfile(int id, Measurable<Duration> maxIntervalBefore, CommodityForecast commodityForecast) {
        this.id = id;
        this.maxIntervalBefore = maxIntervalBefore;
        this.commodityForecast = commodityForecast;
    }

    /**
     * @return Unique id to identify the sequential profile.
     */
    public int getId() {
        return id;
    }

    /**
     * @return The maximum time between the end of the last SequentialProfile and the start of start of this
     *         SequentialProfile. See Figure 12. The maxIntervalBefore of the first sequential profile is typically 0.
     *         The maximum time between the end of the last SequentialProfile and the start of start of this
     *         SequentialProfile. The maxIntervalBefore of the first sequential profile is typically 0.
     */
    public Measurable<Duration> getMaxIntervalBefore() {
        return maxIntervalBefore;
    }

    /**
     * @return The forecast profile is used because there can be an uncertainty in the declared profiles. (e.g. the
     *         program a tumble dryer controlled by a sensor does not have a fixed duration and therefore some
     *         uncertainty)
     */
    public CommodityForecast getCommodityForecast() {
        return commodityForecast;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((commodityForecast == null) ? 0 : commodityForecast.hashCode());
        result = prime * result + id;
        result = prime * result + ((maxIntervalBefore == null) ? 0 : maxIntervalBefore.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SequentialProfile other = (SequentialProfile) obj;
        if (commodityForecast == null) {
            if (other.commodityForecast != null) {
                return false;
            }
        } else if (!commodityForecast.equals(other.commodityForecast)) {
            return false;
        }
        if (id != other.id) {
            return false;
        }
        if (maxIntervalBefore == null) {
            if (other.maxIntervalBefore != null) {
                return false;
            }
        } else if (!maxIntervalBefore.equals(other.maxIntervalBefore)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SequentialProfile [id=" + id
               + ", maxIntervalBefore="
               + maxIntervalBefore
               + ", commodityForecast="
               + commodityForecast
               + "]";
    }
}
