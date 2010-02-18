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
import com.sun.syndication.feed.synd.SyndFeed;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.Lifecycle;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;
import org.springframework.integration.message.MessageSource;

import java.net.URL;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * this is a slightly different use case than {@link com.joshlong.esb.springintegration.modules.net.feed.FeedReaderMessageSource}.
 * This returns which entries are added, which is a more nuanced use case requiring some of our own caching.
 * <em>NB:</em> this does <strong>not</strong> somehow detect entry removal from a feed.
 *
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
public class FeedEntryReaderMessageSource implements InitializingBean, MessageSource<SyndEntry>, Lifecycle {
    private volatile Collection<SyndEntry> receivedEntries;
    private volatile FeedReaderMessageSource feedReaderMessageSource;
    private volatile boolean running;
    private String feedUrl;
    private URL feedUrlObject;
    private Queue<SyndEntry> entries;
    private long maximumBacklogCacheSize = -1;

    public FeedEntryReaderMessageSource() {
        this.receivedEntries = new ConcurrentSkipListSet<SyndEntry>();
        this.entries = new ConcurrentLinkedQueue<SyndEntry>();
    }

    public void start() {
        this.feedReaderMessageSource.start();
        this.running = true;
    }

    public void afterPropertiesSet() throws Exception {
        assert !StringUtils.isEmpty(this.feedUrl) : "the feedUrl can't be null!";
        this.feedUrlObject = new URL(this.feedUrl);

        this.feedReaderMessageSource = new FeedReaderMessageSource();
        this.feedReaderMessageSource.setFeedUrl(this.feedUrl);
    }

    public void stop() {
        this.feedReaderMessageSource.stop();
        this.running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public Message<SyndEntry> receive() {
        SyndEntry se = receiveSyndEntry();

        if (se == null) {
            return null;
        }

        return MessageBuilder.withPayload(se).build();
    }

    @SuppressWarnings("unchecked")
    public SyndEntry receiveSyndEntry() {
        // priority goes to the backlog
        SyndEntry nextUp = pollAndCache();

        if (nextUp != null) {
            return nextUp;
        }

        // otherwise, fill the backlog up
        SyndFeed syndFeed = this.feedReaderMessageSource.receiveSyndFeed();

        if (syndFeed == null) {
            return null;
        }

        Collection<SyndEntry> feedEntries = (Collection<SyndEntry>) syndFeed.getEntries();

        for (SyndEntry se : feedEntries) {
            if (!this.receivedEntries.contains(se)) {
                entries.add(se);
            }
        }

        return pollAndCache();
    }

    private SyndEntry pollAndCache() {
        SyndEntry next = this.entries.poll();

        if ((this.maximumBacklogCacheSize != -1) && (this.receivedEntries.size() > this.maximumBacklogCacheSize)) {
            // whats the correct behavior here?

            // if we were doing LRU we'd evict as many entries from the end as needed until the collection was appropriately sized (N<maximumBacklogCacheSize)
            // however, i dont see why we cant just remove them all this component doesn't guarantee once and only once semantics. were doing our level headed best to ensure dupes arent sent
            // but if its really an issue then the user can leave {@link maximumBacklogCacheSize } at -1.
            this.receivedEntries.clear();
        }

        if (next != null) {
            this.receivedEntries.add(next);
        }

        return next;
    }

    public static void main(String[] args) throws Throwable {
        String siweb = "http://twitter.com/statuses/public_timeline.atom"; //http://localhost:8080/siweb/foo.atom";
        FeedEntryReaderMessageSource feedEntryReaderMessageSource = new FeedEntryReaderMessageSource();
        feedEntryReaderMessageSource.setFeedUrl(siweb);
        feedEntryReaderMessageSource.afterPropertiesSet();
        feedEntryReaderMessageSource.start();

        while (true) {
            Message<SyndEntry> entries = feedEntryReaderMessageSource.receive();

            if (entries != null) {

            }
        }
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public void setFeedUrl(final String feedUrl) {
        this.feedUrl = feedUrl;
    }

    public long getMaximumBacklogCacheSize() {
        return maximumBacklogCacheSize;
    }

    public void setMaximumBacklogCacheSize(final long maximumBacklogCacheSize) {
        this.maximumBacklogCacheSize = maximumBacklogCacheSize;
    }
}
