package com.joshlong.jms.util;

import org.apache.activemq.spring.ActiveMQConnectionFactory;

import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import java.util.HashSet;
import java.util.Set;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;


/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
public class Main {
    public static void main(String[] args) throws Throwable {
        Set<ConnectionFactory> connectionFactorySet = new HashSet<ConnectionFactory>();
        String[] brokerUrls = { "tcp://127.0.0.1:61616", "tcp://127.0.0.1:61617" };
        int brokerInt = 0;

        for (String brokerUrl : brokerUrls) {
            ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
            activeMQConnectionFactory.setBeanName("activeMqConnectionFactory" + brokerInt++);
            activeMQConnectionFactory.setUseBeanNameAsClientIdPrefix(true);
            activeMQConnectionFactory.setBrokerURL(brokerUrl);
            activeMQConnectionFactory.afterPropertiesSet();

            CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(activeMQConnectionFactory);
            connectionFactorySet.add(cachingConnectionFactory);
        }

        LoadBalancingConnectionFactory connectionFactory = new LoadBalancingConnectionFactory();
        connectionFactory.setReceivingConnectionFactories(connectionFactorySet);
        connectionFactory.setSendingConnectionFactories(connectionFactorySet);
        connectionFactory.afterPropertiesSet();

        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.afterPropertiesSet();
        jmsTemplate.send("testLoadBalancer",
            new MessageCreator() {
                @Override
                public Message createMessage(final Session session)
                    throws JMSException {
                    return session.createTextMessage("test" + System.currentTimeMillis());
                }
            });
    }
}
