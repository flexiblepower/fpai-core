package org.flexiblepower.efi.values;

import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;

import org.flexiblepower.rai.values.Commodity;

public class ElectricityProfile extends CommodityProfile<Energy, Power> {

	ElectricityProfile(Element<Energy, Power>[] profile) {
		super(Commodity.ELECTRICITY, profile);
	}

}
