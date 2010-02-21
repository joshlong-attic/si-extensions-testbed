package com.joshlong.esb.springintegration.modules.net.xmpp;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.springframework.context.Lifecycle;
import org.springframework.integration.channel.MessageChannelTemplate;
import org.springframework.integration.core.MessageChannel;
import org.springframework.integration.endpoint.AbstractEndpoint;
import org.springframework.integration.message.MessageBuilder;


/**
 * this message source logs in as a user and forwards any messages <em>to</em> that user on to downstream components.
 *
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 * @see {@link org.jivesoftware.smack.XMPPConnection} the XMPPConnection classs
 */
public class XMPPMessageDrivenEndpoint extends AbstractEndpoint implements Lifecycle {
    static private Logger logger = Logger.getLogger(XMPPMessageDrivenEndpoint.class);
    private final MessageChannelTemplate channelTemplate;
    private volatile MessageChannel requestChannel;
    private volatile XMPPConnection xmppConnection;
    private String user;
    private String password;
    private String host;
    private String serviceName;
    private String resource;
    private String saslMechanismSupported;
    private int saslMechanismSupportedIndex;
    private int port;

    public XMPPMessageDrivenEndpoint() {
        channelTemplate = new MessageChannelTemplate();
    }

    public XMPPConnection getXmppConnection() {
        return xmppConnection;
    }

    public void setXmppConnection(final XMPPConnection xmppConnection) {
        this.xmppConnection = xmppConnection;
    }

    public MessageChannel getRequestChannel() {
        return requestChannel;
    }

    public void setRequestChannel(final MessageChannel requestChannel) {
        this.channelTemplate.setDefaultChannel(requestChannel);
        this.requestChannel = requestChannel;
    }

    private void forwardInboundXMPPMessageToSI(Chat chat, Message msg) {
        for (Message.Body body : msg.getBodies()) {
            logger.debug(body.getMessage());
        }

       

        // todo is this a valid thing to do?
//        if ((msg.getBodies() == null) || (msg.getBodies().size() == 0) || StringUtils.isEmpty(msg.getBody())) {
//            return;
//        }

        org.springframework.integration.core.Message<Message> xmppSIMsg = MessageBuilder.withPayload(msg)
                .setHeader( XMPPConstants.TYPE,msg.getType()).
                 setHeader(XMPPConstants.CHAT, chat).build();
        channelTemplate.send(xmppSIMsg, requestChannel);

    }

    @Override
    protected void doStart() {
        logger.debug("start: " + xmppConnection.isConnected() + ":" + xmppConnection.isAuthenticated());
    }

    @Override
    protected void doStop() {
        if (xmppConnection.isConnected()) {
            logger.debug("shutting down.");
            xmppConnection.disconnect();
        }
    }

    @Override
    protected void onInit() throws Exception {
        channelTemplate.afterPropertiesSet();

        if (this.xmppConnection == null) {
            XMPPConnectionFactory xmppConnectionFactory = new XMPPConnectionFactory(this.getUser(), this.getPassword(), this.getHost(), this.getServiceName(), this.getResource(),
                    this.getSaslMechanismSupported(), this.getSaslMechanismSupportedIndex(), this.getPort());
            xmppConnectionFactory.afterPropertiesSet();
            this.xmppConnection = xmppConnectionFactory.createInstance();
        }

        logger.debug("setXMPPConnection: " + xmppConnection.isConnected() + ":" + xmppConnection.isAuthenticated());

        xmppConnection.addPacketListener(new PacketListener(){
            public void processPacket(final Packet packet) {
                org.jivesoftware.smack.packet.Message msg = (org.jivesoftware.smack.packet.Message) packet;
                forwardInboundXMPPMessageToSI( xmppConnection.getChatManager().getThreadChat(msg.getThread()), msg);                
            }
        } ,null); // we don't have any kind of predicate stuff that we want to do so no need to specify a filter
/*
        ChatManager chatManager = xmppConnection.getChatManager();
        chatManager.addChatListener(new ChatManagerListener() {
                public void chatCreated(final Chat chat, final boolean createdLocally) {
                    chat.addMessageListener(new MessageListener() {
                            public void processMessage(final Chat chat, final Message message) {
                                forwardInboundXMPPMessageToSI(chat, message);
                            }
                        });
                }
            });*/
    }

    public String getUser() {
        return user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(final String serviceName) {
        this.serviceName = serviceName;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(final String resource) {
        this.resource = resource;
    }

    public String getSaslMechanismSupported() {
        return saslMechanismSupported;
    }

    public void setSaslMechanismSupported(final String saslMechanismSupported) {
        this.saslMechanismSupported = saslMechanismSupported;
    }

    public int getSaslMechanismSupportedIndex() {
        return saslMechanismSupportedIndex;
    }

    public void setSaslMechanismSupportedIndex(final int saslMechanismSupportedIndex) {
        this.saslMechanismSupportedIndex = saslMechanismSupportedIndex;
    }

    public int getPort() {
        return port;
    }

    public void setPort(final int port) {
        this.port = port;
    }
}
