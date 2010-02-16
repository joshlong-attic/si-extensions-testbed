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

import com.sun.syndication.feed.synd.*;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * This class offers utility support for various feed-building exercises on top of the ROME API.
 *
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 * @see {@link com.sun.syndication.feed.synd.SyndFeed}
 * @see {@link com.sun.syndication.feed.synd.SyndEntry}
 */

public class FeedUtils implements InitializingBean {
    public SyndEntry createEntry(String title, String link, String description, Date createDate) {
        SyndEntry entry = new SyndEntryImpl();
        entry.setTitle(title);
        entry.setLink(link);
        entry.setPublishedDate(createDate);

        SyndContent entryDescription = new SyndContentImpl();
        entryDescription.setType("text/plain");
        entryDescription.setValue(description);
        entry.setDescription(entryDescription);

        return entry;
    }

    public <T> SyndFeed buildFeed(String feedType,
                                  String author,
                                  String title,
                                  String description,
                                  String link,
                                  Collection<T> inputs,
                                  ObjectToItemConvertorStrategy<T> tConv) {
        List<SyndEntry> entries = this.translateObjectsToSyndEntries(inputs, tConv);
        SyndFeed syndFeed = this.createFeed(feedType, author, title, description, link);
        syndFeed.setEntries(entries);
        return syndFeed;
    }

    public <T> List<SyndEntry> translateObjectsToSyndEntries(Collection<T> inputObjects,
                                                             ObjectToItemConvertorStrategy<T> tConvertor) {
        List<SyndEntry> entries = new ArrayList<SyndEntry>();

        if ((inputObjects != null) && (inputObjects.size() != 0)) {
            for (T t : inputObjects) {
                SyndEntry entry = createEntry(tConvertor.getTitle(t), tConvertor.getLink(t), tConvertor.getDescription(
                        t), tConvertor.getEntryPublishedDate(t));
                entries.add(entry);
            }
        }

        return entries;
    }

    public SyndFeed createFeed(String feedType, String author, String title, String description, String link) {
        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType(feedType);
        feed.setAuthor(author);
        feed.setDescription(description);
        feed.setLink(link);
        feed.setTitle(title);

        return feed;
    }

    public void afterPropertiesSet() throws Exception {
    }
}
