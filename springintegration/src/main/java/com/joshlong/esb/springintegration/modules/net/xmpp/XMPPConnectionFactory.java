package com.joshlong.esb.springintegration.modules.net.xmpp;

import org.apache.commons.lang.StringUtils;

import org.apache.log4j.Logger;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;

import org.springframework.beans.factory.config.AbstractFactoryBean;


/**
 * This class configures an {@link org.jivesoftware.smack.XMPPConnection} object. This object is used for all scenarios
 * to talk to a Smack server.
 *
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 * @see {@link org.jivesoftware.smack.XMPPConnection}
 */
public class XMPPConnectionFactory extends AbstractFactoryBean<XMPPConnection> {
    private static final Logger logger = Logger.getLogger(XMPPConnectionFactory.class);
    private String user;
    private String password;
    private String host;
    private String serviceName;
    private String resource;
    private String saslMechanismSupported;
    private int saslMechanismSupportedIndex;
    private int port;
    private boolean debug = false;

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(final boolean debug) {
        XMPPConnection.DEBUG_ENABLED=debug;

        this.debug = debug;
    }

    public XMPPConnectionFactory() {
    }

    public XMPPConnectionFactory(final String user, final String password, final String host, final String serviceName, final String resource, final String saslMechanismSupported,
        final int saslMechanismSupportedIndex, final int port) {
        this.user = user;
        this.password = password;
        this.host = host;
        this.serviceName = serviceName;
        this.resource = resource;
        this.saslMechanismSupported = saslMechanismSupported;
        this.saslMechanismSupportedIndex = saslMechanismSupportedIndex;
        this.port = port;
    }

    private XMPPConnection configureAndConnect(String usr, String pw, String host, int port, String serviceName, String resource, String saslMechanismSupported, int saslMechanismSupportedIndex) {
        logger.debug(String.format("usr=%s, pw=%s, host=%s, port=%s, serviceName=%s, resource=%s, saslMechanismSupported=%s, saslMechanismSupportedIndex=%s", usr, pw, host, port, serviceName,
                resource, saslMechanismSupported, saslMechanismSupportedIndex));


        XMPPConnection.DEBUG_ENABLED =false; // default
         
    //    if( logger.isDebugEnabled())        XMPPConnection.DEBUG_ENABLED =true ;

        ConnectionConfiguration cc = new ConnectionConfiguration(host, port, serviceName);
        XMPPConnection connection = new XMPPConnection(cc);

        try {
            connection.connect();

            // You have to put this code before you login
            if (!StringUtils.isEmpty(saslMechanismSupported)) {
                SASLAuthentication.supportSASLMechanism(saslMechanismSupported, saslMechanismSupportedIndex);
            }

            // You have to specify your gmail addres WITH @gmail.com at the end
            if (!StringUtils.isEmpty(resource)) {
                connection.login(usr, pw, resource);
            } else {
                connection.login(usr, pw);
            }

            // See if you are authenticated
            logger.debug("authenticated? " + connection.isAuthenticated());
        } catch (Throwable e1) {
            logger.debug("exception occurred trying to connnect", e1);
        }

        return connection;
    }

    @Override
    public Class<?extends XMPPConnection> getObjectType() {
        return XMPPConnection.class;
    }

    @Override
    protected XMPPConnection createInstance() throws Exception {
        XMPPConnection xmppConnection = this.configureAndConnect(this.getUser(), this.getPassword(), this.getHost(), this.getPort(), this.getServiceName(), this.getResource(),
                this.getSaslMechanismSupported(), this.getSaslMechanismSupportedIndex());

        return xmppConnection;
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

    public String getUser() {
        return user;
    }

    public void setUser(final String user) {
        this.user = user;
    }
}
