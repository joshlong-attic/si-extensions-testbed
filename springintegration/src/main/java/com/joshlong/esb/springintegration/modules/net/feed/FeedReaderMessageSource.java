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

import com.sun.syndication.feed.synd.SyndFeed;
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
import org.springframework.integration.message.MessageBuilder;
import org.springframework.integration.message.MessageSource;

import java.net.URL;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The idea behind this class is that {@link org.springframework.integration.message.MessageSource#receive()} will only
 * return a {@link SyndFeed} when the event listener tells us that a feed has been updated. If we can ascertain that
 * it's been updated, then we can add the item to the {@link java.util.Queue} implementation. lol
 *
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 * @author <a href="mailto:mario.gray@gmail.com">Mario Gray</a>
 */
public class FeedReaderMessageSource implements InitializingBean, Lifecycle, MessageSource<SyndFeed> {
    private static final Logger logger = Logger.getLogger(FeedReaderMessageSource.class);
    private volatile boolean running;
    private volatile String feedUrl;
    private volatile URL feedURLObject;
    private volatile FeedFetcherCache fetcherCache;
    private volatile HttpURLFeedFetcher fetcher;
    private volatile ConcurrentLinkedQueue<SyndFeed> syndFeeds;
    private volatile MyFetcherListener myFetcherListener;

    public FeedReaderMessageSource() {
        syndFeeds = new ConcurrentLinkedQueue<SyndFeed>();
    }

    public void afterPropertiesSet() throws Exception {
        myFetcherListener = new MyFetcherListener();
        fetcherCache = HashMapFeedInfoCache.getInstance();

        fetcher = new HttpURLFeedFetcher(fetcherCache);

        // fetcher.set
        fetcher.addFetcherEventListener(myFetcherListener);
        assert !StringUtils.isEmpty(feedUrl) : "the feedUrl can't be null!";
        feedURLObject = new URL(this.feedUrl);
    }

    public void start() {
        this.running = true;
    }

    public void stop() {
        this.running = false;
    }

    public SyndFeed receiveSyndFeed() {
        SyndFeed returnedSyndFeed = null;

        try {
            fetcher.retrieveFeed(this.feedURLObject);
            logger.debug("attempted to retrieve feed '" + this.feedUrl + "'");
            returnedSyndFeed = syndFeeds.poll();

            if (null == returnedSyndFeed) {
                logger.debug("no feeds updated, return null!");
                return null;
            }
        }
        catch (Throwable e) {
            logger.debug("Exception thrown when trying to retrive feed at url '" + this.feedURLObject + "'", e);
        }

        return returnedSyndFeed;
    }

    public Message<SyndFeed> receive() {
        SyndFeed syndFeed = this.receiveSyndFeed();
        if (null == syndFeed) {
            return null;
        }
        return MessageBuilder.withPayload(syndFeed).setHeader(FeedConstants.FEED_URL, this.feedURLObject).build();
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

    static private void test(String url, long delay) throws Throwable {
        FeedReaderMessageSource feedReaderMessageSource = new FeedReaderMessageSource();
        feedReaderMessageSource.setFeedUrl(url);
        feedReaderMessageSource.afterPropertiesSet();
        feedReaderMessageSource.start();

        while (true) {
            Message<SyndFeed> msgWithSyndFeed = feedReaderMessageSource.receive();

            if (msgWithSyndFeed != null) {
                SyndFeed feed = msgWithSyndFeed.getPayload();

                for (Object o : feed.getEntries()) {
                    //    logger.debug(o);
                }
            }

            Thread.sleep(delay);
        }
    }

    public static void main(String[] args) throws Throwable {
        String siweb = "http://twitter.com/statuses/public_timeline.atom"; //http://localhost:8080/siweb/foo.atom";
        test(siweb, 1000);
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
                syndFeeds.add(event.getFeed());
            }
            else if (FetcherEvent.EVENT_TYPE_FEED_UNCHANGED.equals(eventType)) {
                logger.debug("\tEVENT: Feed Unchanged. URL = " + event.getUrlString());

            }
        }
    }
}
