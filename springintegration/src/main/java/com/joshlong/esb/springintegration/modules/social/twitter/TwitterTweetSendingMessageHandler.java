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

package com.joshlong.esb.springintegration.modules.social.twitter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageDeliveryException;
import org.springframework.integration.message.MessageHandler;
import org.springframework.integration.message.MessageHandlingException;
import org.springframework.integration.message.MessageRejectedException;
import org.springframework.util.Assert;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/* 
 *  @author Josh Long 
 * 
 *  Produces status updates on behalf of the user.
 *
 * TODO what does <code>Ordered</code> buy us?
 **/
public class TwitterTweetSendingMessageHandler implements InitializingBean, MessageHandler/*, Ordered */ {
/*

    public static void main(String[] args) throws Throwable {
        try {
            ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext(
                    "08-2-adaptingexternalsystemstothebus.xml");
            classPathXmlApplicationContext.begin();
            DirectChannel channel = (DirectChannel) classPathXmlApplicationContext
                    .getBean("outboundTweets");
            Message<String> helloWorldMessage = MessageBuilder.withPayload(
                    "I wonder...").build();
            channel.send(helloWorldMessage);
        } catch (Throwable th) {
            logger.debug(ExceptionUtils.getFullStackTrace(th));
        }
    }
*/

    static private Logger logger = Logger
            .getLogger(TwitterTweetSendingMessageHandler.class);

    private volatile String username;
    private volatile String password;
    private volatile Twitter twitter;

    public void tweet(String tweet) {
        try {
            twitter.updateStatus(tweet);
        } catch (TwitterException e) {
            logger.debug(ExceptionUtils.getFullStackTrace(e));
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Twitter getTwitter() {
        return twitter;
    }

    public void setTwitter(Twitter twitter) {
        this.twitter = twitter;
    }

    public void afterPropertiesSet() throws Exception {
        if (twitter == null) {
            Assert.state(!StringUtils.isEmpty(username));
            Assert.state(!StringUtils.isEmpty(password));

            twitter = new Twitter();
            twitter.setUserId(username);
            twitter.setPassword(password);

        } else { // it isnt null, in which case it becomes canonical memory
            setPassword(twitter.getPassword());
            setUsername(twitter.getUserId());
        }

    }

    public void handleMessage(Message<?> message) throws MessageRejectedException, MessageHandlingException, MessageDeliveryException {
        Object payload = message.getPayload();
        if (payload instanceof String) {
            String msg = (String) payload;
            tweet(msg);
        } else if (payload instanceof Tweet) {
            Tweet twt = (Tweet) payload;
            tweet(twt.getMessage());
        } else
            throw new RuntimeException("Can't tweet! Payload was not a 'Tweet' or 'String'");
    }

    /*   public int getOrder() {
        return 0;
    }*/
}
