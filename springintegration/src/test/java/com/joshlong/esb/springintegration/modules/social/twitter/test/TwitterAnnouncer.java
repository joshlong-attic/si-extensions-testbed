package com.joshlong.esb.springintegration.modules.social.twitter.test;

import com.joshlong.esb.springintegration.modules.social.twitter.Tweet;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

@Component
public class TwitterAnnouncer {

    private Tweet lastRecievedTweet ;

    public Tweet getLastRecievedTweet() {
        return lastRecievedTweet;
    }

    @ServiceActivator
    public void announce(Tweet t){

        this.lastRecievedTweet= t;
        System.out.println ( "I just recieved a tweet :: "+t.getMessage());
    }
}
