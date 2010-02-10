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
package com.joshlong.esb.springintegration.modules.services.amazon.sqs;

import com.xerox.amazonws.sqs2.Message;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.Lifecycle;
import org.springframework.integration.message.MessageBuilder;
import org.springframework.integration.message.MessageSource;

import java.util.HashMap;
import java.util.Map;


/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
public class SQSMessageMessageSource implements MessageSource, Lifecycle, InitializingBean {
    private static final Logger logger = Logger.getLogger(SQSMessageMessageSource.class);
    private volatile AmazonSQS2Client amazonSQS2Client;
    private String amazonWebServicesAccessKey;
    private String amazonWebServicesHost;
    private String amazonWebServicesSecretKey;
    private String queueName;
    private volatile boolean extractPayload;
    private volatile boolean shouldAutoDeleteOnReciept;

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

    public boolean isExtractPayload() {
        return extractPayload;
    }

    public boolean isRunning() {
        return true;
    }

    public boolean isShouldAutoDeleteOnReciept() {
        return shouldAutoDeleteOnReciept;
    }

    public org.springframework.integration.core.Message<?> receive() {
        try {
            Message msg = this.amazonSQS2Client.receive(this.getQueueName(), this.isShouldAutoDeleteOnReciept());

            Map<String, Object> headers = new HashMap<String, Object>();

            for (String k : msg.getAttributes().keySet()) {
                headers.put(k, msg.getAttribute(k));
            }

            Object payload = isExtractPayload() ? msg.getMessageBody() : msg;

            headers.put(SQSConstants.MESSAGE_ID, msg.getMessageId());
            headers.put(SQSConstants.RECEIPT_HANDLE, msg.getReceiptHandle());

            return MessageBuilder.withPayload(payload).copyHeadersIfAbsent(headers).build();
        } catch (Exception e) {
            logger.debug("exception thrown when trying to retrieve message from SQS queue '" + this.queueName + "' ", e);
        }

        return null;
    }

    public void setAmazonWebServicesAccessKey(final String amazonWebServicesAccessKey) {
        this.amazonWebServicesAccessKey = amazonWebServicesAccessKey;
    }

    public void setAmazonWebServicesHost(final String amazonWebServicesHost) {
        this.amazonWebServicesHost = amazonWebServicesHost;
    }

    public void setAmazonWebServicesSecretKey(final String amazonWebServicesSecretKey) {
        this.amazonWebServicesSecretKey = amazonWebServicesSecretKey;
    }

    public void setExtractPayload(final boolean extractPayload) {
        this.extractPayload = extractPayload;
    }

    public void setQueueName(final String queueName) {
        this.queueName = queueName;
    }

    public void setShouldAutoDeleteOnReciept(final boolean shouldAutoDeleteOnReciept) {
        this.shouldAutoDeleteOnReciept = shouldAutoDeleteOnReciept;
    }

    public void start() {
    }

    public void stop() {
    }
}