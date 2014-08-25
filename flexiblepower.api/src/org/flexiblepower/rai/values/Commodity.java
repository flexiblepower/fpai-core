package org.flexiblepower.rai.values;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import javax.measure.quantity.Quantity;
import javax.measure.quantity.Volume;
import javax.measure.quantity.VolumetricFlowRate;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

/**
 * This abstract class contains information about the units that are used for a particular commodity. There are
 * currently implementations for the commodities ELECTRICITY and GAS.
 *
 * @author TNO
 *
 * @param <BQ>
 *            Quantity of the Billable Unit
 * @param <FQ>
 *            Quantity of the Flow Unit
 */
public abstract class Commodity<BQ extends Quantity, FQ extends Quantity> implements Serializable {

    public static class Set extends AbstractSet<Commodity<?, ?>> {
        public static final Set onlyElectricity = new Set(true, false);
        public static final Set onlyGas = new Set(false, true);

        private final boolean hasElectricity, hasGas;

        public Set(boolean hasElectricity, boolean hasGas) {
            this.hasElectricity = hasElectricity;
            this.hasGas = hasGas;
        }

        public Set(Collection<Commodity<?, ?>> collection) {
            this.hasElectricity = collection.contains(ELECTRICITY);
            this.hasGas = collection.contains(GAS);
        }

        @Override
        public boolean contains(Object o) {
            return (hasElectricity && o == ELECTRICITY) || (hasGas && o == GAS);
        }

        @Override
        public Iterator<Commodity<?, ?>> iterator() {
            return new Iterator<Commodity<?, ?>>() {
                private boolean returnedElectricity = !hasElectricity;
                private boolean returnedGas = !hasGas;

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Commodity<?, ?> next() {
                    if (!returnedElectricity) {
                        returnedElectricity = true;
                        return ELECTRICITY;
                    } else if (!returnedGas) {
                        returnedGas = true;
                        return GAS;
                    } else {
                        throw new NoSuchElementException();
                    }
                }

                @Override
                public boolean hasNext() {
                    return !returnedElectricity || !returnedGas;
                }
            };
        }

        @Override
        public int size() {
            return (hasElectricity ? 1 : 0) + (hasGas ? 1 : 0);
        }
    }

    public static class Map<V> implements java.util.Map<Commodity<?, ?>, V> {
        private final V gasValue, electricityValue;

        public Map(V electricityValue, V gasValue) {
            this.electricityValue = electricityValue;
            this.gasValue = gasValue;
        }

        @Override
        public int size() {
            return (gasValue != null ? 1 : 0) + (electricityValue != null ? 1 : 0);
        }

        @Override
        public boolean isEmpty() {
            return size() == 0;
        }

        @Override
        public boolean containsKey(Object key) {
            if (key == ELECTRICITY) {
                return electricityValue != null;
            } else if (key == GAS) {
                return gasValue != null;
            } else {
                return false;
            }
        }

        @Override
        public boolean containsValue(Object value) {
            return value.equals(electricityValue) || value.equals(gasValue);
        }

        @Override
        public V get(Object key) {
            if (key == ELECTRICITY) {
                return electricityValue;
            } else if (key == GAS) {
                return gasValue;
            } else {
                return null;
            }
        }

        @Override
        public V put(Commodity<?, ?> key, V value) {
            throw new UnsupportedOperationException("Unmodifiable Map");
        }

        @Override
        public V remove(Object key) {
            throw new UnsupportedOperationException("Unmodifiable Map");
        }

        @Override
        public void putAll(java.util.Map<? extends Commodity<?, ?>, ? extends V> m) {
            throw new UnsupportedOperationException("Unmodifiable Map");
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException("Unmodifiable Map");
        }

        private class Entry implements java.util.Map.Entry<Commodity<?, ?>, V> {
            private final Commodity<?, ?> commodity;
            private final V value;

            Entry(Commodity<?, ?> commodity, V value) {
                this.commodity = commodity;
                this.value = value;
            }

            @Override
            public Commodity<?, ?> getKey() {
                return commodity;
            }

            @Override
            public V getValue() {
                return value;
            }

            @Override
            public V setValue(V value) {
                throw new UnsupportedOperationException();
            }
        }

        private transient java.util.Set<java.util.Map.Entry<Commodity<?, ?>, V>> entrySet;

        @Override
        public java.util.Set<java.util.Map.Entry<Commodity<?, ?>, V>> entrySet() {
            if (entrySet == null) {
                entrySet = new HashSet<java.util.Map.Entry<Commodity<?, ?>, V>>();
                if (electricityValue != null) {
                    entrySet.add(new Entry(ELECTRICITY, electricityValue));
                }
                if (gasValue != null) {
                    entrySet.add(new Entry(GAS, gasValue));
                }
                entrySet = Collections.unmodifiableSet(entrySet);
            }
            return entrySet;
        }

        @Override
        public Set keySet() {
            return new Set(electricityValue != null, gasValue != null);
        }

        @Override
        public Collection<V> values() {
            Collection<V> result = new ArrayList<V>(2);
            if (electricityValue != null) {
                result.add(electricityValue);
            }
            if (gasValue != null) {
                result.add(gasValue);
            }
            return result;
        }
    }

    public static final class Measurements extends Map<Measurable<?>> {
        public Measurements(Measurable<Power> electricityValue, Measurable<VolumetricFlowRate> gasValue) {
            super(electricityValue, gasValue);
        }

        @SuppressWarnings("unchecked")
        public <FQ extends Quantity> Measurable<FQ> get(Commodity<?, FQ> commodity) {
            return (Measurable<FQ>) super.get(commodity);
        }
    }

    public static final Gas GAS = Gas.instance;
    public static final Electricity ELECTRICITY = Electricity.instance;

    public static final class Gas extends Commodity<Volume, VolumetricFlowRate> {
        private static final long serialVersionUID = 363346706724665032L;

        public static final Gas instance = new Gas();

        private Gas() {
            super(SI.CUBIC_METRE, NonSI.CUBIC_METRE_PER_SECOND);
        }

        @Override
        public Measurable<VolumetricFlowRate> average(Measurable<Volume> amount, Measurable<Duration> duration) {
            double seconds = duration.doubleValue(SI.SECOND);
            if (seconds <= 0) {
                throw new IllegalArgumentException("invalid duration: " + seconds + " seconds");
            }
            return Measure.valueOf(amount.doubleValue(SI.CUBIC_METRE) / seconds, NonSI.CUBIC_METRE_PER_SECOND);
        }

        @Override
        public Measurable<Volume> amount(Measurable<VolumetricFlowRate> average, Measurable<Duration> duration) {
            double seconds = duration.doubleValue(SI.SECOND);
            if (seconds <= 0) {
                throw new IllegalArgumentException("invalid duration: " + seconds + " seconds");
            }
            return Measure.valueOf(average.doubleValue(NonSI.CUBIC_METRE_PER_SECOND) * seconds, SI.CUBIC_METRE);
        }
    };

    public static final class Electricity extends Commodity<Energy, Power> {
        private static final long serialVersionUID = 3597138377408173103L;

        public static final Electricity instance = new Electricity();

        private Electricity() {
            super(NonSI.KWH, SI.WATT);
        }

        @Override
        public Measurable<Power> average(Measurable<Energy> amount, Measurable<Duration> duration) {
            double seconds = duration.doubleValue(SI.SECOND);
            if (seconds <= 0) {
                throw new IllegalArgumentException("invalid duration: " + seconds + " seconds");
            }
            return Measure.valueOf(amount.doubleValue(SI.JOULE) / seconds, SI.WATT);
        }

        @Override
        public Measurable<Energy> amount(Measurable<Power> average, Measurable<Duration> duration) {
            double seconds = duration.doubleValue(SI.SECOND);
            if (seconds <= 0) {
                throw new IllegalArgumentException("invalid duration: " + seconds + " seconds");
            }
            return Measure.valueOf(average.doubleValue(SI.WATT) * seconds, SI.JOULE);
        }
    };

    private static final long serialVersionUID = 1L;

    // TODO do we have better names for these?
    private final Unit<BQ> billableUnit;
    private final Unit<FQ> flowUnit;

    public Commodity(Unit<BQ> billableUnit, Unit<FQ> flowUnit) {
        this.billableUnit = billableUnit;
        this.flowUnit = flowUnit;
    }

    /**
     * The unit that is used for billing purposes. E.g. for electricity this is kWh, for gas m3 is used.
     *
     * @return Billalbe Unit of the commodit
     */
    public Unit<BQ> getBillableUnit() {
        return billableUnit;
    }

    /**
     * The unit that indicates the rate at which a commodity is being consumed or produced. E.g. for electricity this
     * would be power expressed in the watt unit (W), for gas m3/s is used.
     *
     * @return Flow Unit of the commodity
     */
    public Unit<FQ> getFlowUnit() {
        return flowUnit;
    }

    public abstract Measurable<FQ> average(Measurable<BQ> amount, Measurable<Duration> duration);

    public abstract Measurable<BQ> amount(Measurable<FQ> average, Measurable<Duration> duration);

}
