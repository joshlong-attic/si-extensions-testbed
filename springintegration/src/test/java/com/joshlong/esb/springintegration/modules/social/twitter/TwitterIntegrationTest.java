package com.joshlong.esb.springintegration.modules.social.twitter;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;
import org.springframework.integration.message.MessageHandler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import javax.annotation.Resource;

@ContextConfiguration(locations = {"/social/twitter/sending_and_recieving_messages.xml"})
public class TwitterIntegrationTest extends AbstractJUnit4SpringContextTests {

    private static final Logger logger = Logger.getLogger(TwitterIntegrationTest.class.getName());

    @Resource(name = "inboundTweets")
    private DirectChannel in;
    @Resource(name = "outboundTweets")
    private DirectChannel out;

    private transient Tweet tweet;

    @Test
    public void testRecievingMessages() throws Throwable {

        String msg = "I wonder... " + System.currentTimeMillis();
        in.subscribe(new MessageHandler() {
            public void handleMessage(Message<?> message) {
                tweet = (Tweet) message.getPayload();
                System.out.println("tweet = " + tweet.toString());
            }
        });

        Message<String> helloWorldMessage = MessageBuilder.withPayload(msg).build();
        out.send(helloWorldMessage);


        long delay = 1000;
        long counter = 0;
        if (tweet != null)
            return;

        while (tweet == null && counter < (50 * 1000)) {
            Thread.sleep(delay);
            if (tweet != null) {
                return;
            }
            counter += delay;
        }
        Assert.fail("you should have recieved a tweet!");
    }


}
