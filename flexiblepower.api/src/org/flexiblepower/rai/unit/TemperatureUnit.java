package org.flexiblepower.rai.unit;

/**
 * PMSuite - PM Data Specification - v0.6
 * 
 * Enumeration of power units.
 * <p>
 * This defines Watt, together with their multiples and fractions.
 * <p>
 */
public enum TemperatureUnit implements Unit<TemperatureUnit> {

    CELCIUS("C"), KELVIN("K"), FAHRENHEIT("F");

    private final String symbol;

    TemperatureUnit(String symbol) {
        this.symbol = symbol;
    }

    @Override
    @SuppressWarnings("incomplete-switch")
    public double convertTo(double value, TemperatureUnit other) {
        if (equals(other)) {
            return value;
        }

        switch (this) {

        case FAHRENHEIT:
            switch (other) {
            case CELCIUS:
                return 5 / 9 * (value - 32);
            case KELVIN:
                return 5 / 9 * (value + 273.15 - 32);
            }

        case CELCIUS:
            switch (other) {
            case FAHRENHEIT:
                return 9 / 5 * (value + 32);
            case KELVIN:
                return value - 273.15;
            }

        case KELVIN:
            switch (other) {
            case FAHRENHEIT:
                return 9 / 5 * (value + 273.15 + 32);
            case CELCIUS:
                return value + 273.15;
            }

        }

        throw new UnsupportedOperationException("Conversion from temperature in unit " + getSymbol()
                                                + " to "
                                                + other.getSymbol()
                                                + " is not implemented");
    }

    @Override
    public String getSymbol() {
        return symbol;
    }
}
