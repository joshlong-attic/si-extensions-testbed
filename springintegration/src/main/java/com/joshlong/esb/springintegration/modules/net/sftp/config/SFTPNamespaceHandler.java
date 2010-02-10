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
package com.joshlong.esb.springintegration.modules.net.sftp.config;

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
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@SuppressWarnings("unused")
public class SFTPNamespaceHandler extends NamespaceHandlerSupport {
    private static final String PACKAGE_NAME = "com.joshlong.esb.springintegration.modules.net.sftp";

    public void init() {
        registerBeanDefinitionParser("inbound-channel-adapter", new SFTPMessageSourceBeanDefinitionParser());
        registerBeanDefinitionParser("outbound-channel-adapter", new SFTPMessageSendingConsumerBeanDefinitionParser());
    }

    /**
     * Configures an object that can take inbound messages and send them.
     */
    private static class SFTPMessageSendingConsumerBeanDefinitionParser extends AbstractOutboundChannelAdapterParser {
        @Override
        protected AbstractBeanDefinition parseConsumer(Element element, ParserContext parserContext) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(PACKAGE_NAME + ".config.SFTPMessageSendingConsumerFactoryBean");

            for (String p : "auto-create-directories,username,password,host,key-file,key-file-password,remote-directory".split(",")) {
                IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, p);
            }

            return builder.getBeanDefinition();
        }
    }

    /**
     * Configures an object that can recieve files from a remote SFTP endpoint and broadcast their arrival to the
     * consumer
     */
    private static class SFTPMessageSourceBeanDefinitionParser extends AbstractPollingInboundChannelAdapterParser {
        @Override
        @SuppressWarnings("unused")
        protected String parseSource(Element element, ParserContext parserContext) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(PACKAGE_NAME + ".config.SFTPMessageSourceFactoryBean");

            for (String p : "auto-create-directories,username,password,host,key-file,key-file-password,remote-directory,local-working-directory,auto-delete-remote-files-on-sync".split(",")) {
                IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, p);
            }

            return BeanDefinitionReaderUtils.registerWithGeneratedName(builder.getBeanDefinition(), parserContext.getRegistry());
        }
    }
}