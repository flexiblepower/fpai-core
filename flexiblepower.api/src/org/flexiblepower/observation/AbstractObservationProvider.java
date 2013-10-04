package org.flexiblepower.observation;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class AbstractObservationProvider<T> implements ObservationProvider<T> {
    private final Set<ObservationConsumer<? super T>> consumers = new CopyOnWriteArraySet<ObservationConsumer<? super T>>();

    @Override
    public void subscribe(ObservationConsumer<? super T> consumer) {
        consumers.add(consumer);
    }

    @Override
    public void unsubscribe(ObservationConsumer<? super T> consumer) {
        consumers.remove(consumer);
    }

    protected void publish(Observation<T> observation) {
        for (ObservationConsumer<? super T> consumer : consumers) {
            consumer.consume(this, observation);
        }
    }
}
