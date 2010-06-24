package com.joshlong.jms.util;

import org.apache.commons.lang.builder.CompareToBuilder;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;


/**
 * Simple wrapper {@link javax.jms.ConnectionFactory} used to record whether the instance should be used in load balancing publishing and / or consuming messages.
 *
 * @author Josh Long
 */
public class AdaptedDelegatedConnectionFactory implements ConnectionFactory, Comparable {
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
    public int compareTo(final Object o) {
        ConnectionFactory thatCf = (ConnectionFactory) o,
                            thisCf =this;
        if (o instanceof AdaptedDelegatedConnectionFactory) {
            thatCf = ((AdaptedDelegatedConnectionFactory) o).connectionFactory;
            thisCf = this.connectionFactory;

            if(thatCf instanceof Comparable && thisCf instanceof Comparable){

                Comparable a = ((Comparable) thisCf) ;
                Comparable b = ((Comparable) thatCf) ;
                return a.compareTo(b) ;
            }

        }
        int compareTo = CompareToBuilder.reflectionCompare(  thatCf, thisCf );
        return compareTo ;

    }

    @Override
    public int hashCode() {
        return this.connectionFactory.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof AdaptedDelegatedConnectionFactory) {
            if (o == this) {
                return true;
            }

            if (((AdaptedDelegatedConnectionFactory) o).connectionFactory.equals(this.connectionFactory)) {
                return true;
            }
        }

        return false;
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
