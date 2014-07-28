package org.flexiblepower.messaging;

import aQute.bnd.annotation.metatype.Meta;

/**
 * An interface that can be used for the generating a default configuration for an endpoint in metatype.
 */
@Meta.OCD(name = "Endpoint configuration", factory = false)
public interface EndpointConfig {
    @Meta.AD(description = "The name(s) of the topic(s) on which the connection should be set-up.",
            name = "Message Topic",
            required = true)
    String[] messaging_topic();

    @Meta.AD(description = "A target Filter to look for a specific Endpoint(s) to connect with.",
             name = "Message Filter",
             required = false)
    String messaging_filter();

    @Meta.AD(description = "The class names which are valid messages that this class will send across the connection.",
             name = "Message Type",
             required = false)
    String[] messaging_type();
}
