package org.flexiblepower.rai.unit;

/**
 * PMSuite - PM Data Specification - v0.6
 * 
 * Interface of all measure units.
 */
public interface Unit<U extends Unit<U>> {

    /**
     * A unit has a symbol (for example "kW" for UnitPower.kiloWatt)
     * 
     * @return the symbol of this unit
     */
    public String getSymbol();

    /**
     * converts a value in this unit to the amount in the other unit. This unit and other should be from the same class,
     * so representing the same quantity; otherwise 0 is returned.
     * <p>
     * Example: UnitEnergy.kiloJoule.convertTo(7200.0,UnitEnergy.kiloWattHour)
     * 
     * @param value
     *            is an amount measured in this unit
     * @param other
     *            is another unit
     * @return the amount of this value in the other unit, if other is from the same class, 0 otherwise
     */
    public double convertTo(double value, U other);
}
