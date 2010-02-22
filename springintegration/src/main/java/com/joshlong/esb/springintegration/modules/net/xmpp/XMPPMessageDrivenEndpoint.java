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
 * This compnents logs in as a user and forwards any messages <em>to</em> that user on to downstream components. The
 * component is an endpoint that has its own lifecycle and does not need any {@link
 * org.springframework.integration.endpoint.AbstractPollingEndpoint.Poller} to work. It takes any message from a given
 * XMPP session (as established by the current {@link org.jivesoftware.smack.XMPPConnection}) and forwards the {@link
 * org.jivesoftware.smack.packet.Message} as the payload of the Spring Integration {@link
 * org.springframework.integration.core.Message}. The {@link org.jivesoftware.smack.Chat} instance that's used is passed
 * along as a header (under {@link com.joshlong.esb.springintegration.modules.net.xmpp.XMPPConstants#CHAT}.)
 * Additionally, the {@link org.jivesoftware.smack.packet.Message.Type} is passed along under the header {@link
 * com.joshlong.esb.springintegration.modules.net.xmpp.XMPPConstants#TYPE}. Both of these pieces of metadata can be
 * obtained directly from the payload, if required. They are here as a convenience. <strong>Note</strong>: the {@link
 * org.jivesoftware.smack.ChatManager} maintains a Map<String,Chat> for threads and users, where the threadID ({@link
 * String}) is the key or the userID {@link String} is the key, respectively. This {@link java.util.Map} is a
 * Smack-specific implementation called {@link org.jivesoftware.smack.util.collections.ReferenceMap} that removes
 * key/values as references are dereferenced. Take care to enable this garbage collection, taking what you need from the
 * payload and the headers and discarding as soon as possible.
 *
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 * @see {@link org.jivesoftware.smack.ChatManager} the ChatManager class that keeps watch over all Chats between the
 *      client and any other participants.
 * @see {@link org.springframework.integration.channel.MessageChannelTemplate} handles all interesing operations on any
 *      Spring Integration channels.
 * @see {@link org.jivesoftware.smack.XMPPConnection} the XMPPConnection (as created by {@link
 *      com.joshlong.esb.springintegration.modules.net.xmpp.XMPPConnectionFactory}
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
        org.springframework.integration.core.Message<Message> xmppSIMsg = MessageBuilder.withPayload(msg).setHeader(
                XMPPConstants.TYPE, msg.getType()).setHeader(XMPPConstants.CHAT, chat).build();
        channelTemplate.send(xmppSIMsg, requestChannel);
    }

    @Override
    protected void doStart() {
        logger.debug("start: " + xmppConnection.isConnected() + ":" + xmppConnection.isAuthenticated());
    }

    @Override
    protected void doStop() {
        if (xmppConnection.isConnected()) {
            logger.debug("shutting down" + XMPPMessageDrivenEndpoint.class.getName() + ".");
            xmppConnection.disconnect();
        }
    }

    @Override
    protected void onInit() throws Exception {
        channelTemplate.afterPropertiesSet();

        if (this.xmppConnection == null) {
            XMPPConnectionFactory xmppConnectionFactory = new XMPPConnectionFactory(this.getUser(), this.getPassword(),
                                                                                    this.getHost(),
                                                                                    this.getServiceName(),
                                                                                    this.getResource(),
                                                                                    this.getSaslMechanismSupported(),
                                                                                    this.getSaslMechanismSupportedIndex(),
                                                                                    this.getPort());
            xmppConnectionFactory.afterPropertiesSet();
            this.xmppConnection = xmppConnectionFactory.createInstance();
        }

        xmppConnection.addPacketListener(new PacketListener() {
            public void processPacket(final Packet packet) {
                org.jivesoftware.smack.packet.Message msg = (org.jivesoftware.smack.packet.Message) packet;
                forwardInboundXMPPMessageToSI(xmppConnection.getChatManager().getThreadChat(msg.getThread()), msg);
            }
        }, null); // we don't have any kind of predicate stuff that we want to do so no need to specify a filter
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
