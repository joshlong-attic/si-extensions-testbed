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

package com.joshlong.esb.springintegration.modules.net.feed.web.test;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedOutput;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * The whole purpose of this class is to generate new entries at a fixed interval so that, when I ultimately write a
 * client, it is updated.
 *
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Component("testRssWriter")
public class SimpleRSSSynthesizingServlet implements HttpRequestHandler, InitializingBean {

    static private Logger logger = Logger.getLogger(SimpleRSSSynthesizingServlet.class);

    static public String ATOM_03 = "atom_0.3";
    static public String RSS_20 = "rss_2.0";

    private Set<NewsItem> newsItems;
    private FeedUtils feedUtils;
    private ObjectToItemConvertorStrategy<NewsItem> objectToItemConvertorStrategy;

    public void write(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, SyndFeed feed)
            throws Throwable {
        httpServletResponse.setContentType("text/xml");

        SyndFeedOutput syndFeedOutput = new SyndFeedOutput();
        syndFeedOutput.output(feed, httpServletResponse.getWriter());
    }

    public void handleRequest(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        try {
            SyndFeed syndFeed = this.feedUtils.buildFeed(ATOM_03, "Josh Long", "JoshLong.com's Blog",
                                                         "Description Time", "http://www.joshlong.com", this.newsItems,
                                                         this.objectToItemConvertorStrategy);

            write(request, response, syndFeed);
        }
        catch (Throwable throwable) {
            logger.debug("Something happened when trying to write the Collection<NewsItem> collection", throwable);
        }
    }

    @Scheduled(fixedRate = 1000 * 10)
    public void addAnotherNewsItem() {
        Date newDate = new Date();
        long now = newDate.getTime();
        logger.debug("running addAnotherNewsItem()");

        NewsItem newsItem = new NewsItem("Title " + now, newDate, "Body " + now);
        this.newsItems.add(newsItem);
    }

    public void afterPropertiesSet() throws Exception {
        this.newsItems = new ConcurrentSkipListSet<NewsItem>();

        this.feedUtils = new FeedUtils();
        this.feedUtils.afterPropertiesSet();

        this.objectToItemConvertorStrategy = new MyNewsItemToItemConvertorStrategy();
    }
}

class MyNewsItemToItemConvertorStrategy implements ObjectToItemConvertorStrategy<NewsItem> {
    public String getTitle(final NewsItem newsItem) {
        return newsItem.getTitle();
    }

    public String getLink(final NewsItem newsItem) {
        return "http://www.joshlong.com/myarticles/foo/bar/" + newsItem.getId() + "/id/" + newsItem.getDate().getTime() + ".html";
    }

    public Date getEntryPublishedDate(final NewsItem newsItem) {
        return newsItem.getDate();
    }

    public String getDescription(final NewsItem newsItem) {
        return newsItem.getBody();
    }
}
