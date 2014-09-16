package org.flexiblepower.efi.buffer;

public class LeakageRate {
    private final double leakageRate;

    public LeakageRate(double leakageRate) {
        this.leakageRate = leakageRate;
    }

    public double getLeakageRate() {
        return leakageRate;
    }

    public double getValue() {
        return leakageRate;
    }

    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(leakageRate);
        return 31 * (int) (temp ^ (temp >>> 32));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        LeakageRate other = (LeakageRate) obj;
        return Double.doubleToLongBits(leakageRate) == Double.doubleToLongBits(other.leakageRate);
    }

    @Override
    public String toString() {
        return "LeakageRate(" + leakageRate + ")";
    }
}
