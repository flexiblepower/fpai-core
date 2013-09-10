package org.flexiblepower.rai.unit;

/**
 * PMSuite - PM Data Specification - v0.6
 * 
 * Enumeration of power units.
 * <p>
 * This defines Watt, together with their multiples and fractions.
 * <p>
 */
public enum PowerUnit implements Unit<PowerUnit> {

    YOCTO_WATT(SiPrefix.YOCTO),
    ZEPTO_WATT(SiPrefix.ZEPTO),
    ATTO_WATT(SiPrefix.ATTO),
    FEMTO_WATT(SiPrefix.FEMTO),
    PICO_WATT(SiPrefix.PICO),
    NANO_WATT(SiPrefix.NANO),
    MICRO_WATT(SiPrefix.MICRO),
    MILLI_WATT(SiPrefix.MILLI),
    CENTI_WATT(SiPrefix.CENTI),
    DECI_WATT(SiPrefix.DECI),
    WATT(SiPrefix.NONE),
    KILO_WATT(SiPrefix.KILO),
    MEGA_WATT(SiPrefix.MEGA),
    GIGA_WATT(SiPrefix.GIGA),
    TERA_WATT(SiPrefix.TERA),
    PETA_WATT(SiPrefix.PETA),
    EXA_WATT(SiPrefix.EXA),
    ZETA_WATT(SiPrefix.ZETTA),
    YOTTA_WATT(SiPrefix.YOTTA);

    private String symbol;
    private SiPrefix prefix;

    PowerUnit(SiPrefix multiplier) {
        symbol = multiplier.getSymbol() + "W";
        prefix = multiplier;
    }

    @Override
    public double convertTo(double value, PowerUnit other) {
        return prefix.to(value, other.prefix);
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    public SiPrefix getPrefix() {
        return prefix;
    }
}
