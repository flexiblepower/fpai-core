package org.flexiblepower.efi.values;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Quantity;

import org.flexiblepower.rai.values.Commodity;

/**
 * Represents the elements of an EnergyProfile.
 */
public class CommodityProfileElement<BU extends Quantity, FU extends Quantity> {
	private final Measurable<Duration> duration;
	private final Measurable<BU> amount;
	private Commodity<BU, FU> commodity;

	CommodityProfileElement(Commodity<BU, FU> commodity, Measurable<Duration> duration,
			Measurable<BU> amount) {
		if (duration == null || amount == null) {
			throw new NullPointerException();
		}
		this.commodity = commodity;
		this.duration = duration;
		this.amount = amount;
	}

	/**
	 * @return The duration of this element
	 */
	public Measurable<Duration> getDuration() {
		return duration;
	}

	/**
	 * @return The amount of energy of this element
	 */
	public Measurable<BU> getAmount() {
		return amount;
	}

	/**
	 * @return The average power that has been produced or consumed during
	 *         this period.
	 */
	public Measurable<FU> getAverage() {
		return commodity.average(amount, duration);
	}

	@Override
	public String toString() {
		return "(" + duration + "," + amount + ")";
	}
}
