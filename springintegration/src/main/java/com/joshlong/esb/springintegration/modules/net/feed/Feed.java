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
 * I'm thinking we might want one level of indirection between our clients and the RSS/ATOM library that we use. I'm not
 * really sure though. I'll err on the side of caution and use a wrapper for {@link
 * com.sun.syndication.feed.synd.SyndFeed} I'll make it so that you can get to it through the {@link
 * com.joshlong.esb.springintegration.modules.net.feed.Feed#getDelegate()} method. TODO how much should I go through
 * with?
 *
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
public interface Feed {
    Object getDelegate();

}
