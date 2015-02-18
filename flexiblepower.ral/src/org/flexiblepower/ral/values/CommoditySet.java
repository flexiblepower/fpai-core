package org.flexiblepower.ral.values;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A utility class that efficiently stores which {@link Commodity} instances are in this {@link Set}. It does not store
 * real references, but instead keeps track using boolean values.
 */
public class CommoditySet extends AbstractSet<Commodity<?, ?>> {
    /**
     * An empty set (will return false for all contains methods).
     */
    public static final CommoditySet empty = new CommoditySet(false, false, false);
    /**
     * The CommoditySet that will only contain {@link Commodity#ELECTRICITY}
     */
    public static final CommoditySet onlyElectricity = new CommoditySet(true, false, false);
    /**
     * The CommoditySet that will only contain {@link Commodity#GAS}
     */
    public static final CommoditySet onlyGas = new CommoditySet(false, true, false);
    /**
     * The CommoditySet that will only contain {@link Commodity#HEAT}
     */
    public static final CommoditySet onlyHeat = new CommoditySet(false, false, true);

    /**
     * <p>
     * This helper class makes it easy to create an instance of the {@link CommoditySet}. To create an instance of this
     * class, use {@link CommoditySet#create()}.
     * </p>
     *
     * <p>
     * Typical usage looks like this:
     * </p>
     *
     * <p>
     * <code>CommoditySet commoditySet = CommoditySet.create().addElectricity().addGas().build();</code>
     * </p>
     */
    public static class Builder {
        private boolean hasElectricity, hasGas, hasHeat;

        public Builder add(Commodity<?, ?> commodity) {
            if (commodity == Commodity.ELECTRICITY) {
                hasElectricity = true;
            } else if (commodity == Commodity.GAS) {
                hasGas = true;
            } else if (commodity == Commodity.HEAT) {
                hasHeat = true;
            }
            return this;
        }

        public Builder addElectricity() {
            hasElectricity = true;
            return this;
        }

        public Builder addGas() {
            hasGas = true;
            return this;
        }

        public Builder addHeat() {
            hasHeat = true;
            return this;
        }

        public CommoditySet build() {
            return new CommoditySet(hasElectricity, hasGas, hasHeat);
        }
    }

    /**
     * @return A {@link Builder} to easily create your own CommoditySet
     */
    public static Builder create() {
        return new Builder();
    }

    private final boolean hasElectricity, hasGas, hasHeat;

    CommoditySet(boolean hasElectricity, boolean hasGas, boolean hasHeat) {
        this.hasElectricity = hasElectricity;
        this.hasGas = hasGas;
        this.hasHeat = hasHeat;
    }

    /**
     * The copy constructor.
     *
     * @param collection
     *            The other collection from which will be checked if one of the commodities should be in this set.
     */
    public CommoditySet(Collection<Commodity<?, ?>> collection) {
        hasElectricity = collection.contains(Commodity.ELECTRICITY);
        hasGas = collection.contains(Commodity.GAS);
        hasHeat = collection.contains(Commodity.HEAT);
    }

    @Override
    public boolean contains(Object o) {
        return (hasElectricity && o == Commodity.ELECTRICITY) || (hasGas && o == Commodity.GAS);
    }

    @Override
    public Iterator<Commodity<?, ?>> iterator() {
        return new Iterator<Commodity<?, ?>>() {
            private boolean returnedElectricity = !hasElectricity;
            private boolean returnedGas = !hasGas;
            private boolean returnedHeat = !hasHeat;

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Commodity<?, ?> next() {
                if (!returnedElectricity) {
                    returnedElectricity = true;
                    return Commodity.ELECTRICITY;
                } else if (!returnedGas) {
                    returnedGas = true;
                    return Commodity.GAS;
                } else if (!returnedHeat) {
                    returnedHeat = true;
                    return Commodity.HEAT;
                } else {
                    throw new NoSuchElementException();
                }
            }

            @Override
            public boolean hasNext() {
                return !returnedElectricity || !returnedGas || !returnedHeat;
            }
        };
    }

    @Override
    public int size() {
        return (hasElectricity ? 1 : 0) + (hasGas ? 1 : 0);
    }

    @Override
    public boolean isEmpty() {
        return !(hasElectricity || hasGas || hasHeat);
    }
}
