package org.flexiblepower.efi.values;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;

import org.flexiblepower.rai.values.Commodity;

public class ElectricityProfile extends CommodityProfile<Energy, Power> {

	public ElectricityProfile(CommodityProfileElement<Energy, Power>[] profile) {
		super(Commodity.ELECTRICITY, profile);
	}
}
