package org.flexiblepower.data;

public interface DataChangeListener<T extends IdentifyableObject> {
    void dataChanged(DataChangeEvent<T> evt);
}
