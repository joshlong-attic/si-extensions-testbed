package com.joshlong.jms.util;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import javax.jms.*;
import java.util.Collection;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */

/**
 * The muiltiplexed {@link javax.jms.Connection}
 *
 * @author jlong
 */
public class LoadBalancingConnection implements Connection, InitializingBean {
    private String user;
    private String password;
    private ConcurrentSkipListSet<AdaptedDelegatedConnectionFactory> connectionFactories = new ConcurrentSkipListSet<AdaptedDelegatedConnectionFactory>();
    private CopyOnWriteArrayList<Connection> connections = new CopyOnWriteArrayList<Connection>();
    private ExceptionListener exceptionListener;
    private boolean shouldAutoVaryClientID = true;
    private SelectionStrategy<Connection> selectionStrategy = new RoundRobinSelectionStrategy<Connection>();

    public LoadBalancingConnection(Collection<AdaptedDelegatedConnectionFactory> cfSet) {
        Assert.notEmpty(cfSet, "the collection of ConnectionFactory instances can not be empty");

        for (AdaptedDelegatedConnectionFactory connectionFactory : cfSet) {
            this.connectionFactories.add(connectionFactory);
        }
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void start() throws JMSException {
        for (Connection connection : this.connections) {
            connection.start();
        }
    }

    @Override
    public void stop() throws JMSException {
        for (Connection connection : this.connections) {
            connection.stop();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        boolean shouldCreateWithCredentials = (!StringUtils.isEmpty(this.user) && !StringUtils.isEmpty(this.password));

        if (shouldCreateWithCredentials) {
            for (ConnectionFactory connectionFactory : this.connectionFactories) {
                this.connections.add(connectionFactory.createConnection(this.user, this.password));
            }
        } else {
            for (ConnectionFactory connectionFactory : this.connectionFactories) {
                this.connections.add(connectionFactory.createConnection());
            }
        }
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public Collection<AdaptedDelegatedConnectionFactory> getConnectionFactories() {
        return connectionFactories;
    }

    public Collection<Connection> getConnections() {
        return connections;
    }

    @Override
    public Session createSession(boolean transacted, int ackMode)
        throws JMSException {
        Connection c = this.nextConnection();
        return new LoadBalancingSession(this,  c, transacted, ackMode);
    }

    @Override
    public String getClientID() throws JMSException {
        throw new UnsupportedOperationException("invalid semantics. Can't call 'getClientID' on '" + LoadBalancingConnection.class + "'");
    }

    public boolean isShouldAutoVaryClientID() {
        return shouldAutoVaryClientID;
    }

    public void setShouldAutoVaryClientID(boolean shouldAutoVaryClientID) {
        this.shouldAutoVaryClientID = shouldAutoVaryClientID;
    }

    @Override
    public void setClientID(String s) throws JMSException {
        int ctr = 0;
        for (Connection connection : this.connections) {
            connection.setClientID(s + (this.shouldAutoVaryClientID ? StringUtils.EMPTY : ctr++));
        }
    }

    @Override
    public ConnectionMetaData getMetaData() throws JMSException {
        throw new UnsupportedOperationException("invalid semantics. Can't call 'getMetaData' on '" + LoadBalancingConnection.class + "'");
    }

    private Connection nextConnection() {
        return this.selectionStrategy.which(this.connections);
    }

  /*  private Connection nextSendConnection() {
        return this.selectionStrategy.which(this.connections);
    }*/

    @Override
    public ExceptionListener getExceptionListener() throws JMSException {
        ExceptionListener exceptionListener = this.connections.iterator().next().getExceptionListener();

        if (exceptionListener == this.exceptionListener) {
            return this.exceptionListener;
        }

        throw new UnsupportedOperationException("invalid semantics. Can't call 'getExceptionListener' on '" + LoadBalancingConnection.class + "'");
    }

    @Override
    public void setExceptionListener(ExceptionListener exceptionListener)
        throws JMSException {
        this.exceptionListener = exceptionListener;

        for (Connection connection : this.connections) {
            connection.setExceptionListener(this.exceptionListener);
        }
    }

    @Override
    public void close() throws JMSException {
        for (Connection connection : this.connections) {
            connection.close();
        }
    }

    @Override
    public ConnectionConsumer createConnectionConsumer(Destination destination, String s, ServerSessionPool serverSessionPool, int i)
        throws JMSException {
        return nextConnection().createConnectionConsumer(destination, s, serverSessionPool, i);
    }

    @Override
    public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String s, String s1, ServerSessionPool serverSessionPool, int i)
        throws JMSException {
        return nextConnection().createConnectionConsumer(topic, s, serverSessionPool, i);
    }
}
