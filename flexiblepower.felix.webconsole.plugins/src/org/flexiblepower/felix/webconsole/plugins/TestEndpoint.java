package org.flexiblepower.felix.webconsole.plugins;

import org.flexiblepower.felix.webconsole.plugins.TestEndpoint.Config;
import org.flexiblepower.messaging.Connection;
import org.flexiblepower.messaging.Endpoint;
import org.flexiblepower.messaging.MessageHandler;
import org.flexiblepower.messaging.Port;
import org.flexiblepower.messaging.Ports;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.metatype.Meta;

@Component(immediate = false, designateFactory = Config.class)
@Ports({ @Port(name = "foo", sends = String.class, accepts = String.class),
        @Port(name = "bar", sends = String.class, accepts = String.class),
        @Port(name = "baz", sends = String.class, accepts = String.class) })
public class TestEndpoint implements Endpoint {
    public interface Config {
        @Meta.AD
        String message();
    }

    private static final Logger logger = LoggerFactory.getLogger(TestEndpoint.class);

    @Override
    public MessageHandler onConnect(Connection connection) {
        connection.sendMessage("Test");
        return new MessageHandler() {

            @Override
            public void handleMessage(Object message) {
                logger.info("got a message {}", message);
            }

            @Override
            public void disconnected() {
                logger.trace("Disconnected TestEndpoint");
            }
        };
    }
}
