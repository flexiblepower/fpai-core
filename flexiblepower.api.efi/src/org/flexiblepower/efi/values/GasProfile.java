package org.flexiblepower.efi.values;

import javax.measure.quantity.Volume;
import javax.measure.quantity.VolumetricFlowRate;

import org.flexiblepower.rai.values.Commodity;

public class GasProfile extends CommodityProfile<Volume, VolumetricFlowRate> {

	public GasProfile(
			CommodityProfileElement<Volume, VolumetricFlowRate>[] profile) {
		super(Commodity.GAS, profile);
	}
}
