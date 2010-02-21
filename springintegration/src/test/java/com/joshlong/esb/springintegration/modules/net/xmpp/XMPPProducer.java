package com.joshlong.esb.springintegration.modules.net.xmpp;

import org.apache.commons.lang.StringUtils;

import org.apache.log4j.Logger;

import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;
import org.springframework.integration.message.MessageSource;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;


/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Component
public class XMPPProducer implements MessageSource<String> {
    private static final Logger logger = Logger.getLogger(XMPPProducer.class);
    private volatile int counter;
    private Collection<String> messages;

    public XMPPProducer() {
        messages = new ArrayList<String>();
    }

    public Message<String> receive() {
        try {
            if (counter > 10) {
                logger.debug("return null");
                return null;
            }
            counter += 1;
            Thread.sleep(1000 * 10);

            String msg = "the current time is " + System.currentTimeMillis() + "";

            String usr = System.getProperty("xmpp.test.to.user");
            logger.debug("the recipient is " + usr);
            messages.add(msg);

            String nMessage = StringUtils.join(messages.iterator(), ",");

            ///*.setHeader(XMPPConstants.THREAD_ID, "athreadid11222").*/
            return MessageBuilder.withPayload(nMessage).setHeader(XMPPConstants.TO_USER, usr).build();
        } catch (InterruptedException e) {
            logger.debug("exception thrown when trying to receive a message", e);
        }

        return null;
    }
}
