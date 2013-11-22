package org.flexiblepower.observation.ext;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.flexiblepower.observation.Observation;
import org.flexiblepower.observation.ObservationConsumer;
import org.flexiblepower.observation.ObservationProvider;

/**
 * Gives a basic implementation of an {@link ObservationProvider} where the {@link #subscribe(ObservationConsumer)} and
 * {@link #unsubscribe(ObservationConsumer)} methods are implemented. To publish a new observation, the
 * {@link #publish(Observation)} method should be used.
 * 
 * @param <T>
 *            The type of the value
 */
public abstract class AbstractObservationProvider<T> implements ObservationProvider<T> {
    private final Set<ObservationConsumer<? super T>> consumers = new CopyOnWriteArraySet<ObservationConsumer<? super T>>();

    @Override
    public final void subscribe(ObservationConsumer<? super T> consumer) {
        consumers.add(consumer);
    }

    @Override
    public final void unsubscribe(ObservationConsumer<? super T> consumer) {
        consumers.remove(consumer);
    }

    /**
     * Publishes an observation to all the subscribed consumers.
     * 
     * @param observation
     *            The observation that will be sent.
     */
    protected final void publish(Observation<? extends T> observation) {
        for (ObservationConsumer<? super T> consumer : consumers) {
            consumer.consume(this, observation);
        }
    }
}
