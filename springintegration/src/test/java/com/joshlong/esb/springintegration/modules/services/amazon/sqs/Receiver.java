package com.joshlong.esb.springintegration.modules.services.amazon.sqs;

import com.xerox.amazonws.sqs2.Message;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

@Component
public class Receiver {
    @ServiceActivator
    public void handleNewMessage(org.springframework.integration.core.Message<?> msg) {
        Message payload = (Message) msg.getPayload();
        System.out.println("the payload is " + payload);
    }
}
