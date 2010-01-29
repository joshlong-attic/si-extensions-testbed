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

package com.joshlong.esb.springintegration.modules.nativefs.config;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.IntegrationNamespaceUtils;
import org.w3c.dom.Element;

public class NativeFileSystemNamespaceHandler extends NamespaceHandlerSupport {

    private static final String PACKAGE_NAME = "com.joshlong.esb.springintegration.modules.nativefs";

    public void init() {
        registerBeanDefinitionParser("native-fs-event-driven-endpoint", new NativeFileSystemMonitoringEndpointParser());
    }

    private static class NativeFileSystemMonitoringEndpointParser extends AbstractSingleBeanDefinitionParser {
        @Override
        protected boolean shouldGenerateId() {
            return false;
        }

        @Override
        protected boolean shouldGenerateIdAsFallback() {
            return true;
        }

        @Override
        protected String getBeanClassName(Element element) {
            return PACKAGE_NAME + ".config.NativeFileSystemMonitoringEndpointFactoryBean";
        }

        @Override
        protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
            IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "directory");
            IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "auto-create-directory");
            IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "max-queued-value");
            IntegrationNamespaceUtils.setReferenceIfAttributeDefined(builder, element, "channel", "requestChannel");
            IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "auto-startup");

         
        }
    }
}
