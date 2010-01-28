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

/* 
 *  @author Josh Long 
 * 
 *  Consumes messages from the Twitter that show up in a given 
 *  users timeline that weren't there before and feeds them.
 *   
 **/
public class TwitterMessageSource implements MessageSource<Tweet>, InitializingBean {


    static private Logger logger = Logger.getLogger(TwitterMessageSource.class);

    private volatile Queue<Tweet> cachedStatuses;
    private volatile String userId;
    private volatile String password;
    private volatile Twitter twitter;
    private volatile long lastStatusIdRetreived = -1;
    private TwitterMessageSourceType twitterMessageSourceType = TwitterMessageSourceType.FRIENDS;

    public TwitterMessageSourceType getTwitterMessageSourceType() {
        return twitterMessageSourceType;
    }

    public void setTwitterMessageSourceType(TwitterMessageSourceType twitterMessageSourceType) {
        this.twitterMessageSourceType = twitterMessageSourceType;
    }

    private Tweet buildTweetFromStatus(Status firstPost) {
        Tweet tweet = new Tweet(firstPost.getId(), firstPost.getUser()
                .getName(), firstPost.getCreatedAt(), firstPost.getText());
        return tweet;
    }

    public Message<Tweet> receive() {

        Assert.state(this.twitterMessageSourceType != null, "the twitterMessageSourceType can't be null!");
        Assert.state(cachedStatuses != null, "the cachedStatuses can't be null!");

        if (cachedStatuses.peek() == null) {
            Paging paging = new Paging();
            paging.setCount(20);
            if (-1 != lastStatusIdRetreived) {
                paging.sinceId(lastStatusIdRetreived);
            }
            try {
                List<Status> statuses = new ArrayList<Status>();
                switch (this.twitterMessageSourceType) {
                    case DM:
                        throw new UnsupportedOperationException("we don't support recieving direct mentions yet!");

                    case FRIENDS:
                        statuses = twitter.getFriendsTimeline(paging);
                        break;
                    case MENTIONS:
                        statuses = twitter.getMentions(paging);
                        break;
                }

                Assert.state(cachedStatuses.peek() == null);
                for (Status status : statuses)
                    this.cachedStatuses.add(buildTweetFromStatus(status));

            } catch (TwitterException e) {
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

    public Twitter getTwitter() {
        return twitter;
    }

    public void setTwitter(Twitter twitter) {
        this.twitter = twitter;
    }

}
