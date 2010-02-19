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

package com.joshlong.esb.springintegration.modules.net.feed.config;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractPollingInboundChannelAdapterParser;
import org.springframework.integration.config.xml.IntegrationNamespaceUtils;
import org.w3c.dom.Element;

/**
 * This is a rather tricky one. I've decided it's best to not get cute about it and to expose *one*
 * <em>inbound-channel-adapter</em>. The adapter will let the user pick which type of updated object they'd like to
 * return. By default it'll return new {@link com.sun.syndication.feed.synd.SyndEntry} objects (which represent
 * individual, new entries in a given feed). One adapter will return updated {@link
 * com.sun.syndication.feed.synd.SyndFeed} objects, or it can return updated {@link
 * com.sun.syndication.feed.synd.SyndEntry} objects.
 *
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
public class FeedNamespaceHandler extends NamespaceHandlerSupport {
    private static final String PACKAGE_NAME = "com.joshlong.esb.springintegration.modules.net.feed";
    private static final String TRUE = Boolean.TRUE.toString().toLowerCase();

    public void init() {
        registerBeanDefinitionParser("inbound-channel-adapter", new FeedMessageSourceBeanDefinitionParser());
    }

    private static class FeedMessageSourceBeanDefinitionParser extends AbstractPollingInboundChannelAdapterParser {
        @Override
        protected String parseSource(final Element element, final ParserContext parserContext) {
            String pftoe = StringUtils.defaultString(element.getAttribute(
                    "prefer-updated-feed-to-entries")).trim().toLowerCase();
            boolean preferFeed = pftoe.equalsIgnoreCase(TRUE);
            String className = PACKAGE_NAME + "." + (preferFeed ? "FeedReaderMessageSource" : "FeedEntryReaderMessageSource");
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(className);
            builder.addPropertyValue("feedUrl", element.getAttribute("feed"));

            if (!preferFeed) {
                IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "backlog-cache-size",
                                                                     "maximumBacklogCacheSize");
            }

            return BeanDefinitionReaderUtils.registerWithGeneratedName(builder.getBeanDefinition(),
                                                                       parserContext.getRegistry());
        }
    }
}
