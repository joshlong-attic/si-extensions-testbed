package com.joshlong.esb.springintegration.modules.net.xmpp.test;

import com.joshlong.esb.springintegration.modules.net.xmpp.XMPPConnectionFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.XMPPConnection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */

public class TestXMPP /*extends AbstractJUnit4SpringContextTests*/ {
    private static final Logger logger = Logger.getLogger(TestXMPP.class);
    private String user;
    private String pw;
    private volatile boolean connected = true;
    private volatile XMPPConnection xmppConnection;
    private Executor executor = Executors.newSingleThreadExecutor();
    private CyclicBarrier barrier;

    @Before
    public void setup() throws Throwable {
        user = System.getProperty("xmpp.test.smack.user");
        pw = System.getProperty("xmpp.test.smack.pw");
        Assert.assertTrue("the user can't be null", !StringUtils.isEmpty(user));
        Assert.assertTrue("the pw can't be null", !StringUtils.isEmpty(pw));

        barrier = new CyclicBarrier(1,
                new Runnable() {
                    public void run() {
                        logger.debug("thread's done waiting! Disconnecting!");
                        xmppConnection.disconnect();
                        connected = false;
                        Assert.assertTrue("the connection should have been cut by now", !xmppConnection.isConnected());
                    }
                });
    }

    @Test
    public void testCreatingAnXMPPConn() throws Throwable {
        XMPPConnectionFactory xmppConnectionFactory = new XMPPConnectionFactory();
        xmppConnectionFactory.setHost("talk.google.com");
        xmppConnectionFactory.setPassword(pw);
        xmppConnectionFactory.setUser(user);
        xmppConnectionFactory.setPort(5222);
        xmppConnectionFactory.setServiceName("gmail.com");
        xmppConnectionFactory.setSaslMechanismSupported("PLAIN");
        xmppConnectionFactory.setSaslMechanismSupportedIndex(0);
        xmppConnectionFactory.setResource("resource");
        xmppConnectionFactory.afterPropertiesSet();
        this.xmppConnection = xmppConnectionFactory.getObject();
        Assert.assertTrue("the connection should have been made by now", this.xmppConnection.isConnected());
        this.executor.execute(new MyRunnable(barrier));

        while (connected) {
            Thread.sleep(500);
        }
    }
}


class MyRunnable implements Runnable {
    private static final Logger logger = Logger.getLogger(MyRunnable.class);
    private CyclicBarrier barrier;

    MyRunnable(CyclicBarrier barrier) {
        this.barrier = barrier;
    }

    public void run() {
        try {
            Thread.sleep(10 * 1000);
            barrier.await();
        } catch (Exception e) {
            logger.debug("exception thrown when trying to disable the connection", e);
        }
    }
}
