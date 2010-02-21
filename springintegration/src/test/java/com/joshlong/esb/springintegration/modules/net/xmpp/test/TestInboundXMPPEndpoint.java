package com.joshlong.esb.springintegration.modules.net.xmpp.test;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */

@ContextConfiguration(locations =  {
        "/net/xmpp/test_xmpp_inbound_adapter.xml"}
)
public class TestInboundXMPPEndpoint extends AbstractJUnit4SpringContextTests {

    @Test
    public void testInboundXMPPEndpoint () throws Throwable {

        System.in.read();

    }
}
