package org.flexiblepower.observation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This attribute can be used on value types to let the {@link ObservationProviderRegistrationHelper} register the value
 * types correctly in the service registry.
 * 
 * @author TNO
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface ObservationAttribute {
    /**
     * @return The unit of the attribute.
     */
    String unit() default "";

    /**
     * @return If the attribute is optional or not. By default it is not.
     */
    boolean optional() default false;
}
