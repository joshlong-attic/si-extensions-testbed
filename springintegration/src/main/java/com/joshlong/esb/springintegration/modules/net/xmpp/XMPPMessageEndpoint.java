package com.joshlong.esb.springintegration.modules.net.xmpp;

import org.apache.commons.lang.builder.ToStringBuilder;

import org.apache.log4j.Logger;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;

import org.springframework.context.Lifecycle;

import org.springframework.integration.channel.MessageChannelTemplate;
import org.springframework.integration.core.MessageChannel;
import org.springframework.integration.endpoint.AbstractEndpoint;
import org.springframework.integration.message.MessageBuilder;


/**
 *
 *
 * TODO rework this so that instead of injecting a bean we just configure it in place in this bean to see if that works 
 *
 *
 * this message source logs in as a user and forwards any messages <em>to</em> that user on to downstream components.
 *
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 * @see {@link org.jivesoftware.smack.XMPPConnection} the xmpconnection classs
 */
public class XMPPMessageEndpoint extends AbstractEndpoint implements Lifecycle {
    static private Logger logger = Logger.getLogger(XMPPMessageEndpoint.class);
    private final MessageChannelTemplate channelTemplate;
    private volatile MessageChannel requestChannel;
    private volatile XMPPConnection xmppConnection;

    public XMPPMessageEndpoint() {
        channelTemplate = new MessageChannelTemplate();
    }

    public MessageChannel getRequestChannel() {
        return requestChannel;
    }

    public void setRequestChannel(final MessageChannel requestChannel) {
        this.channelTemplate.setDefaultChannel(requestChannel);
        this.requestChannel = requestChannel;
    }

    private void forwardInboundXMPPMessageToSI(Chat chat, Message msg) {
        org.springframework.integration.core.Message<Message> xmppSIMsg = MessageBuilder.withPayload(msg).setHeader(XMPPConstants.CHAT, chat).build();
        channelTemplate.send(xmppSIMsg, requestChannel);
    }

    @Override
    protected void doStart() {
        logger.debug("start ");
        logger.debug("start: " + xmppConnection.isConnected() + ":" + xmppConnection.isAuthenticated());
    }

    public XMPPConnection getXmppConnection() {
        return xmppConnection;
    }

    public void setXmppConnection(final XMPPConnection xmppConnection) {
        logger.debug("xmppConnection=" + ToStringBuilder.reflectionToString(xmppConnection));
        this.xmppConnection = xmppConnection;
        logger.debug("setXMPPConnection: " + xmppConnection.isConnected() + ":" + xmppConnection.isAuthenticated());
        logger.debug("XMPPMessageEndpoint: thread ID :" + Thread.currentThread().getId());
        ChatManager chatManager = xmppConnection.getChatManager();
        chatManager.addChatListener(new ChatManagerListener() {
                public void chatCreated(final Chat chat, final boolean createdLocally) {
                    chat.addMessageListener(new MessageListener() {
                            public void processMessage(final Chat chat, final Message message) {
                                logger.debug(String.format("%s says %s. Message toString() = %s", chat.getParticipant(), message.getBody(), ToStringBuilder.reflectionToString(message)));
                                forwardInboundXMPPMessageToSI(chat, message);
                            }
                        });
                }
            });
    }

    @Override
    protected void doStop() {
        //  if (xmppConnection.isConnected()) {
        //   xmppConnection.disconnect();
        // }
    }

    @Override
    protected void onInit() throws Exception {
        assert xmppConnection != null : "the xmppCnnection shouldn't be null";
        channelTemplate.afterPropertiesSet();

        logger.debug("Init:" + xmppConnection.isConnected() + ":" + xmppConnection.isAuthenticated());
    }
}
