package com.joshlong.esb.springintegration.modules.net.xmpp.config;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
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
    static private Logger logger = Logger.getLogger(XMPPNamespaceHandler.class);

    public void init() {
        registerBeanDefinitionParser("xmpp-inbound-adapter", new XMPPInboundEndpointParser());
        registerBeanDefinitionParser("xmpp-outbound-adapter", new XMPPOutboundEndpointParser());
        registerBeanDefinitionParser("xmpp-connection", new XMPPConnectionParser());
    }

    private static void configureXMPPConnection(Element elementForXmppConnectionAttrs, BeanDefinitionBuilder targetBuilder, ParserContext parserContext) {
        String ref = elementForXmppConnectionAttrs.getAttribute("xmpp-connection");
        logger.debug("ref=" + ref);

        if (!StringUtils.isEmpty(ref)) {
            targetBuilder.addPropertyReference("xmppConnection", ref);
        } else {
            for (String attr : "user,password,host,service-name,resource,sasl-mechanism-supported,sasl-mechanism-supported-index,port".split(",")) {
                IntegrationNamespaceUtils.setValueIfAttributeDefined(targetBuilder, elementForXmppConnectionAttrs, attr);
            }
        }
    }

    private static class XMPPConnectionParser extends AbstractSingleBeanDefinitionParser {
        @Override
        protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
            logger.debug("element = " + element.toString());
            configureXMPPConnection(element, builder, parserContext);
        }

        @Override
        protected String getBeanClassName(Element element) {
            return PACKAGE_NAME + ".XMPPConnectionFactory";
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

    private static class XMPPOutboundEndpointParser extends AbstractSingleBeanDefinitionParser {
        @Override
        protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
            configureXMPPConnection(element, builder, parserContext);
            IntegrationNamespaceUtils.setReferenceIfAttributeDefined(builder, element, "channel", "requestChannel");
        }

        @Override
        protected String getBeanClassName(Element element) {
            return PACKAGE_NAME + ".XMPPMessageSendingMessageHandler";
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
