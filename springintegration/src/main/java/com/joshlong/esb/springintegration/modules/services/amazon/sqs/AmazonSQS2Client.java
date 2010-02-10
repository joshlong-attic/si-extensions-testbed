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
import com.xerox.amazonws.sqs2.MessageQueue;
import com.xerox.amazonws.sqs2.QueueService;
import com.xerox.amazonws.sqs2.SQSUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * AMazon SQS (simple messagesNotYetRecieved service) is a powerful, REST-ful Message Queue. You can use it with no fear
 * of scalability issues. To use it with Spring integration we need a scalable way of hittnig that REST endpoint and
 * consuming messages and then forwarding them onto the bus We will use Typica to do the hard work of talking to the
 * service. Additionally, we'll use only Amazon SQS2 ,not the older one pre-early 2008. The goals here are to a) build a
 * inbound adapter that can recieve SQS messages and b) send SQS messages to a given messagesNotYetRecieved/topic
 * whatever SQS2 supports. For the all-there-is-to-know examples, visit: http://code.google.com/p/typica/wiki/TypicaSampleCode
 *
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
public class AmazonSQS2Client implements InitializingBean {
    private static final Logger logger = Logger.getLogger(AmazonSQS2Client.class);
    private volatile ConcurrentHashMap<String, MessageQueue> messageQueuesById;
    private volatile ConcurrentLinkedQueue<Message> messagesNotYetRecieved;
    private volatile QueueService queueService;
    private volatile String awsHost;
    private volatile String awsPassword;
    private volatile String awsUser;
    private int maxNumberOfMessageToReceive = 1000;
    private int queueReceiveInterval = 10 * 1000; // 10 seconds

    public AmazonSQS2Client(String awsUser, String awsPw, String awsHost) {
        this.messageQueuesById = new ConcurrentHashMap<String, MessageQueue>();
        this.messagesNotYetRecieved = new ConcurrentLinkedQueue<Message>();
        this.awsUser = awsUser;
        this.awsPassword = awsPw;
        this.awsHost = awsHost;
    }

    public void afterPropertiesSet() throws Exception {
        assert !StringUtils.isEmpty(awsUser) : "the amazon web services user must not be null!";
        assert !StringUtils.isEmpty(awsPassword) : "the amazon web services password must not be null!";
        assert !StringUtils.isEmpty(awsHost) : "the amazon web services host must not be null!";
        assert this.queueReceiveInterval > 0 : "thw queueReceiveInterval should be > 0";
        assert this.maxNumberOfMessageToReceive > 0 : "the maxNumberOfMessageToReceive should always be greater than 0";

        this.queueService = SQSUtils.getQueueService(this.awsUser, this.awsPassword, this.awsHost);
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof AmazonSQS2Client) {
            AmazonSQS2Client other = (AmazonSQS2Client) o;
            EqualsBuilder eqb = new EqualsBuilder();

            return eqb.append(other.getAwsHost(), this.getAwsHost()).append(other.getAwsPassword(), this.getAwsPassword()).append(other.getAwsUser(), this.getAwsUser()).isEquals();
        }

        return false;
    }

    public String getAwsHost() {
        return awsHost;
    }

    public String getAwsPassword() {
        return awsPassword;
    }

    public String getAwsUser() {
        return awsUser;
    }

    public int getMaxNumberOfMessageToReceive() {
        return maxNumberOfMessageToReceive;
    }

    public int getQueueReceiveInterval() {
        return queueReceiveInterval;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();

        return hashCodeBuilder.append(this.getAwsHost()).append(this.getAwsPassword()).append(this.getAwsUser()).toHashCode();
    }

    public Message receive(String queueId, boolean shouldDeleteOnReceipt)
            throws Exception {
        return this.receive(this.queueService, queueId, shouldDeleteOnReceipt);
    }

    public String send(String queueId, String msg) throws Exception {
        return this.send(this.queueService, queueId, msg);
    }

    public void setAwsHost(final String awsHost) {
        this.awsHost = awsHost;
    }

    public void setAwsPassword(final String awsPassword) {
        this.awsPassword = awsPassword;
    }

    public void setAwsUser(final String awsUser) {
        this.awsUser = awsUser;
    }

    public void setMaxNumberOfMessageToReceive(final int maxNumberOfMessageToReceive) {
        this.maxNumberOfMessageToReceive = maxNumberOfMessageToReceive;
    }

    public void setQueueReceiveInterval(final int queueReceiveInterval) {
        this.queueReceiveInterval = queueReceiveInterval;
    }

    private MessageQueue getAndCacheMessageQueue(QueueService qs, String queueId) {
        if (!this.messageQueuesById.containsKey(queueId)) {
            logger.debug("attempting to lookup queueId '" + queueId + "' - this may block indefinitely");
            this.messageQueuesById.put(queueId, SQSUtils.getQueueOrElse(qs, queueId));
            logger.debug("resolved queueId '" + queueId + "'");
        }

        return this.messageQueuesById.get(queueId);
    }

    private Message receive(QueueService queueService, String queueuId, boolean shouldDeleteOnReceipt)
            throws Exception {
        MessageQueue messageQueue = this.getAndCacheMessageQueue(queueService, queueuId);
        Message somethingToReturn = this.messagesNotYetRecieved.poll();

        if (null == somethingToReturn) {
            Message[] msgs = messageQueue.receiveMessages(this.maxNumberOfMessageToReceive);
            this.messagesNotYetRecieved.addAll(Arrays.asList(msgs));
            somethingToReturn = this.messagesNotYetRecieved.poll();
        }

        if (shouldDeleteOnReceipt && (somethingToReturn != null)) {
            messageQueue.deleteMessage(somethingToReturn);
        }

        return somethingToReturn;
    }

    private String send(QueueService queueService, String queueId, String msgPayload)
            throws Exception {
        Assert.state(!StringUtils.isEmpty(queueId), "the 'queueId' can't be empty!");

        MessageQueue messageQueue = getAndCacheMessageQueue(queueService, queueId);
        logger.debug("obtained messageQueue " + messageQueue.getUrl());

        String msgId = messageQueue.sendMessage(msgPayload);
        logger.debug(String.format("sent message '%s' on messagesNotYetRecieved ID '%s'", msgId, queueId));

        return msgId;
    }
}