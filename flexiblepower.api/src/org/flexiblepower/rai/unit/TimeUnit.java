package org.flexiblepower.rai.unit;

/**
 * PMSuite - PM Data Specification - v0.6
 * 
 * Enumeration of time units.
 * <p>
 * This defines ms(millisecond), s(second), m(minute), h(hour) and d(day)
 */
public enum TimeUnit implements Unit<TimeUnit> {

    MILLISECONDS("ms", 1), SECONDS("s", 1000L), MINUTES("m", 60000L), HOURS("h", 3600000L), DAYS("d", 86400000L);

    private long base;
    private String symbol;

    TimeUnit(String symbol, long base) {
        this.symbol = symbol;
        this.base = base;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    public long getMilliSeconds() {
        return base;
    }

    @Override
    public double convertTo(double value, TimeUnit other) {
        return value * ((double) base / (double) other.base);
    }
}
