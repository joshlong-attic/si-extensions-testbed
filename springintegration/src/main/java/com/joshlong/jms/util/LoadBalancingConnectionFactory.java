package com.joshlong.jms.util;

import org.apache.commons.lang.math.RandomUtils;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.io.Serializable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.jms.*;


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

    // these are temporary, do not use 
    private Set<ConnectionFactory> sendingConnectionFactories = new HashSet<ConnectionFactory>();
    private Set<ConnectionFactory> receivingConnectionFactories = new HashSet<ConnectionFactory>();
    private SelectionStrategy<AdaptedDelegatedConnectionFactory> selectionStrategy = new RoundRobinSelectionStrategy<AdaptedDelegatedConnectionFactory>();

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

            //adaptedDelegatedConnectionFactory.afterPropertiesSet();
        }

        for (ConnectionFactory connectionFactory : this.sendingConnectionFactories) {
            AdaptedDelegatedConnectionFactory adaptedDelegatedConnectionFactory = adaptedCfMap.putIfAbsent(connectionFactory, new AdaptedDelegatedConnectionFactory(connectionFactory));
            adaptedDelegatedConnectionFactory.setUseForSend(true);

            // adaptedDelegatedConnectionFactory.afterPropertiesSet();
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
        return this.createConnection(null, null);
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
