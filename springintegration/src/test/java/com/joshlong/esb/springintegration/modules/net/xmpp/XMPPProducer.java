package com.joshlong.esb.springintegration.modules.net.xmpp;

import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;
import org.springframework.integration.message.MessageSource;

import org.springframework.stereotype.Component;


/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Component
public class XMPPProducer implements MessageSource<String> {
    public Message<String> receive() {
        String msg = "the current time is " + System.currentTimeMillis() + "";

        return MessageBuilder.withPayload(msg).setHeader(XMPPConstants.TO_USER, System.getProperty("xmpp.test.to.user")).build();
    }
}
