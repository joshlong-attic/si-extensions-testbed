/*
 * Copyright 2010 the original author or authors
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.joshlong.esb.springintegration.modules.net.sftp;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

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
        if (null == session) {
            session = this.sftpSessionFactory.getObject();
            if (queue.size() < maxPoolSize) {
                queue.add(session);
            }
        }
        if (null == session) session = queue.poll();
        return session;
    }

    public void release(SFTPSession session) {
        logger.debug("releasing " + session.toString());
        if (queue.size() < maxPoolSize) {
            queue.add(session);    // somehow one snuck in before <code>session</code> was finished!
        }
        else {
            dispose(session);
        }
    }

    private void dispose(SFTPSession s) {
        if (s == null) {
            return;
        }
        if (queue.contains(s)) //this should never happen, but if it does ...
        {
            queue.remove(s);
        }
        if (s.getChannel() != null && s.getChannel().isConnected()) s.getChannel().disconnect();
        if (s.getSession().isConnected()) s.getSession().disconnect();
    }

    public void afterPropertiesSet() throws Exception {
        assert maxPoolSize > 0 : "poolSize must be greater than 0!";
        queue = new ArrayBlockingQueue<SFTPSession>(maxPoolSize, true); // size, faireness to avoid starvation
        assert sftpSessionFactory != null : "sftpSessionFactory must not be null!";
    }
}
