package com.joshlong.esb.springintegration.modules.social.twitter.test;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {"/social/twitter/sending_tweets_using_ns.xml"})
public class TestSendingUsingNamespace extends AbstractJUnit4SpringContextTests {

    @Test
    public void testTweeting() throws Throwable {

        long ctr = 0, s = 1000;
        while (ctr < s * 30) {

            Thread.sleep(s);
            ctr += s;
        }
    }
}
