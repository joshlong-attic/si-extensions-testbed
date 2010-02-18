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

package com.joshlong.esb.springintegration.modules.net.feed;

import com.sun.syndication.feed.synd.SyndEntry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.Lifecycle;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageSource;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
public class FeedEntryReaderMessageSource implements InitializingBean, MessageSource<SyndEntry>, Lifecycle {

    private volatile FeedReaderMessageSource feedReaderMessageSource;
    private volatile boolean running;

    public void start() {
        this.running = true;
    }

    public void afterPropertiesSet() throws Exception {

    }

    public void stop() {
        this.running = false;
    }

    public boolean isRunning() {
        return running;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Message<SyndEntry> receive() {
        return null;
    }
}
