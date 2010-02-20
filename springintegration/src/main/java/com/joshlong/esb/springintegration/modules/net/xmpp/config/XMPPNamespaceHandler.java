package com.joshlong.esb.springintegration.modules.net.xmpp.config;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;

import org.springframework.integration.config.xml.IntegrationNamespaceUtils;

import org.w3c.dom.Element;


/**
 * This class enables support for inbound and outbound XMPP support.
 *
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 * @see {@link }
 */
@SuppressWarnings("unused")
public class XMPPNamespaceHandler extends NamespaceHandlerSupport {
    static private final String PACKAGE_NAME = "com.joshlong.esb.springintegration.modules.net.xmpp";

    public void init() {
        registerBeanDefinitionParser("xmpp-inbound-endpoint", new XMPPInboundEndpointParser());
    }

    

    /**
     * This has a pre-condition that the bean being configured supports an property named xmppConnection of type {@link
     * org.jivesoftware.smack.XMPPConnection}
     *
     * @param elementForXmppConnectionAttrs the element to parse
     * @param targetBuilder                 the
     * @param parserContext
     */
    private static void configureXMPPConnection(Element elementForXmppConnectionAttrs, BeanDefinitionBuilder targetBuilder, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(com.joshlong.esb.springintegration.modules.net.xmpp.XMPPConnectionFactory.class);
        for (String attr : "user,password,host,service-name,resource,sasl-mechanism-supported,sasl-mechanism-supported-index,port".split(",")) {
            IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, elementForXmppConnectionAttrs, attr);
        }
        String registeredBean = BeanDefinitionReaderUtils.registerWithGeneratedName(builder.getBeanDefinition(), parserContext.getRegistry());
        targetBuilder.addPropertyValue("xmppConnection", registeredBean);
    }

    private static class XMPPInboundEndpointParser extends AbstractSingleBeanDefinitionParser {
        @Override
        protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
            configureXMPPConnection(element, builder, parserContext);
            IntegrationNamespaceUtils.setReferenceIfAttributeDefined(builder, element, "channel", "requestChannel");

        }

        @Override
        protected String getBeanClassName(Element element) {
            return PACKAGE_NAME + ".XMPPMessageEndpoint";
        }

        @Override
        protected boolean shouldGenerateId() {
            return false;
        }

        @Override
        protected boolean shouldGenerateIdAsFallback() {
            return true;
        }
    }
}
