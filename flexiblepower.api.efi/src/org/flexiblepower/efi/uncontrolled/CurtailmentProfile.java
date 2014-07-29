package org.flexiblepower.efi.uncontrolled;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Quantity;

import org.flexiblepower.efi.uncontrolled.CurtailmentProfile.CurtailmentProfileElement;
import org.flexiblepower.rai.values.Commodity;
import org.flexiblepower.rai.values.Profile;
import org.flexiblepower.rai.values.ProfileElement;

public class CurtailmentProfile<FQ extends Quantity> extends Profile<CurtailmentProfileElement<FQ>> {

    private final Commodity<?, FQ> commodity;

    public CurtailmentProfile(Commodity<?, FQ> commodity, CurtailmentProfileElement<FQ>[] elements) {
        super(elements);
        this.commodity = commodity;
    }

    public Commodity<?, FQ> getCommodity() {
        return commodity;
    }

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
