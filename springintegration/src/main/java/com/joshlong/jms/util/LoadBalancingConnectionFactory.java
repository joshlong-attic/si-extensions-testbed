package com.joshlong.jms.util;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import javax.jms.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
public class LoadBalancingConnectionFactory   implements QueueConnectionFactory,
                                                         TopicConnectionFactory, ExceptionListener, InitializingBean, DisposableBean {
    private static final Logger logger = Logger.getLogger(LoadBalancingConnectionFactory.class);
    private Set<ConnectionFactory> delegateConnectionFactories = new HashSet<ConnectionFactory>();

    @Override
    public void destroy() throws Exception {
        logger.debug("destroy()");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.debug("afterPropertiesSet()");
    }

    @Override
    public void onException(final JMSException e) {

    }

    @Override
    public QueueConnection createQueueConnection() throws JMSException {
        return null;
    }

    @Override
    public QueueConnection createQueueConnection(String s, String s1)
        throws JMSException {
        return null;
    }

    @Override
    public TopicConnection createTopicConnection() throws JMSException {
        return null;
    }

    @Override
    public TopicConnection createTopicConnection(String s, String s1)
        throws JMSException {
        return null;
    }

    @Override
    public Connection createConnection() throws JMSException {
        return null;
    }

    @Override
    public Connection createConnection(String usr, String pw)
        throws JMSException {
        LoadBalancingConnection loadBalancingConnection = new LoadBalancingConnection(this.delegateConnectionFactories);

        try {
            loadBalancingConnection.setPassword(pw);
            loadBalancingConnection.setUser(usr);
            loadBalancingConnection.afterPropertiesSet();
        } catch (Exception e) {
            logger.debug("exception thrown when trying to initialize LoadBalancingConnection instance",
                e);
            loadBalancingConnection = null;
        }

        return loadBalancingConnection;
    }
}


/**
 *
 * The muiltiplexed {@link javax.jms.Connection}
 *
 * @author jlong
 *
 */
class LoadBalancingConnection implements Connection, InitializingBean {
    private String user;
    private String password;
    private ConcurrentSkipListSet<ConnectionFactory> connectionFactories = new ConcurrentSkipListSet<ConnectionFactory>();
    private ConcurrentSkipListSet<Connection> connections = new ConcurrentSkipListSet<Connection>();
    private ExceptionListener exceptionListener;
    private boolean shouldAutoVaryClientID = true;

    public LoadBalancingConnection(Collection<ConnectionFactory> cfSet) {
        Assert.notEmpty(cfSet, "the collection of ConnectionFactory instances can not be empty");

        for (ConnectionFactory connectionFactory : cfSet)
            this.connectionFactories.add(connectionFactory);
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void start() throws JMSException {
        for (Connection connection : this.connections)
            connection.start();
    }

    @Override
    public void stop() throws JMSException {
        for (Connection connection : this.connections)
            connection.stop();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        boolean shouldCreateWithCredentials = (!StringUtils.isEmpty(this.user) &&
            !StringUtils.isEmpty(this.password));

        if (shouldCreateWithCredentials) {
            for (ConnectionFactory connectionFactory : this.connectionFactories)
                this.connections.add(connectionFactory.createConnection(this.user, this.password));
        } else {
            for (ConnectionFactory connectionFactory : this.connectionFactories)
                this.connections.add(connectionFactory.createConnection());
        }
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public Collection<ConnectionFactory> getConnectionFactories() {
        return connectionFactories;
    }

    public Collection<Connection> getConnections() {
        return connections;
    }

    @Override
    public Session createSession(boolean transacted, int ackMode)
        throws JMSException {
        return new LoadBalancingSession(this, transacted, ackMode);
    }

    @Override
    public String getClientID() throws JMSException {
        throw new UnsupportedOperationException("invalid semantics. Can't call 'getClientID' on '" +
            LoadBalancingConnection.class + "'");
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

        for (Connection connection : this.connections)
            connection.setClientID(s + (this.shouldAutoVaryClientID ? StringUtils.EMPTY : ctr++));
    }

    @Override
    public ConnectionMetaData getMetaData() throws JMSException {
        throw new UnsupportedOperationException("invalid semantics. Can't call 'getMetaData' on '" +
            LoadBalancingConnection.class + "'");
    }

    Connection nextConnection() {
        return this.connections.iterator().next();
    }

    @Override
    public ExceptionListener getExceptionListener() throws JMSException {
        ExceptionListener exceptionListener = this.connections.iterator().next()
                                                              .getExceptionListener();

        if (exceptionListener == this.exceptionListener) {
            return this.exceptionListener;
        }

        throw new UnsupportedOperationException(
            "invalid semantics. Can't call 'getExceptionListener' on '" +
            LoadBalancingConnection.class + "'");
    }

    @Override
    public void setExceptionListener(ExceptionListener exceptionListener)
        throws JMSException {
        this.exceptionListener = exceptionListener ;
        for (Connection connection : this.connections)
            connection.setExceptionListener(this.exceptionListener);
    }

    @Override
    public void close() throws JMSException {
        for (Connection connection : this.connections)
            connection.close();
    }

    @Override
    public ConnectionConsumer createConnectionConsumer(Destination destination, String s,
        ServerSessionPool serverSessionPool, int i) throws JMSException {
        return null;
    }

    @Override
    public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String s, String s1,
        ServerSessionPool serverSessionPool, int i) throws JMSException {
        return null;
    }
}


/**
 * This is where the metal begins to hit the road: we delegate to other {@link javax.jms.Session} implementations in a way defined by a strategy. For the moment we'll just random
 * todo some sort of strategy pattern that could let us encapsulate a selection strategy thats generic enough to be reused for many kinds of objects?
 * http://download.oracle.com/docs/cd/E17410_01/javaee/6/api/javax/jms/Connection.html
 *
 * @author jlong
 */
class LoadBalancingSession implements Session {
    private LoadBalancingConnection loadBalancingConnection;
    private boolean transacted;
    private int ackMode;

    public LoadBalancingSession(LoadBalancingConnection loadBalancingConnection,
        boolean transacted, int ackMode) {
        this.transacted = transacted;
        this.ackMode = ackMode;
        this.loadBalancingConnection = loadBalancingConnection;
    }

    @Override
    public void unsubscribe(String s) throws JMSException {
    }

    @Override
    public MapMessage createMapMessage() throws JMSException {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Message createMessage() throws JMSException {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ObjectMessage createObjectMessage() throws JMSException {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ObjectMessage createObjectMessage(Serializable serializable)
        throws JMSException {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public StreamMessage createStreamMessage() throws JMSException {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public TextMessage createTextMessage() throws JMSException {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public TextMessage createTextMessage(String s) throws JMSException {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean getTransacted() throws JMSException {
        return this.transacted;
    }

    @Override
    public int getAcknowledgeMode() throws JMSException {
        return this.ackMode;
    }

    @Override
    public void commit() throws JMSException {
    }

    @Override
    public void rollback() throws JMSException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void close() throws JMSException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void recover() throws JMSException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MessageListener getMessageListener() throws JMSException {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setMessageListener(MessageListener messageListener)
        throws JMSException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void run() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MessageProducer createProducer(Destination destination)
        throws JMSException {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MessageConsumer createConsumer(Destination destination)
        throws JMSException {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MessageConsumer createConsumer(Destination destination, String s)
        throws JMSException {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MessageConsumer createConsumer(Destination destination, String s, boolean b)
        throws JMSException {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Queue createQueue(String s) throws JMSException {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Topic createTopic(String s) throws JMSException {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public TopicSubscriber createDurableSubscriber(Topic topic, String s)
        throws JMSException {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public TopicSubscriber createDurableSubscriber(Topic topic, String s, String s1, boolean b)
        throws JMSException {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public QueueBrowser createBrowser(Queue queue) throws JMSException {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public QueueBrowser createBrowser(Queue queue, String s)
        throws JMSException {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public TemporaryQueue createTemporaryQueue() throws JMSException {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public TemporaryTopic createTemporaryTopic() throws JMSException {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BytesMessage createBytesMessage() throws JMSException {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }
}
