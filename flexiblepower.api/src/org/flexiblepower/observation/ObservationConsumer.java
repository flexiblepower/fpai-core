package org.flexiblepower.observation;

/**
 * An {@link ObservationConsumer} is any object that is interested in observations of {@link ObservationProvider}s. It
 * only contains the {@link #consume(ObservationProvider, Observation)} method that will be called by
 * {@link ObservationProvider}s when the consumer is registered with them.
 * 
 * @author TNO
 * @param <T>
 *            The type of observation values that can be consumed.
 */
public interface ObservationConsumer<T> {
    /**
     * This method should be call by any {@link ObservationProvider} to which this consumer is bound.
     * 
     * @param source
     *            The {@link ObservationProvider} that has sent the observation.
     * @param observation
     *            The {@link Observation} that has been sent.
     * @throws NullPointerException
     *             When the source or the observation is <code>null</code>.
     */
    void consume(ObservationProvider<? extends T> source, Observation<? extends T> observation);
}
