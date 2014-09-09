package org.flexiblepower.rai.values;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

/**
 * A utility class that efficiently stores values for each type of {@link Commodity}. This map has less overheat than
 * the {@link HashMap} or {@link TreeMap} implementations, because it just stores 1 reference for each commodity type.
 *
 * @param <V>
 *            The type of the value that can be stored in this map
 */
public class CommodityMap<V> implements Map<Commodity<?, ?>, V> {
    private final V gasValue, electricityValue, heatValue;

    CommodityMap(V electricityValue, V gasValue, V heatValue) {
        this.electricityValue = electricityValue;
        this.gasValue = gasValue;
        this.heatValue = heatValue;
    }

    /**
     * Copy constructor
     *
     * @param source
     *            The base map from which the values will be copied
     */
    public CommodityMap(Map<Commodity<?, ?>, V> source) {
        this.electricityValue = source.get(Commodity.ELECTRICITY);
        this.gasValue = source.get(Commodity.GAS);
        this.heatValue = source.get(Commodity.HEAT);
    }

    @Override
    public int size() {
        return (gasValue != null ? 1 : 0) + (electricityValue != null ? 1 : 0) + (heatValue != null ? 1 : 0);
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == Commodity.ELECTRICITY) {
            return electricityValue != null;
        } else if (key == Commodity.GAS) {
            return gasValue != null;
        } else if (key == Commodity.HEAT) {
            return heatValue != null;
        } else {
            return false;
        }
    }

    @Override
    public boolean containsValue(Object value) {
        return value.equals(electricityValue) || value.equals(gasValue) || value.equals(heatValue);
    }

    @Override
    public V get(Object key) {
        if (key == Commodity.ELECTRICITY) {
            return electricityValue;
        } else if (key == Commodity.GAS) {
            return gasValue;
        } else if (key == Commodity.HEAT) {
            return heatValue;
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
                entrySet.add(new Entry(Commodity.ELECTRICITY, electricityValue));
            }
            if (gasValue != null) {
                entrySet.add(new Entry(Commodity.GAS, gasValue));
            }
            if (heatValue != null) {
                entrySet.add(new Entry(Commodity.HEAT, heatValue));
            }
            entrySet = Collections.unmodifiableSet(entrySet);
        }
        return entrySet;
    }

    @Override
    public CommoditySet keySet() {
        return new CommoditySet(electricityValue != null, gasValue != null, heatValue != null);
    }

    @Override
    public Collection<V> values() {
        Collection<V> result = new ArrayList<V>(3);
        if (electricityValue != null) {
            result.add(electricityValue);
        }
        if (gasValue != null) {
            result.add(gasValue);
        }
        if (heatValue != null) {
            result.add(heatValue);
        }
        return result;
    }
}
