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

import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.FetcherEvent;
import com.sun.syndication.fetcher.FetcherListener;
import com.sun.syndication.fetcher.impl.FeedFetcherCache;
import com.sun.syndication.fetcher.impl.HashMapFeedInfoCache;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.Lifecycle;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageSource;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The idea behind this class is that {@link org.springframework.integration.message.MessageSource#receive()} will only
 * return a {@link SyndFeed} when the event listener tells us that a feed has been updated. If we can ascertain that
 * it's been updated, then we can add the item to the {@link java.util.Queue} implementation.
 *
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
public class FeedReaderMessageSource<SyndFeed> implements InitializingBean, Lifecycle, MessageSource<Feed> {
    private static final Logger logger = Logger.getLogger(FeedReaderMessageSource.class);
    private volatile boolean running;
    private volatile String feedUrl;
    private volatile FeedFetcherCache fetcherCache;
    private volatile FeedFetcher fetcher;
    private volatile Queue<SyndFeed> syndFeeds;

    public FeedReaderMessageSource() {
        syndFeeds = new ConcurrentLinkedQueue<SyndFeed>();
    }

    public void afterPropertiesSet() throws Exception {
        fetcherCache = HashMapFeedInfoCache.getInstance();
        fetcher = new HttpURLFeedFetcher(fetcherCache);

        assert !StringUtils.isEmpty(feedUrl) : "the feedUrl can't be null!";

    }

    public void start() {
        this.running = true;
    }

    public void stop() {
        this.running = false;
    }

    public Message<Feed> receive() {
        return null;
    }

    public boolean isRunning() {
        return this.running;
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public void setFeedUrl(final String feedUrl) {

        this.feedUrl = feedUrl;
    }

    class MyFetcherListener implements FetcherListener {
        /**
         * @see com.sun.syndication.fetcher.FetcherListener#fetcherEvent(com.sun.syndication.fetcher.FetcherEvent)
         */
        public void fetcherEvent(final FetcherEvent event) {
            String eventType = event.getEventType();
            if (FetcherEvent.EVENT_TYPE_FEED_POLLED.equals(eventType)) {
                logger.debug("\tEVENT: Feed Polled. URL = " + event.getUrlString());
            }
            else if (FetcherEvent.EVENT_TYPE_FEED_RETRIEVED.equals(eventType)) {
                logger.debug("\tEVENT: Feed Retrieved. URL = " + event.getUrlString());
            }
            else if (FetcherEvent.EVENT_TYPE_FEED_UNCHANGED.equals(eventType)) {
                logger.debug("\tEVENT: Feed Unchanged. URL = " + event.getUrlString());
            }
        }
    }
}
