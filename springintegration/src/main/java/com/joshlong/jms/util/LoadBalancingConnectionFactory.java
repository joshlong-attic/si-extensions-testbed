package com.joshlong.jms.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.util.Assert;

import javax.jms.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * This will let you interface with a set of {@link javax.jms.ConnectionFactory} instances as though they were one. This has the effect of load balancing the work across many - preusmably simialar - message brokers. You can stipulate which {@link javax.jms.ConnectionFactory} instances should be used
 * when sending a message versus which should be used when consuming. The reasoning is that you may not wish to create a {@link javax.jms.MessageConsumer} from all the {@link javax.jms.ConnectionFactory} instances to which you might send them because, by defintion, consumption of all N instances
 * increases the load on the client in a single place, where syndication via send lessens it.
 *
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
public class LoadBalancingConnectionFactory implements QueueConnectionFactory, TopicConnectionFactory, ExceptionListener, InitializingBean, DisposableBean {
    private static final Logger logger = Logger.getLogger(LoadBalancingConnectionFactory.class);
    private CopyOnWriteArrayList<AdaptedDelegatedConnectionFactory> delegatedConnectionFactories = new CopyOnWriteArrayList<AdaptedDelegatedConnectionFactory>();
    private Set<ConnectionFactory> sendingConnectionFactories = new HashSet<ConnectionFactory>();
    private Set<ConnectionFactory> receivingConnectionFactories = new HashSet<ConnectionFactory>();

    private SelectionStrategy<AdaptedDelegatedConnectionFactory> selectionStrategy
            = new RoundRobinSelectionStrategy<AdaptedDelegatedConnectionFactory>();

    public void setReceivingConnectionFactories(final Set<ConnectionFactory> receivingConnectionFactories) {
        this.receivingConnectionFactories = receivingConnectionFactories;
    }

    public void setSendingConnectionFactories(final Set<ConnectionFactory> targetConnectionFactories) {
        this.sendingConnectionFactories = targetConnectionFactories;
    }

    @Override
    public void destroy() throws Exception {
        logger.debug("destroy()");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.debug("afterPropertiesSet()");

        ConcurrentHashMap<ConnectionFactory, AdaptedDelegatedConnectionFactory> adaptedCfMap = new ConcurrentHashMap<ConnectionFactory, AdaptedDelegatedConnectionFactory>();

        for (ConnectionFactory connectionFactory : this.receivingConnectionFactories) {
            AdaptedDelegatedConnectionFactory adaptedDelegatedConnectionFactory = adaptedCfMap.putIfAbsent(connectionFactory, new AdaptedDelegatedConnectionFactory(connectionFactory));
            adaptedDelegatedConnectionFactory.setUseForReceive(true);
        }

        for (ConnectionFactory connectionFactory : this.sendingConnectionFactories) {
            AdaptedDelegatedConnectionFactory adaptedDelegatedConnectionFactory = adaptedCfMap.putIfAbsent(connectionFactory, new AdaptedDelegatedConnectionFactory(connectionFactory));
            adaptedDelegatedConnectionFactory.setUseForSend(true);
        }


        this.delegatedConnectionFactories.addAll(adaptedCfMap.values());

        this.sendingConnectionFactories.clear();
        this.receivingConnectionFactories.clear();

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
        LoadBalancingConnection loadBalancingConnection = new LoadBalancingConnection(this.delegatedConnectionFactories);

        try {
            loadBalancingConnection.setPassword(pw);
            loadBalancingConnection.setUser(usr);
            loadBalancingConnection.afterPropertiesSet();
        } catch (Exception e) {
            logger.debug("exception thrown when trying to initialize LoadBalancingConnection instance", e);
            loadBalancingConnection = null;
        }

        return loadBalancingConnection;
    }
}


/**
 * The muiltiplexed {@link javax.jms.Connection}
 *
 * @author jlong
 */
class LoadBalancingConnection implements Connection, InitializingBean {
    private String user;
    private String password;
    private ConcurrentSkipListSet<AdaptedDelegatedConnectionFactory> connectionFactories = new ConcurrentSkipListSet<AdaptedDelegatedConnectionFactory>();
    private ConcurrentSkipListSet<Connection> connections = new ConcurrentSkipListSet<Connection>();
    private ExceptionListener exceptionListener;
    private boolean shouldAutoVaryClientID = true;

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
        return new LoadBalancingSession(this, transacted, ackMode);
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

    Connection nextConnection() {
        return this.connections.iterator().next();
    }

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
        return null;
    }

    @Override
    public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String s, String s1, ServerSessionPool serverSessionPool, int i)
        throws JMSException {
        return null;
    }
}


/**
 * This is where the metal begins to hit the road: we delegate to other {@link javax.jms.Session} implementations in a way defined by a strategy. For the moment we'll just random todo some sort of strategy pattern that could let us encapsulate a selection strategy thats generic enough to be reused
 * for many kinds of objects? http://download.oracle.com/docs/cd/E17410_01/javaee/6/api/javax/jms/Connection.html
 *
 * @author jlong
 */
class LoadBalancingSession implements Session {
    private LoadBalancingConnection loadBalancingConnection;
    private boolean transacted;
    private int ackMode;

    public LoadBalancingSession(LoadBalancingConnection loadBalancingConnection, boolean transacted, int ackMode) {
        this.transacted = transacted;
        this.ackMode = ackMode;
        this.loadBalancingConnection = loadBalancingConnection;
    }

    @Override
    public void unsubscribe(String s) throws JMSException {
    }

    @Override
    public MapMessage createMapMessage() throws JMSException {
        return null;
    }

    @Override
    public Message createMessage() throws JMSException {
        return null;
    }

    @Override
    public ObjectMessage createObjectMessage() throws JMSException {
        return null;
    }

    @Override
    public ObjectMessage createObjectMessage(Serializable serializable)
        throws JMSException {
        return null;
    }

    @Override
    public StreamMessage createStreamMessage() throws JMSException {
        return null;
    }

    @Override
    public TextMessage createTextMessage() throws JMSException {
        return null;
    }

    @Override
    public TextMessage createTextMessage(String s) throws JMSException {
        return null;
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
    }

    @Override
    public void close() throws JMSException {
    }

    @Override
    public void recover() throws JMSException {
    }

    @Override
    public MessageListener getMessageListener() throws JMSException {
        return null;
    }

    @Override
    public void setMessageListener(MessageListener messageListener)
        throws JMSException {
    }

    @Override
    public void run() {
    }

    @Override
    public MessageProducer createProducer(Destination destination)
        throws JMSException {
        return null;
    }

    @Override
    public MessageConsumer createConsumer(Destination destination)
        throws JMSException {
        return null;
    }

    @Override
    public MessageConsumer createConsumer(Destination destination, String s)
        throws JMSException {
        return null;
    }

    @Override
    public MessageConsumer createConsumer(Destination destination, String s, boolean b)
        throws JMSException {
        return null;
    }

    @Override
    public Queue createQueue(String s) throws JMSException {
        return null;
    }

    @Override
    public Topic createTopic(String s) throws JMSException {
        return null;
    }

    @Override
    public TopicSubscriber createDurableSubscriber(Topic topic, String s)
        throws JMSException {
        return null;
    }

    @Override
    public TopicSubscriber createDurableSubscriber(Topic topic, String s, String s1, boolean b)
        throws JMSException {
        return null;
    }

    @Override
    public QueueBrowser createBrowser(Queue queue) throws JMSException {
        return null;
    }

    @Override
    public QueueBrowser createBrowser(Queue queue, String s)
        throws JMSException {
        return null;
    }

    @Override
    public TemporaryQueue createTemporaryQueue() throws JMSException {
        return null;
    }

    @Override
    public TemporaryTopic createTemporaryTopic() throws JMSException {
        return null;
    }

    @Override
    public BytesMessage createBytesMessage() throws JMSException {
        return null;
    }
}


/**
 * Simple wrapper {@link javax.jms.ConnectionFactory} used to record whether the instance should be used in load balancing publishing and / or consuming messages.
 *
 * @author Josh Long
 */
class AdaptedDelegatedConnectionFactory extends SingleConnectionFactory {
    private boolean useForSend;
    private boolean useForReceive;

    public AdaptedDelegatedConnectionFactory(boolean s, boolean c) {
        super();
        this.useForReceive = c;
        this.useForSend = s;
    }

    public AdaptedDelegatedConnectionFactory() {
        super();
    }

    public AdaptedDelegatedConnectionFactory(final Connection target) {
        super(target);
    }

    public AdaptedDelegatedConnectionFactory(final ConnectionFactory targetConnectionFactory) {
        super(targetConnectionFactory);
    }

    public boolean isUseForReceive() {
        return useForReceive;
    }

    public void setUseForReceive(final boolean useForReceive) {
        this.useForReceive = useForReceive;
    }

    public boolean isUseForSend() {
        return useForSend;
    }

    public void setUseForSend(final boolean useForSend) {
        this.useForSend = useForSend;
    }
}


interface SelectionStrategy<T> {
    T which(List<T> choices);
}


class RandomSelectionStrategy<T> implements SelectionStrategy<T> {
    @Override
    public T which(final List<T> choices) {
        int randInt = RandomUtils.nextInt(choices.size() - 1);

        return choices.get(randInt);
    }
}


class RoundRobinSelectionStrategy<T> implements SelectionStrategy<T> {
    private AtomicInteger integer = new AtomicInteger();

    @Override
    public T which(final List<T> choices) {
        Assert.notEmpty(choices, "choices can't be empty");

        int len = choices.size(); // should be constant

        integer.compareAndSet(len, 0); // if by chance its overrun the safe level and is now equal to lenght + 1, then wrap it around to 0

        int indx = Math.max(0, Math.min(integer.getAndIncrement(), len - 1));

        //there should be no way to be < 0 and no way to be > len -1
        // theres a slight thread safety issue here, so again to be sure, the final value is ensured to be < the total length
        return choices.get(indx);
    }
}
