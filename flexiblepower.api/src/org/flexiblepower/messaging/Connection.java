package org.flexiblepower.messaging;

/**
 * <p>
 * <b>This interface should never be implemented by users</b>
 * </p>
 */
public interface Connection {
    void sendMessage(Object message);

    Port getPort();
}
