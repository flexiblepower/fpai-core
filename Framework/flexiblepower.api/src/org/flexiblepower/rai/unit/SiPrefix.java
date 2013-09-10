package org.flexiblepower.rai.unit;

/**
 * PMSuite - PM Data Specification - v0.6
 * 
 * Enumeration to define multipliers, corresponding with the SI multiples and fraction prefixes
 */
public enum SiPrefix {
    YOTTA("Yotta", "Y", 24, "septillion"),
    ZETTA("Zetta", "Z", 21, "sextillion"),
    EXA("Exa", "E", 18, "quintillion"),
    PETA("Peta", "P", 15, "quadrillion"),
    TERA("Tera", "T", 12, "trillion"),
    GIGA("Giga", "G", 9, "billion"),
    MEGA("Mega", "M", 6, "million"),
    KILO("Kilo", "k", 3, "thousand"),
    HECTO("Hecto", "h", 2, "hundred"),
    DECA("Deca", "da", 1, "ten"),
    NONE("", "", 0, "one"),
    DECI("Deci", "d", -1, "tenth"),
    CENTI("Centi", "c", -2, "hundredth"),
    MILLI("Milli", "m", -3, "thousandth"),
    MICRO("Micro", "µ", -6, "millionth"),
    NANO("Nano", "n", -9, "billionth"),
    PICO("Pico", "p", -12, "trillionth"),
    FEMTO("Femto", "f", -15, "quadrillionth"),
    ATTO("Atto", "a", -18, "quintillionth"),
    ZEPTO("Zepto", "z", -21, "sextillionth"),
    YOCTO("Yocto", "y", -24, "septillionth");

    private String prefix;
    private String symbol;
    private int exponent;
    private String name;

    private SiPrefix(String prefix, String symbol, int exponent, String name) {
        this.prefix = prefix;
        this.symbol = symbol;
        this.exponent = exponent;
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getExponent() {
        return exponent;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return symbol;
    }

    public double to(double value, SiPrefix targetMultiplier) {
        int exp = exponent - targetMultiplier.exponent;
        return value * Math.pow(10, exp);
    }
}
