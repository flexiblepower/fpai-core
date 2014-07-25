package org.flexiblepower.rai.values;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

public class CommodityProfile<BQ extends Quantity, FQ extends Quantity> extends
                                                                        Profile<CommodityProfileElement<BQ, FQ>> {

    public CommodityProfile(CommodityProfileElement<BQ, FQ>[] elements) {
        super(elements);
        validate();
    }

    private void validate() {
        // Check if profile is empty
        if (elements.length == 0) {
            throw new IllegalArgumentException("A CommodityProfile cannot be empty");
        }
        // Check if all the commodities are the same
        final Commodity<BQ, FQ> commodity = elements[0].getCommodity();
        for (int i = 1; i < elements.length; i++) {
            if (elements[i].getCommodity() != commodity) {
                throw new IllegalArgumentException("A CommodityProfile can only consist of commodites of the same type");
            }
        }
    }

    public Commodity<BQ, FQ> getCommodity() {
        // Validate makes sure there is at least one element
        return elements[0].getCommodity();
    }

    public Measurable<BQ> getTotalAmount() {
        double amount = 0;
        final Unit<BQ> billableUnit = getCommodity().getBillableUnit();
        for (final CommodityProfileElement<BQ, FQ> e : elements) {
            amount += e.getAmount().doubleValue(billableUnit);
        }
        return Measure.valueOf(amount, billableUnit);
    }

    public Measurable<FQ> getAverage() {
        return getCommodity().average(getTotalAmount(), getTotalDuration());
    }

}
