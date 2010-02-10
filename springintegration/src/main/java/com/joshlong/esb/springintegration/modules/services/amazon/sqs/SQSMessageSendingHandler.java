package com.joshlong.esb.springintegration.modules.services.amazon.sqs;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.Lifecycle;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageDeliveryException;
import org.springframework.integration.message.MessageHandler;
import org.springframework.integration.message.MessageHandlingException;
import org.springframework.integration.message.MessageRejectedException;


/**
 * This class takes inbound messages and publishes them to the SQS service.
 *
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
public class SQSMessageSendingHandler implements MessageHandler, Lifecycle, InitializingBean {
    private static final Logger logger = Logger.getLogger(SQSMessageMessageSource.class);
    private volatile AmazonSQS2Client amazonSQS2Client;
    private String amazonWebServicesAccessKey;
    private String amazonWebServicesHost;
    private String amazonWebServicesSecretKey;
    private String queueName;
    private volatile boolean running;

    public void afterPropertiesSet() throws Exception {
        // assert !StringUtils.isEmpty(this.amazonWebServicesHost);
        assert !StringUtils.isEmpty(this.amazonWebServicesAccessKey);
        assert !StringUtils.isEmpty(this.amazonWebServicesSecretKey);
        assert !StringUtils.isEmpty(this.queueName);

        this.amazonSQS2Client = new AmazonSQS2Client(this.amazonWebServicesAccessKey, this.amazonWebServicesSecretKey, this.amazonWebServicesHost);
        this.amazonSQS2Client.afterPropertiesSet();
    }

    public String getAmazonWebServicesAccessKey() {
        return amazonWebServicesAccessKey;
    }

    public String getAmazonWebServicesHost() {
        return amazonWebServicesHost;
    }

    public String getAmazonWebServicesSecretKey() {
        return amazonWebServicesSecretKey;
    }

    public String getQueueName() {
        return queueName;
    }

    public void handleMessage(Message<?> message) throws MessageRejectedException, MessageHandlingException, MessageDeliveryException {
        Object payload = message.getPayload();
        assert payload != null : "the payload can't be null!";

        try {
            if (payload instanceof String) {
                this.amazonSQS2Client.send(this.queueName, (String) payload);
            } else {
                this.amazonSQS2Client.send(this.queueName, payload.toString());
            }
        } catch (Exception e) {
            logger.debug("Something happened when trying to send mesage to queueName '" + this.queueName + "'", e);
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    public void setAmazonWebServicesAccessKey(String amazonWebServicesAccessKey) {
        this.amazonWebServicesAccessKey = amazonWebServicesAccessKey;
    }

    public void setAmazonWebServicesHost(String amazonWebServicesHost) {
        this.amazonWebServicesHost = amazonWebServicesHost;
    }

    public void setAmazonWebServicesSecretKey(String amazonWebServicesSecretKey) {
        this.amazonWebServicesSecretKey = amazonWebServicesSecretKey;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public void start() {
        this.running = true;
    }

    public void stop() {
        this.running = false;
    }
}