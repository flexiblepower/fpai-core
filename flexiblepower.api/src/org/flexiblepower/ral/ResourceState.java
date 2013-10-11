package org.flexiblepower.ral;

/**
 * This is a marker interface for describing the state of a {@link ResourceDriver}. Extensions of this interface should
 * only define getter in a Java bean style that each return the parameter for its state.
 */
public interface ResourceState {
    /**
     * @return <code>true</code> when the driver has a connection with the device, <code>false</code> otherwise.
     */
    boolean isConnected();
}
