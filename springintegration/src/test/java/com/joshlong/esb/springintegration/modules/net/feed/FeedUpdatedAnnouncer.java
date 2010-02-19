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
import org.apache.log4j.Logger;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Component
public class FeedUpdatedAnnouncer {
    private static final Logger logger = Logger.getLogger(FeedUpdatedAnnouncer.class);

    @ServiceActivator
    public void announce(SyndEntry entry) {

        logger.debug(String.format("received entry with uri: %s and title:%s and contents: %s",
                                   entry.getUri(), entry.getTitle(), entry.getContents().toString()
        ));

    }
}
