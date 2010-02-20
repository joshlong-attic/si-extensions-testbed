package com.joshlong.esb.springintegration.modules.net.xmpp;

import org.jivesoftware.smack.XMPPConnection;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.Lifecycle;
import org.springframework.integration.endpoint.AbstractEndpoint;

/**
 * this message source logs in as a user and forwards any messages <em>to</em> that user on to downstream components.
 *
 * @see {@link org.jivesoftware.smack.XMPPConnection} the xmpconnection classs
 *
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
public class XMPPMessageEndpoint  extends AbstractEndpoint implements Lifecycle, InitializingBean{


    
    public XMPPMessageEndpoint(){ }



    private volatile XMPPConnection xmppConnection;



    @Override
    protected void doStart() {

    }

    @Override
    protected void doStop() {

    }

    @Override
    protected void onInit() throws Exception {
     assert xmppConnection!=null:"the xmppCnnection shouldn't be null";
    }
}
