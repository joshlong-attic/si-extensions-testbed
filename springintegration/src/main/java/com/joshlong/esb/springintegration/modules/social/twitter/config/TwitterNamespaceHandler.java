/*
 * Copyright 2010 the original author or authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.joshlong.esb.springintegration.modules.social.twitter.config;

import com.joshlong.esb.springintegration.modules.social.twitter.TwitterMessageType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractOutboundChannelAdapterParser;
import org.springframework.integration.config.xml.AbstractPollingInboundChannelAdapterParser;
import org.springframework.integration.config.xml.IntegrationNamespaceUtils;
import org.w3c.dom.Element;


public class TwitterNamespaceHandler extends NamespaceHandlerSupport {
    static public String DIRECT_MESSAGES = "direct-messages";
    static public String MENTIONS = "mentions";
    static public String FRIENDS = "friends";


    static private TwitterMessageType handleParsingMessageType(BeanDefinitionBuilder builder, Element element, ParserContext parserContext) {
        String typeAttr = element.getAttribute("type");
        if (!StringUtils.isEmpty(typeAttr)) {


            if (typeAttr.equalsIgnoreCase(FRIENDS))
                return TwitterMessageType.FRIENDS;

            if (typeAttr.equalsIgnoreCase(MENTIONS))
                return TwitterMessageType.MENTIONS;
            if (typeAttr.equalsIgnoreCase(DIRECT_MESSAGES))
                return TwitterMessageType.DM;

        }

        return TwitterMessageType.FRIENDS;
    }

    private static final String PACKAGE_NAME = "com.joshlong.esb.springintegration.modules.social.twitter";

    public void init() {
        registerBeanDefinitionParser("inbound-channel-adapter", new TwitterMessageSourceBeanDefinitionParser());
        registerBeanDefinitionParser("outbound-channel-adapter", new TwitterMessageProducerBeanDefinitionParser());
    }

    private static class TwitterMessageSourceBeanDefinitionParser extends AbstractPollingInboundChannelAdapterParser {
        @Override
        protected String parseSource(Element element, ParserContext parserContext) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(
                    PACKAGE_NAME + ".config.TwitterMessageSourceFactoryBean");
            IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "username");
            IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "password");
            builder.addPropertyValue("type", handleParsingMessageType(builder, element, parserContext));
            return BeanDefinitionReaderUtils.registerWithGeneratedName(builder.getBeanDefinition(), parserContext.getRegistry());
        }
    }

    private static class TwitterMessageProducerBeanDefinitionParser extends AbstractOutboundChannelAdapterParser {
        @Override
        protected AbstractBeanDefinition parseConsumer(Element element, ParserContext parserContext) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(
                    PACKAGE_NAME + ".TwitterTweetSendingMessageHandler");
            IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "username");
            IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "password");
            builder.addPropertyValue("type", handleParsingMessageType(builder, element, parserContext));

            return builder.getBeanDefinition();
        }
    }
}
