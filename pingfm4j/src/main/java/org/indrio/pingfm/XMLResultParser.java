package org.indrio.pingfm;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.indrio.pingfm.beans.Message;
import org.indrio.pingfm.beans.Service;
import org.indrio.pingfm.beans.Trigger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XMLResultParser {
    private final Logger LOGGER = Logger.getLogger(XMLResultParser.class);
    SAXBuilder builder = new SAXBuilder();
    Element rspRootElement;

    public XMLResultParser(InputStream _xmlInputStream) {
        try {
            Document doc = builder.build(_xmlInputStream);
            rspRootElement = doc.getRootElement();
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getStatus() {
        return rspRootElement.getAttributeValue("status");
    }

    public String getTransactionId() {
        return rspRootElement.getChildTextTrim("transaction");
    }

    public String getMethod() {
        return rspRootElement.getChildTextTrim("method");
    }

    public String getErrorMessage() {
        return rspRootElement.getChildTextTrim("message");
    }

    @SuppressWarnings("unchecked")
    public List<Service> getServices() {
        List<Service> services = null;

        Element servicesElement = rspRootElement.getChild("services");

        if (servicesElement != null) {
            List<Element> elements = (List<Element>) servicesElement.getChildren("service");

            LOGGER.debug("contains service tag ? " + ((elements != null) && !elements.isEmpty()));

            if ((elements != null) && !elements.isEmpty()) {
                LOGGER.debug("elements size ? " + elements.size());

                services = new ArrayList<Service>();

                for (Element serviceElements : elements) {
                    Service service =
                            new Service(serviceElements.getAttributeValue("id"),
                                    serviceElements.getAttributeValue("name"));
                    service.setTrigger(new Trigger(serviceElements.getChildTextTrim("trigger"),
                            null));
                    service.setUrl(serviceElements.getChildTextTrim("url"));
                    service.setIcon(serviceElements.getChildTextTrim("icon"));

                    String methods = serviceElements.getChildTextTrim("methods");

                    service.setMethods((methods != null) ? Arrays.asList(methods.split(",")) : null);

                    services.add(service);
                }
            }
        }

        return services;
    }

    @SuppressWarnings("unchecked")
    public List<Trigger> getTrigger() {
        List<Trigger> triggers = null;

        Element triggersElement = rspRootElement.getChild("triggers");

        if (triggersElement != null) {
            List<Element> elements = (List<Element>) triggersElement.getChildren("trigger");

            LOGGER.debug("contains trigger tag ? " + ((elements != null) && !elements.isEmpty()));

            if ((elements != null) && !elements.isEmpty()) {
                triggers = new ArrayList<Trigger>();

                for (Element triggerElements : elements) {
                    Trigger trigger =
                            new Trigger(triggerElements.getAttributeValue("id"),
                                    triggerElements.getAttributeValue("method"));
                    List<Element> serviceElements = triggerElements.getChildren("service");

                    for (Element serviceElement : serviceElements) {
                        trigger.addService(new Service(
                                serviceElement.getAttributeValue("id"),
                                serviceElement.getAttributeValue("name")));
                    }

                    triggers.add(trigger);
                }
            }
        }

        return triggers;
    }

    @SuppressWarnings("unchecked")
    public List<Message> getMessages() {
        List<Message> messages = null;

        Element messagesElement = rspRootElement.getChild("messages");

        if (messagesElement != null) {
            List<Element> elements = (List<Element>) messagesElement.getChildren("message");

            LOGGER.debug("contains message tag ? " + ((elements != null) && !elements.isEmpty()));

            if ((elements != null) && !elements.isEmpty()) {
                messages = new ArrayList<Message>();

                LOGGER.debug("	> message size ? " + elements.size());

                for (Element messageElements : elements) {
                    LOGGER.debug("		> message id ? " + messageElements.getAttributeValue("id"));

                    Message message =
                            new Message(messageElements.getAttributeValue("id"),
                                    messageElements.getAttributeValue("method"));

                    Element dateElement = messageElements.getChild("date");

                    LOGGER.debug("dateElement null ? " + (dateElement == null));

                    if (dateElement != null) {
                        message.setDateRfc(dateElement.getAttributeValue("rfc"));
                        message.setDateUnix(dateElement.getAttributeValue("unix"));
                    }

                    Element contentElement = messageElements.getChild("content");

                    LOGGER.debug("contentElement null ? " + (contentElement == null));

                    if (contentElement != null) {
                        String title = contentElement.getChildTextTrim("title");
                        String body = contentElement.getChildTextTrim("body");

                        message.setContentTitle(((title != null)
                                ? new String(Base64.decodeBase64(title.getBytes())) : null));
                        message.setContentBody(((body != null)
                                ? new String(Base64.decodeBase64(body.getBytes())) : null));
                    }

                    messages.add(message);
                }
            }
        }

        return messages;
    }
}
