package com.joshlong.esb.springintegration.modules.net.xmpp;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.Chat;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.core.Message;
import org.springframework.stereotype.Component;


/**
 *
 * Handle display of incoming XMPP messages to this user
 *
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Component
public class XMPPAnnouncer {
    private static final Logger logger = Logger.getLogger(XMPPAnnouncer.class);

    @ServiceActivator
    public void announceXMPPMessage(Message<org.jivesoftware.smack.packet.Message> msg)
        throws Throwable {

        logger.debug( StringUtils.repeat( "=" , 100));

        Chat chat = (Chat)msg.getHeaders().get(XMPPConstants.CHAT);
        logger.debug("chat=" +ToStringBuilder.reflectionToString(chat));
        logger.debug("announceXMPPMessage: " + ToStringBuilder.reflectionToString(msg));

    }
}
