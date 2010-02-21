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
package com.joshlong.esb.springintegration.modules.services.amazon.sqs.config;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;

import org.springframework.integration.config.xml.AbstractOutboundChannelAdapterParser;
import org.springframework.integration.config.xml.AbstractPollingInboundChannelAdapterParser;
import org.springframework.integration.config.xml.IntegrationNamespaceUtils;

import org.w3c.dom.Element;


/**
 * This class handles the job of registering the parsers that ultimately make the namespace support for Amazon SQS2
 * work.
 *
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@SuppressWarnings("unused")
public class SQSNamespaceHandler extends NamespaceHandlerSupport {
    private static final Logger log = Logger.getLogger(SQSNamespaceHandler.class);
    private static final String PACKAGE_NAME = "com.joshlong.esb.springintegration.modules.services.amazon.sqs";

    public void init() {
        registerBeanDefinitionParser("inbound-channel-adapter", new SQSMessageSourceBeanDefinitionParser());
        registerBeanDefinitionParser("outbound-channel-adapter", new SQSMessageSendingConsumerBeanDefinitionParser());
    }

    /**
     * This handles the task of configuring the recipient.
     */
    private static class SQSMessageSendingConsumerBeanDefinitionParser extends AbstractOutboundChannelAdapterParser {
        @Override
        protected AbstractBeanDefinition parseConsumer(final Element element, final ParserContext parserContext) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(PACKAGE_NAME + ".SQSMessageSendingHandler");
            IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "access-key", "amazonWebServicesAccessKey");
            IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "destination", "queueName");
            IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "host", "amazonWebServicesHost");
            IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "secret-key", "amazonWebServicesSecretKey");

            return builder.getBeanDefinition();
        }
    }

    /**
     * This handles the task of configuring the recipient.
     */
    private static class SQSMessageSourceBeanDefinitionParser extends AbstractPollingInboundChannelAdapterParser {
        @Override
        protected String parseSource(final Element element, final ParserContext parserContext) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(PACKAGE_NAME + ".SQSMessageMessageSource");

            IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "access-key", "amazonWebServicesAccessKey");
            IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "delete-on-receipt", "shouldAutoDeleteOnReciept");
            IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "destination", "queueName");
            IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "host", "amazonWebServicesHost");
            IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "secret-key", "amazonWebServicesSecretKey");

            return BeanDefinitionReaderUtils.registerWithGeneratedName(builder.getBeanDefinition(), parserContext.getRegistry());
        }
    }
}
