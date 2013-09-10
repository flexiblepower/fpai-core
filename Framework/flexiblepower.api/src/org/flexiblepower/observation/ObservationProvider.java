package org.flexiblepower.observation;

public interface ObservationProvider<T> {
    void subscribe(ObservationConsumer<? super T> consumer);

    void unsubscribe(ObservationConsumer<? super T> consumer);
}
