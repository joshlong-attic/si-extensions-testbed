package com.joshlong.jms.util;

import javax.jms.*;


/**
 * Simple wrapper {@link javax.jms.ConnectionFactory} used to record whether the instance should be used in load balancing publishing and / or consuming messages.
 *
 * @author Josh Long
 */
public class AdaptedDelegatedConnectionFactory implements ConnectionFactory, TopicConnectionFactory, QueueConnectionFactory, Comparable {
    private boolean useForSend;
    private ConnectionFactory connectionFactory;
    private boolean useForReceive;

    public AdaptedDelegatedConnectionFactory(ConnectionFactory targetConnectionFactory, boolean s, boolean c) {
        this.connectionFactory = targetConnectionFactory;
        this.useForReceive = c;
        this.useForSend = s;
    }

    public AdaptedDelegatedConnectionFactory(ConnectionFactory targetConnectionFactory) {
        this.connectionFactory = targetConnectionFactory;
    }

    @Override
    public int compareTo(final Object o) {
        if (o instanceof AdaptedDelegatedConnectionFactory) {
            AdaptedDelegatedConnectionFactory thatCf = (AdaptedDelegatedConnectionFactory) o;
            AdaptedDelegatedConnectionFactory thisCf = (AdaptedDelegatedConnectionFactory) this;

            return thatCf.hashCode() - thisCf.hashCode();
        }

        return -1;
    }

    @Override
    public int hashCode() {
        return this.getConnectionFactory().hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof AdaptedDelegatedConnectionFactory) {
            AdaptedDelegatedConnectionFactory adaptedDelegatedConnectionFactory = (AdaptedDelegatedConnectionFactory) o;

            return (o == this) || (adaptedDelegatedConnectionFactory.compareTo(this) == 0);
        }

        return false;
    }

    @Override
    public Connection createConnection() throws JMSException {
        return this.getConnectionFactory().createConnection();
    }

    @Override
    public Connection createConnection(final String s, final String s1)
        throws JMSException {
        return this.getConnectionFactory().createConnection(s, s1);
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

    private ConnectionFactory getConnectionFactory() {
        return this.connectionFactory;
    }

    private QueueConnectionFactory getQueueConnectionFactory() {
        if (this.connectionFactory instanceof QueueConnectionFactory) {
            return (QueueConnectionFactory) this.connectionFactory;
        }

        return null;
    }

    private TopicConnectionFactory getTopicConnectionFactory() {
        if (this.connectionFactory instanceof TopicConnectionFactory) {
            return (TopicConnectionFactory) this.connectionFactory;
        }

        return null;
    }

    @Override
    public QueueConnection createQueueConnection() throws JMSException {
        return this.getQueueConnectionFactory().createQueueConnection();
    }

    @Override
    public QueueConnection createQueueConnection(final String userName, final String password)
        throws JMSException {
        return this.getQueueConnectionFactory().createQueueConnection(userName, password);
    }

    @Override
    public TopicConnection createTopicConnection() throws JMSException {
        return this.getTopicConnectionFactory().createTopicConnection();
    }

    @Override
    public TopicConnection createTopicConnection(final String userName, final String password)
        throws JMSException {
        return this.getTopicConnectionFactory().createTopicConnection(userName, password);
    }
}
