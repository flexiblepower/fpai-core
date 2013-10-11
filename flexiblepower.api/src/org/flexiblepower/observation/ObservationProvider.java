package org.flexiblepower.observation;

import org.flexiblepower.ral.ResourceDriver;

/**
 * The {@link ObservationProvider} is any object that wants to publish {@link Observation}s to
 * {@link ObservationConsumer}. When using OSGi, there are a couple of properties that should be registered to make it
 * easier for consumers to find the proper providers. <br/>
 * <code>org.flexiblepower.monitoring.observedBy</code>: describes the component that does the observations. Normally
 * this will be the name of the component itself. <br/>
 * <code>org.flexiblepower.monitoring.observationOf</code>: describes the physical thing that it is observing. Normally
 * this will be the resource identifier when using the {@link ResourceDriver}.<br/>
 * <code>org.flexiblepower.monitoring.type</code>: gives the full classname of the type of value that will be published
 * by the provider.<br/>
 * <code>org.flexiblepower.monitoring.type.X</code>: for each attribute, the X is replaced by the name of the attribute
 * (as will be returned by the {@link Observation#getValueMap() method} and the value is the type description, usually
 * the classname.<br/>
 * <code>org.flexiblepower.monitoring.type.X.unit</code>: optionally describes the unit in which the attribute will be
 * transformed. This is only relevant for raw values, like doubles or integers. <br/>
 * <code>org.flexiblepower.monitoring.type.X.optional</code>: optionally describes if an attribute is optional. By
 * default the consumer may assume that all attributes are available each time, but by setting this to true it can
 * indicate otherwise.
 * 
 * @author TNO
 * 
 * @param <T>
 *            The type of value that will be in the published observations.
 */
public interface ObservationProvider<T> {
    /**
     * Binds the consumer to this provider. After this call has returned, all the observations produced by the provider
     * should be sent to this consumer via its {@link ObservationConsumer#consume(ObservationProvider, Observation)}
     * method.
     * 
     * @param consumer
     *            The {@link ObservationConsumer} that will be bound to this provider.
     */
    void subscribe(ObservationConsumer<? super T> consumer);

    /**
     * Unbinds the consumer from this provider. After this call has returned, the observations must no longer be sent to
     * the consumer.
     * 
     * @param consumer
     *            The {@link ObservationConsumer} that will be unbound from this provider.
     */
    void unsubscribe(ObservationConsumer<? super T> consumer);
}
