package org.flexiblepower.messaging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.flexiblepower.ral.ResourceManager;

/**
 * The {@link Port} annotation should be put on {@link Endpoint}s that are registered in the service registry. It
 * indicates what kind of connections it can support (including what object it can send, receive and the cardinality).
 * If multiple ports need to be defined on an {@link Endpoint}, use the {@link Ports} annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.TYPE)
public @interface Port {
    /**
     * The name of the port. This is useful in the {@link Endpoint#onConnect(Connection)} method to determine which port
     * has been connected to. Should be unique for the Endpoint.
     *
     * This is the only required part of a {@link Port}. If no {@link #sends()} or {@link #accepts()} is defined, the
     * port has been defined without any implementation (like an interface). This is useful for specifying on an
     * interface that should use a specific port. For example, see the {@link ResourceManager}.
     *
     * @return The name of the port.
     */
    String name();

    /**
     * Specifies the type of objects that can be sent through this port. When the {@link ConnectionManager} will detect
     * which ports can be connected to each other, it will check if this set is a subset of the other {@link #accepts()}
     * set. The implementation of the {@link Endpoint} should never sent other types of objects through this port,
     * otherwise the behavior is unspecified (possible throws ClassCastExceptions).
     *
     * @return The type of objects that can be sent through this port.
     */
    Class<?>[] sends() default {};

    /**
     * Specifies the type of objects that can be received through this port. When this port is used in a
     * {@link Connection}, the {@link MessageHandler} should only receive objects of this types.
     *
     * @return The type of objects that can be received through this port.
     */
    Class<?>[] accepts() default {};

    /**
     * Specifies the cardinality of this port. When it is set to {@link Cardinality#SINGLE}, only one {@link Connection}
     * can be created on this port at the same time. If it is set to {@link Cardinality#MULTIPLE}, multiple connections
     * are possible and should be handled by the {@link Endpoint#onConnect(Connection)} method.
     *
     * @return The cardinality of this port.
     */
    Cardinality cardinality() default Cardinality.SINGLE;
}
