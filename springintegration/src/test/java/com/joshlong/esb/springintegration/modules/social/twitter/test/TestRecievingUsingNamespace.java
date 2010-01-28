package com.joshlong.esb.springintegration.modules.social.twitter.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {"/social/twitter/recieving_tweets_using_ns.xml"})
public class TestRecievingUsingNamespace extends AbstractJUnit4SpringContextTests {

    @Autowired private TwitterAnnouncer twitterAnnouncer ;

    @Test
    public void testIt() throws Throwable {

        while( twitterAnnouncer.getLastRecievedTweet() == null)
            Thread.sleep( 10* 1000 );

        System.out.println( "the last recieved tweet was: " + twitterAnnouncer.getLastRecievedTweet().getMessage());


    }

}
