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

public abstract class Commodity<BQ extends Quantity, FQ extends Quantity> implements Serializable {

    public static final Commodity<Volume, VolumetricFlowRate> GAS = new Commodity<Volume, VolumetricFlowRate>(SI.CUBIC_METRE,
                                                                                                              NonSI.CUBIC_METRE_PER_SECOND) {
        private static final long serialVersionUID = 363346706724665032L;

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

    public static final Commodity<Energy, Power> ELECTRICITY = new Commodity<Energy, Power>(NonSI.KWH, SI.WATT) {
        private static final long serialVersionUID = 3597138377408173103L;

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

    // TODO do we have better names for these?
    private final Unit<BQ> billableUnit;
    private final Unit<FQ> flowUnit;

    public Commodity(Unit<BQ> billableUnit, Unit<FQ> flowUnit) {
        this.billableUnit = billableUnit;
        this.flowUnit = flowUnit;
    }

    public Unit<BQ> getBillableUnit() {
        return billableUnit;
    }

    public Unit<FQ> getFlowUnit() {
        return flowUnit;
    }

    public abstract Measurable<FQ> average(Measurable<BQ> amount, Measurable<Duration> duration);

    public abstract Measurable<BQ> amount(Measurable<FQ> average, Measurable<Duration> duration);

}
