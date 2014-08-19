package org.flexiblepower.felix.webconsole.plugins;

import org.flexiblepower.messaging.Connection;
import org.flexiblepower.messaging.Endpoint;
import org.flexiblepower.messaging.MessageHandler;
import org.flexiblepower.messaging.Port;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Component;

@Component(immediate = true)
@Port(name = "test", sends = String.class, accepts = String.class)
public class TestEndpoint implements Endpoint {
	private static final Logger log = LoggerFactory
			.getLogger(TestEndpoint.class);

	@Override
    public MessageHandler onConnect(Connection connection) {
        connection.sendMessage("Test");
        return new MessageHandler() {

            @Override
            public void handleMessage(Object message) {
                log.info("got a message {}", message);
            }

            @Override
            public void disconnected() {
				log.trace("Disconnected TestEndpoint");
            }
        };
    }
}
