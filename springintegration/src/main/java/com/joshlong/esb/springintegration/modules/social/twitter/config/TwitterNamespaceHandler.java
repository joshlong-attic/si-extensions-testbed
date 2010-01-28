package com.joshlong.esb.springintegration.modules.social.twitter.config;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractPollingInboundChannelAdapterParser;
import org.springframework.integration.config.xml.IntegrationNamespaceUtils;
import org.w3c.dom.Element;


public class TwitterNamespaceHandler extends NamespaceHandlerSupport {

    private static final String PACKAGE_NAME = "com.joshlong.esb.springintegration.modules.social.twitter";

    public void init() {
        registerBeanDefinitionParser("inbound-channel-adapter", new TwitterMessageSourceBeanDefinitionParser());
    }

    private class TwitterMessageSourceBeanDefinitionParser extends AbstractPollingInboundChannelAdapterParser {
        @Override
        protected String parseSource(Element element, ParserContext parserContext) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(
                    PACKAGE_NAME + ".config.TwitterMessageSourceFactoryBean");


            //IntegrationNamespaceUtils.setReferenceIfAttributeDefined(builder, element, "comparator");
            IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "username");
            IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "password");
            return BeanDefinitionReaderUtils.registerWithGeneratedName(builder.getBeanDefinition(), parserContext.getRegistry());
        }

     }
}
