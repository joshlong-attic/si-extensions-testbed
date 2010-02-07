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

package com.joshlong.esb.springintegration.modules.social.twitter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;
import org.springframework.integration.message.MessageSource;
import org.springframework.util.Assert;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
public class TwitterMessageSource implements MessageSource<Tweet>, InitializingBean {

    static private Logger logger = Logger.getLogger(TwitterMessageSource.class);

    private volatile Queue<Tweet> cachedStatuses;
    private volatile String userId;
    private volatile String password;
    private volatile Twitter twitter;
    private volatile long lastStatusIdRetreived = -1;
    private TwitterMessageType twitterMessageType = TwitterMessageType.FRIENDS;

    public TwitterMessageType getTwitterMessageType() {
        return twitterMessageType;
    }

    public void setTwitterMessageType(TwitterMessageType twitterMessageType) {
        this.twitterMessageType = twitterMessageType;
    }

    public int getPagingCount() {
        return pagingCount;
    }

    public void setPagingCount(int pagingCount) {
        this.pagingCount = pagingCount;
    }

    private int pagingCount = 10;

    private Tweet buildTweetFromStatus(Status firstPost) {
        Tweet tweet = new Tweet(firstPost.getId(), firstPost.getUser()
                .getName(), firstPost.getCreatedAt(), firstPost.getText());
        return tweet;
    }

    public Message<Tweet> receive() {

        Assert.state(this.twitterMessageType != null, "the twitterMessageType can't be null!");
        Assert.state(cachedStatuses != null, "the cachedStatuses can't be null!");

        if (cachedStatuses.peek() == null) {
            Paging paging = new Paging();
            paging.setCount(getPagingCount());
            if (-1 != lastStatusIdRetreived) {
                paging.sinceId(lastStatusIdRetreived);
            }
            try {
                List<Status> statuses = new ArrayList<Status>();
                switch (getTwitterMessageType()) {
                    case DM:
                        throw new UnsupportedOperationException("we don't support receiving direct mentions yet!");

                    case FRIENDS:
                        statuses = twitter.getFriendsTimeline(paging);
                        break;
                    case MENTIONS:
                        statuses = twitter.getMentions(paging);
                        break;
                }

                if (cachedStatuses.peek() == null) {
                    for (Status status : statuses) {
                        this.cachedStatuses.add(buildTweetFromStatus(status));
                    }
                }

            }
            catch (TwitterException e) {
                logger.info(ExceptionUtils.getFullStackTrace(e));
                throw new RuntimeException(e);
            }
        }

        if (cachedStatuses.peek() != null) {
            // size() == 0 would be more obvious a test, but size() isn't constant time
            Tweet cachedStatus = cachedStatuses.poll();
            lastStatusIdRetreived = cachedStatus.getTweetId();
            return MessageBuilder.withPayload(cachedStatus).build();
        }
        return null;
    }

    public void afterPropertiesSet() throws Exception {
        System.out.println("after properties set for TwitterMessageSource!!");
        if (twitter == null) {
            Assert.state(!StringUtils.isEmpty(userId));
            Assert.state(!StringUtils.isEmpty(password));

            twitter = new Twitter();
            twitter.setUserId(userId);
            twitter.setPassword(password);

        }
        else { // it isnt null, in which case it becomes canonical memory
            setPassword(twitter.getPassword());
            setUserId(twitter.getUserId());
        }

        cachedStatuses = new ConcurrentLinkedQueue<Tweet>();
        lastStatusIdRetreived = -1;

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
/*

    public Twitter getTwitter() {
        return twitter;
    }

    public void setTwitter(Twitter twitter) {
        this.twitter = twitter;
    }
*/

}
