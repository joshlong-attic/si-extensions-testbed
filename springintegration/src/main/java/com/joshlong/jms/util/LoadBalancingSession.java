package com.joshlong.jms.util;

import javax.jms.*;
import java.io.Serializable;


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
//    private Connection receiveConn;
    private Connection conn;
//    private Session receiveSession;
    private Session sendSession;

    public LoadBalancingSession(LoadBalancingConnection loadBalancingConnection, /*Connection receiver,*/ Connection sender, boolean transacted, int ackMode)
        throws JMSException {
        this.transacted = transacted;
        this.ackMode = ackMode;
        this.loadBalancingConnection = loadBalancingConnection;
        this.conn = sender;
//        this.receiveConn = receiver;

//        this.receiveSession = this.receiveConn.createSession(transacted, ackMode);
        this.sendSession = this.conn.createSession(transacted, ackMode);
    }

    @Override
    public void unsubscribe(String s) throws JMSException {
        this.getSession().unsubscribe(s);
    }

    @Override
    public MapMessage createMapMessage() throws JMSException {
        return getSession().createMapMessage();
    }

    @Override
    public Message createMessage() throws JMSException {
        return getSession().createMessage();
    }

    @Override
    public ObjectMessage createObjectMessage() throws JMSException {
        return getSession().createObjectMessage();
    }

    @Override
    public ObjectMessage createObjectMessage(Serializable serializable)
        throws JMSException {
        return getSession().createObjectMessage(serializable);
    }

    @Override
    public StreamMessage createStreamMessage() throws JMSException {
        return getSession().createStreamMessage();
    }

    @Override
    public TextMessage createTextMessage() throws JMSException {
        return getSession().createTextMessage();
    }

    @Override
    public TextMessage createTextMessage(String s) throws JMSException {
        return this.getSession().createTextMessage(s);
    }

    @Override
    public boolean getTransacted() throws JMSException {
        return this.getSession().getTransacted();
    }

    @Override
    public int getAcknowledgeMode() throws JMSException {
        return this.getSession().getAcknowledgeMode();
    }

    @Override
    public void commit() throws JMSException {
        this.getSession().commit();
    }

    @Override
    public void rollback() throws JMSException {
        this.getSession().rollback();
    }

    @Override
    public void close() throws JMSException {
        this.getSession().close();
    }

    @Override
    public void recover() throws JMSException {
        this.getSession().recover();
    }

    @Override
    public MessageListener getMessageListener() throws JMSException {
        return this.getSession().getMessageListener();
    }

    @Override
    public void setMessageListener(MessageListener messageListener)
        throws JMSException {
        this.getSession().setMessageListener(messageListener);
    }

    @Override
    public void run() {
        this.getSession().run();
    }

    @Override
    public MessageProducer createProducer(Destination destination)
        throws JMSException {
        return this.getSession().createProducer(destination);
    }

    @Override
    public MessageConsumer createConsumer(Destination destination)
        throws JMSException {
        return this.getSession().createConsumer(destination);
    }

    @Override
    public MessageConsumer createConsumer(Destination destination, String s)
        throws JMSException {
        return getSession().createConsumer(destination, s);
    }

    @Override
    public MessageConsumer createConsumer(Destination destination, String s, boolean b)
        throws JMSException {
        return getSession().createConsumer(destination, s, b);
    }

    @Override
    public Queue createQueue(String s) throws JMSException {
        return getSession().createQueue(s);
    }

    @Override
    public Topic createTopic(String s) throws JMSException {
        return this.getSession().createTopic(s);
    }

    @Override
    public TopicSubscriber createDurableSubscriber(Topic topic, String s)
        throws JMSException {
        return getSession().createDurableSubscriber(topic, s);
    }

    @Override
    public TopicSubscriber createDurableSubscriber(Topic topic, String s, String s1, boolean b)
        throws JMSException {
        return getSession().createDurableSubscriber(topic, s, s1, b);
    }

 /*   private Session getReceiveSession() {
        return this.receiveSession;
    }*/

    private Session getSession() {
        return this.sendSession;
    }

    // todo what are the right semantics of this?  
    @Override
    public QueueBrowser createBrowser(Queue queue) throws JMSException {
        return getSession().createBrowser(queue);
    }

    // todo what are the right semantics of this?
    @Override
    public QueueBrowser createBrowser(Queue queue, String s)
        throws JMSException {
        return this.getSession().createBrowser(queue, s);
    }

    @Override
    public TemporaryQueue createTemporaryQueue() throws JMSException {
        return this.getSession().createTemporaryQueue();
    }

    @Override
    public TemporaryTopic createTemporaryTopic() throws JMSException {
        return this.getSession().createTemporaryTopic();
    }

    @Override
    public BytesMessage createBytesMessage() throws JMSException {
        return this.getSession().createBytesMessage();
    }
}
