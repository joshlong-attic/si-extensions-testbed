package com.joshlong.jms.util;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;


/**
 * Simple wrapper {@link javax.jms.ConnectionFactory} used to record whether the instance should be used in load balancing publishing and / or consuming messages.
 *
 * @author Josh Long
 */
public class AdaptedDelegatedConnectionFactory implements ConnectionFactory {
    private boolean useForSend = false;
    private ConnectionFactory connectionFactory;
    private boolean useForReceive = false;

    public AdaptedDelegatedConnectionFactory(ConnectionFactory targetConnectionFactory, boolean s, boolean c) {
        this.connectionFactory = targetConnectionFactory;
        this.useForReceive = c;
        this.useForSend = s;
    }

    public AdaptedDelegatedConnectionFactory(ConnectionFactory targetConnectionFactory) {
        this.connectionFactory = targetConnectionFactory;
    }

    @Override
    public Connection createConnection() throws JMSException {
        return this.connectionFactory.createConnection();
    }

    @Override
    public Connection createConnection(final String s, final String s1)
        throws JMSException {
        return this.connectionFactory.createConnection(s, s1);
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
