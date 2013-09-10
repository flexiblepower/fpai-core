package org.flexiblepower.observation;

public interface ObservationConsumer<T> {
    void consume(ObservationProvider<? extends T> source, Observation<? extends T> observation);
}
