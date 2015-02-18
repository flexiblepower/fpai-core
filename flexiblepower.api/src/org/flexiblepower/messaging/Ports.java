package org.flexiblepower.messaging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When defining more that one {@link Port} on and {@link Endpoint}, this annotation can be used to group them. It is
 * not possible in Java to define multiple {@link Port} annotations on the same object.
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.TYPE)
public @interface Ports {
    /**
     * @return The specified {@link Port}s on the {@link Endpoint}.
     */
    Port[] value();
}
