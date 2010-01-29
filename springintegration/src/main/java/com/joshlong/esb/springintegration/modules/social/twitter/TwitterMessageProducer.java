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
import org.springframework.util.Assert;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/* 
 *  @author Josh Long 
 * 
 *  Produces status updates on behalf of the user.
 *   
 **/
public class TwitterMessageProducer implements InitializingBean {
/*

    public static void main(String[] args) throws Throwable {
        try {
            ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext(
                    "08-2-adaptingexternalsystemstothebus.xml");
            classPathXmlApplicationContext.start();
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
            .getLogger(TwitterMessageProducer.class);

    private volatile String userId;
    private volatile String password;
    private volatile Twitter twitter;

    public void tweet(String tweet) {
        try {
            twitter.updateStatus(tweet);
        } catch (TwitterException e) {
            logger.debug(ExceptionUtils.getFullStackTrace(e));
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
            Assert.state(!StringUtils.isEmpty(userId));
            Assert.state(!StringUtils.isEmpty(password));

            twitter = new Twitter();
            twitter.setUserId(userId);
            twitter.setPassword(password);

        } else { // it isnt null, in which case it becomes canonical memory
            setPassword(twitter.getPassword());
            setUserId(twitter.getUserId());
        }

    }

}
