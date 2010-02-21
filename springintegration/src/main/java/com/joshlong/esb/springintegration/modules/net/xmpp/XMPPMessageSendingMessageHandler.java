package com.joshlong.esb.springintegration.modules.net.xmpp;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.Lifecycle;
import org.springframework.integration.message.MessageDeliveryException;
import org.springframework.integration.message.MessageHandler;
import org.springframework.integration.message.MessageHandlingException;
import org.springframework.integration.message.MessageRejectedException;
import org.springframework.util.Assert;


/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
public class XMPPMessageSendingMessageHandler implements MessageHandler, Lifecycle, InitializingBean {
    private static final Logger logger = Logger.getLogger(XMPPMessageSendingMessageHandler.class);
    private volatile boolean running;
    private volatile XMPPConnection xmppConnection;
    private String user;
    private String password;
    private String host;
    private String serviceName;
    private String resource;
    private String saslMechanismSupported;
    private int saslMechanismSupportedIndex;
    private int port;

    public int getPort() {
        return port;
    }

    public void setPort(final int port) {
        this.port = port;
    }

    public int getSaslMechanismSupportedIndex() {
        return saslMechanismSupportedIndex;
    }

    public void setSaslMechanismSupportedIndex(final int saslMechanismSupportedIndex) {
        this.saslMechanismSupportedIndex = saslMechanismSupportedIndex;
    }

    public String getSaslMechanismSupported() {
        return saslMechanismSupported;
    }

    public void setSaslMechanismSupported(final String saslMechanismSupported) {
        this.saslMechanismSupported = saslMechanismSupported;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(final String resource) {
        this.resource = resource;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(final String serviceName) {
        this.serviceName = serviceName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

    public XMPPConnection getXmppConnection() {
        return xmppConnection;
    }

    public void setXmppConnection(final XMPPConnection xmppConnection) {
        this.xmppConnection = xmppConnection;
    }

    public void handleMessage(final org.springframework.integration.core.Message<?> message)
        throws MessageRejectedException, MessageHandlingException, MessageDeliveryException {
        try {
            // pre-reqs: user to send, string to send as msg boyd
            String msgBody = null;

            // pre-reqs: user to send, string to send as msg boyd
            String destinationUser = null;

            Object payload = message.getPayload();

            if (payload instanceof String) {
                msgBody = (String) payload;
            }

            destinationUser = (String) message.getHeaders().get(XMPPConstants.TO_USER);

            Assert.state(!StringUtils.isEmpty(destinationUser), "the destination user can't be null");
            Assert.state(!StringUtils.isEmpty(msgBody), "the message body can't be null");

            Chat chat = xmppConnection.getChatManager().createChat(destinationUser,
                  null );


            chat.sendMessage(msgBody);

        } catch (Throwable th) {
            logger.debug("exception thrown when trying to send a message", th);
        }
    }

    public void start() {
        this.running = true;
    }

    public void stop() {
        this.running = false;

        if (xmppConnection.isConnected()) {
            logger.debug("shutting down.");
            xmppConnection.disconnect();
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    public void afterPropertiesSet() throws Exception {
        if (this.xmppConnection == null) {
            XMPPConnectionFactory xmppConnectionFactory = new XMPPConnectionFactory(this.getUser(), this.getPassword(), this.getHost(), this.getServiceName(), this.getResource(),
                    this.getSaslMechanismSupported(), this.getSaslMechanismSupportedIndex(), this.getPort());
            xmppConnectionFactory.afterPropertiesSet();
            this.xmppConnection = xmppConnectionFactory.createInstance();
        }
    }
}
