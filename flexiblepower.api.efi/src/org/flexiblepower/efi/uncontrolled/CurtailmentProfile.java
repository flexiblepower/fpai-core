package org.flexiblepower.efi.uncontrolled;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Power;
import javax.measure.quantity.Quantity;
import javax.measure.quantity.VolumetricFlowRate;

import org.flexiblepower.efi.uncontrolled.CurtailmentProfile.CurtailmentProfileElement;
import org.flexiblepower.rai.values.Commodity;
import org.flexiblepower.rai.values.CommodityMap;
import org.flexiblepower.rai.values.Profile;
import org.flexiblepower.rai.values.ProfileElement;

/**
 * A CurtailmentProfile is a profile that describes the maximum consumption or production of an appliance for a certain
 * commodity over a certain amount of time. A CurtailmentProfile is build out of one or more CurtailmentProfileElements
 * which can have dissimilar durations.
 *
 * @author TNO
 *
 * @param <FQ>
 *            Quantity of the profile, see {@link Commodity}
 */

public class CurtailmentProfile<FQ extends Quantity> extends Profile<CurtailmentProfileElement<FQ>> {
    public static final class Map extends Map<CurtailmentProfile<?>> {
        public Map(CurtailmentProfile<Power> electricityValue, CurtailmentProfile<VolumetricFlowRate> gasValue) {
            super(electricityValue, gasValue);
        }

        @SuppressWarnings("unchecked")
        public <FQ extends Quantity> CurtailmentProfile<FQ> get(Commodity<?, FQ> commodity) {
            return (CurtailmentProfile<FQ>) super.get(commodity);
        }
    }

    /**
     * The commodity for which the CurtailmentProfile is valid.
     */
    private final Commodity<?, FQ> commodity;

    public CurtailmentProfile(Commodity<?, FQ> commodity, CurtailmentProfileElement<FQ>[] elements) {
        super(elements);
        this.commodity = commodity;
    }

    public Commodity<?, FQ> getCommodity() {
        return commodity;
    }

    /**
     * The CurtailmentProfileElement describes the maximum consumption or production of an appliance for a certain
     * duration.
     */
    public static class CurtailmentProfileElement<FQ extends Quantity> implements
                                                                       ProfileElement<CurtailmentProfileElement<FQ>> {

        private final Measurable<Duration> duration;
        private final Measurable<FQ> maxConsumption;
        private final Measurable<FQ> maxProduction;

        public CurtailmentProfileElement(Measurable<Duration> duration,
                                         Measurable<FQ> maxConsumption,
                                         Measurable<FQ> maxProduction) {
            super();
            this.duration = duration;
            this.maxConsumption = maxConsumption;
            this.maxProduction = maxProduction;
        }

        @Override
        public Measurable<Duration> getDuration() {
            return duration;
        }

        @Override
        public CurtailmentProfileElement<FQ> subProfile(Measurable<Duration> offset, Measurable<Duration> duration) {
            return new CurtailmentProfileElement<FQ>(duration, maxConsumption, maxProduction);
        }
    }

}
