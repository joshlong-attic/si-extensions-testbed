package com.joshlong.jms.util;

import java.io.Serializable;

import javax.jms.*;


/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */

/**
 * This is where the metal begins to hit the road: we delegate to other {@link javax.jms.Session} implementations in a way defined by a strategy. For the moment we'll just random todo some sort of strategy pattern that could let us encapsulate a selection strategy thats generic enough to be reused
 * for many kinds of objects? http://download.oracle.com/docs/cd/E17410_01/javaee/6/api/javax/jms/Connection.html
 *
 * @author jlong
 */
public class LoadBalancingSession implements Session {
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
