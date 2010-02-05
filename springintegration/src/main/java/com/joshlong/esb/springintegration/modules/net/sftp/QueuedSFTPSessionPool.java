/** copyright */
package com.joshlong.esb.springintegration.modules.net.sftp;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;


/**
 * this is designed to keep multiple sessions going so that multiple subscribers
 */
public class QueuedSFTPSessionPool implements SFTPSessionPool, InitializingBean {

    static public final Logger logger = Logger.getLogger(QueuedSFTPSessionPool.class);
    static public final int DEFAULT_POOL_SIZE = 10;
    private Queue<SFTPSession> queue;
    private final SFTPSessionFactory sftpSessionFactory;
    private int maxPoolSize;

    public QueuedSFTPSessionPool(SFTPSessionFactory factory) {
        this(DEFAULT_POOL_SIZE, factory);
    }

    public QueuedSFTPSessionPool(int maxPoolSize, SFTPSessionFactory sessionFactory) {
        this.sftpSessionFactory = sessionFactory;
        this.maxPoolSize = maxPoolSize;
    }

    public SFTPSession getSession() throws Exception {
        SFTPSession session = this.queue.poll();
        if (null == session)
        {

            session = this.sftpSessionFactory.getObject();
            if( queue.size() < maxPoolSize)
                queue.add(session);
        }

       if(null == session)
       {
           session = this.queue.poll();
       }
        

        return prepareSFTPSession(session);
    }

    private SFTPSession prepareSFTPSession(SFTPSession sftpSession) {
       /* if( sftpSession.getSession().isConnected())
        {
            if(sftpSession.getChannel()!=null&& sftpSession.getChannel().isConnected())
            sftpSession.getChannel().disconnect();
            
            sftpSession.getSession().disconnect();
        }*/
        // todo some sort of connection quality checks
        return sftpSession;
    }

    public void release(SFTPSession session) {
        logger.debug("releasing " + session.toString());
       //  session.getChannel().disconnect();
      //  session.getSession().disconnect();
        queue.add(session);
    }

    public void afterPropertiesSet() throws Exception {
        assert maxPoolSize > 0 : "poolSize must be greater than 0!";
        queue = new ArrayBlockingQueue<SFTPSession>(maxPoolSize,true); // size, faireness to avoid starvation
        
        assert sftpSessionFactory != null : "sftpSessionFactory must not be null!";

    }
}
