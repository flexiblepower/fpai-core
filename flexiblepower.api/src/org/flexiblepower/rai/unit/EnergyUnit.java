package org.flexiblepower.rai.unit;

/**
 * PMSuite - PM Data Specification - v0.6
 * 
 * Enumeration of energy units.
 * <p>
 * This defines WattSec, WattHour, Joule, together with their multiples and fractions.
 * <p>
 */
public enum EnergyUnit implements Unit<EnergyUnit> {

    // Wh and variants
    YOCTO_WATTHOUR("Wh", PowerUnit.YOCTO_WATT, TimeUnit.HOURS),
    ZEPTO_WATTHOUR("Wh", PowerUnit.ZEPTO_WATT, TimeUnit.HOURS),
    ATTO_WATTHOUR("Wh", PowerUnit.ATTO_WATT, TimeUnit.HOURS),
    FEMTO_WATTHOUR("Wh", PowerUnit.FEMTO_WATT, TimeUnit.HOURS),
    PICO_WATTHOUR("Wh", PowerUnit.PICO_WATT, TimeUnit.HOURS),
    NANO_WATTHOUR("Wh", PowerUnit.NANO_WATT, TimeUnit.HOURS),
    MICRO_WATTHOUR("Wh", PowerUnit.MICRO_WATT, TimeUnit.HOURS),
    MILLI_WATTHOUR("Wh", PowerUnit.MILLI_WATT, TimeUnit.HOURS),
    CENTI_WATTHOUR("Wh", PowerUnit.CENTI_WATT, TimeUnit.HOURS),
    DECI_WATTHOUR("Wh", PowerUnit.DECI_WATT, TimeUnit.HOURS),
    WATTHOUR("Wh", PowerUnit.WATT, TimeUnit.HOURS),
    KILO_WATTHOUR("Wh", PowerUnit.KILO_WATT, TimeUnit.HOURS),
    MEGA_WATTHOUR("Wh", PowerUnit.MEGA_WATT, TimeUnit.HOURS),
    GIGA_WATTHOUR("Wh", PowerUnit.GIGA_WATT, TimeUnit.HOURS),
    TERA_WATTHOUR("Wh", PowerUnit.TERA_WATT, TimeUnit.HOURS),
    PETA_WATTHOUR("Wh", PowerUnit.PETA_WATT, TimeUnit.HOURS),
    EXA_WATTHOUR("Wh", PowerUnit.EXA_WATT, TimeUnit.HOURS),
    ZETA_WATTHOUR("Wh", PowerUnit.ZETA_WATT, TimeUnit.HOURS),
    YOTTA_WATTHOUR("Wh", PowerUnit.YOTTA_WATT, TimeUnit.HOURS),

    // Joule and variants
    YOCTO_JOULE("J", PowerUnit.YOCTO_WATT, TimeUnit.SECONDS),
    ZEPTO_JOULE("J", PowerUnit.ZEPTO_WATT, TimeUnit.SECONDS),
    ATTO_JOULE("J", PowerUnit.ATTO_WATT, TimeUnit.SECONDS),
    FEMTO_JOULE("J", PowerUnit.FEMTO_WATT, TimeUnit.SECONDS),
    PICO_JOULE("J", PowerUnit.PICO_WATT, TimeUnit.SECONDS),
    NANO_JOULE("J", PowerUnit.NANO_WATT, TimeUnit.SECONDS),
    MICRO_JOULE("J", PowerUnit.MICRO_WATT, TimeUnit.SECONDS),
    MILLI_JOULE("J", PowerUnit.MILLI_WATT, TimeUnit.SECONDS),
    CENTI_JOULE("J", PowerUnit.CENTI_WATT, TimeUnit.SECONDS),
    DECI_JOULE("J", PowerUnit.DECI_WATT, TimeUnit.SECONDS),
    JOULE("J", PowerUnit.WATT, TimeUnit.SECONDS),
    KILO_JOULE("J", PowerUnit.KILO_WATT, TimeUnit.SECONDS),
    MEGA_JOULE("J", PowerUnit.MEGA_WATT, TimeUnit.SECONDS),
    GIGA_JOULE("J", PowerUnit.GIGA_WATT, TimeUnit.SECONDS),
    TERA_JOULE("J", PowerUnit.TERA_WATT, TimeUnit.SECONDS),
    PETA_JOULE("J", PowerUnit.PETA_WATT, TimeUnit.SECONDS),
    EXA_JOULE("J", PowerUnit.EXA_WATT, TimeUnit.SECONDS),
    ZETA_JOULE("J", PowerUnit.ZETA_WATT, TimeUnit.SECONDS),
    YOTTA_JOULE("J", PowerUnit.YOTTA_WATT, TimeUnit.SECONDS);

    private String symbol;
    private PowerUnit unitPower;
    private TimeUnit unitTime;

    EnergyUnit(String symbolspec, PowerUnit unitPower, TimeUnit unitTime) {
        symbol = unitPower.getPrefix().getSymbol() + symbolspec;
        this.unitPower = unitPower;
        this.unitTime = unitTime;
    }

    @Override
    public double convertTo(double value, EnergyUnit other) {
        EnergyUnit o = other;
        double timeconvert = unitTime.convertTo(value, o.unitTime);
        double convertpow = unitPower.convertTo(timeconvert, o.unitPower);
        return convertpow;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }
}
