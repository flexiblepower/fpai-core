package org.flexiblepower.rai.values;

import javax.measure.quantity.Quantity;

/**
 * Class for representing an commodity consumption / production forecast over time. This class is similar to
 * {@link ProfileElement}, with the addition of uncertainty (see {@link UncertainMeasure}) in both amount and time.
 * 
 * @param <BQ>
 *            Billable Quantity, see {@link Commodity}
 * @param <FQ>
 *            Flow Quantity, see {@link Commodity}
 */
public class CommodityForecast<BQ extends Quantity, FQ extends Quantity> extends
                                                                         Profile<CommodityForecastElement<BQ, FQ>> {

    public CommodityForecast(CommodityForecastElement<BQ, FQ>[] elements) {
        super(elements);
    }

}
