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

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a> There are two goals here: - establish a mechanism to
 *         'monitor' an RSS/ATOM resource and publish events as inbound events - to 'update' an RSS/ATOM resource by
 *         taking outbound messages and updating a resource
 */
public class Main {
    // to watch it all work, see:

    // https://rome.dev.java.net/source/browse/rome/subprojects/fetcher/src/java/com/sun/syndication/fetcher/samples/FeedReaderMessageSource.java?rev=HEAD&content-type=text/vnd.viewcvs-markup

    /**
     * Ive setup a test app (run using mvn jetty:run, in the worst case) that generates a constantly changing rss/atom
     * feed. Now i need to writ ean adapter for it
     *
     * @throws Throwable
     */
    static public void rssReading1() throws Throwable {
    }

    static public void main(String[] ar) throws Throwable {
        rssReading1();
    }
}
