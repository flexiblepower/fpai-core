package org.flexiblepower.rai.values;

import java.io.Serializable;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import javax.measure.quantity.Quantity;
import javax.measure.quantity.Volume;
import javax.measure.quantity.VolumetricFlowRate;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

/**
 * This abstract class contains information about the units that are used for a particular commodity. There are
 * currently implementations for the commodities ELECTRICITY and GAS.
 *
 * @param <BQ>
 *            Quantity of the Billable Unit
 * @param <FQ>
 *            Quantity of the Flow Unit
 */
public abstract class Commodity<BQ extends Quantity, FQ extends Quantity> implements Serializable {
    /**
     * The singleton object for {@link Gas}
     */
    public static final Gas GAS = new Gas();

    /**
     * The singleton object for {@link Electricity}
     */
    public static final Electricity ELECTRICITY = new Electricity();

    /**
     * The singleton object for {@link Heat}
     */
    public static final Heat HEAT = new Heat();

    /**
     * The {@link Gas} commodity. The billable unit is in cubic meters. The flow unit is in cubic meters pers second.
     *
     * This is a singleton object (see {@link Commodity#GAS});
     */
    public static final class Gas extends Commodity<Volume, VolumetricFlowRate> {
        private static final long serialVersionUID = 363346706724665032L;

        Gas() {
            super(SI.CUBIC_METRE, NonSI.CUBIC_METRE_PER_SECOND);
        }

        @Override
        public Measurable<VolumetricFlowRate> average(Measurable<Volume> amount, Measurable<Duration> duration) {
            double seconds = duration.doubleValue(SI.SECOND);
            if (seconds <= 0) {
                throw new IllegalArgumentException("invalid duration: " + seconds + " seconds");
            }
            return Measure.valueOf(amount.doubleValue(SI.CUBIC_METRE) / seconds, NonSI.CUBIC_METRE_PER_SECOND);
        }

        @Override
        public Measurable<Volume> amount(Measurable<VolumetricFlowRate> average, Measurable<Duration> duration) {
            double seconds = duration.doubleValue(SI.SECOND);
            if (seconds <= 0) {
                throw new IllegalArgumentException("invalid duration: " + seconds + " seconds");
            }
            return Measure.valueOf(average.doubleValue(NonSI.CUBIC_METRE_PER_SECOND) * seconds, SI.CUBIC_METRE);
        }
    };

    /**
     * The {@link Electricity} commodity. The billable unit is in kWh. The flow unit is in watt.
     *
     * This is a singleton object (see {@link Commodity#ELECTRICITY});
     */
    public static final class Electricity extends Commodity<Energy, Power> {
        private static final long serialVersionUID = 3597138377408173103L;

        Electricity() {
            super(NonSI.KWH, SI.WATT);
        }

        @Override
        public Measurable<Power> average(Measurable<Energy> amount, Measurable<Duration> duration) {
            double seconds = duration.doubleValue(SI.SECOND);
            if (seconds <= 0) {
                throw new IllegalArgumentException("invalid duration: " + seconds + " seconds");
            }
            return Measure.valueOf(amount.doubleValue(SI.JOULE) / seconds, SI.WATT);
        }

        @Override
        public Measurable<Energy> amount(Measurable<Power> average, Measurable<Duration> duration) {
            double seconds = duration.doubleValue(SI.SECOND);
            if (seconds <= 0) {
                throw new IllegalArgumentException("invalid duration: " + seconds + " seconds");
            }
            return Measure.valueOf(average.doubleValue(SI.WATT) * seconds, SI.JOULE);
        }
    };

    /**
     * The {@link Heat} commodity. The billable unit is in joule. The flow unit is in watt.
     *
     * This is a singleton object (see {@link Commodity#HEAT});
     */
    public static final class Heat extends Commodity<Energy, Power> {
        private static final long serialVersionUID = 3597138377408173103L;

        Heat() {
            super(SI.JOULE, SI.WATT);
        }

        @Override
        public Measurable<Power> average(Measurable<Energy> amount, Measurable<Duration> duration) {
            double seconds = duration.doubleValue(SI.SECOND);
            if (seconds <= 0) {
                throw new IllegalArgumentException("invalid duration: " + seconds + " seconds");
            }
            return Measure.valueOf(amount.doubleValue(SI.JOULE) / seconds, SI.WATT);
        }

        @Override
        public Measurable<Energy> amount(Measurable<Power> average, Measurable<Duration> duration) {
            double seconds = duration.doubleValue(SI.SECOND);
            if (seconds <= 0) {
                throw new IllegalArgumentException("invalid duration: " + seconds + " seconds");
            }
            return Measure.valueOf(average.doubleValue(SI.WATT) * seconds, SI.JOULE);
        }
    };

    private static final long serialVersionUID = 1L;

    private final Unit<BQ> billableUnit;
    private final Unit<FQ> flowUnit;

    Commodity(Unit<BQ> billableUnit, Unit<FQ> flowUnit) {
        this.billableUnit = billableUnit;
        this.flowUnit = flowUnit;
    }

    /**
     * @return The unit that is used for billing purposes. E.g. for electricity this is kWh, for gas m^3 is used. There
     *         will be helper methods to transform the flowUnit into billableUnit (see
     *         {@link #amount(Measurable, Measurable)} and {@link #average(Measurable, Measurable)}).
     */
    public Unit<BQ> getBillableUnit() {
        return billableUnit;
    }

    /**
     * @return The unit that indicates the rate at which a commodity is being consumed or produced. E.g. for electricity
     *         this would be power expressed in the watt unit (W), for gas m^3/s is used.
     */
    public Unit<FQ> getFlowUnit() {
        return flowUnit;
    }

    /**
     * @param amount
     *            The billable amount of this commodity
     * @param duration
     *            The duration over which the amount has been measured
     * @return The flow amount that represents the average flow, using the total amount sent over the given duration.
     */
    public abstract Measurable<FQ> average(Measurable<BQ> amount, Measurable<Duration> duration);

    /**
     * @param average
     *            The average flow amount of this commodity
     * @param duration
     *            The duration over which the flow has been measured
     * @return The billable amount that represents the total amount that has been measured
     */
    public abstract Measurable<BQ> amount(Measurable<FQ> average, Measurable<Duration> duration);

    @Override
    public String toString() {
        return getClass().getName();
    }
}
