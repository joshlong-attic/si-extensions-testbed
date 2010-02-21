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
package com.joshlong.esb.springintegration.modules.social.twitter.test;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;


/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@ContextConfiguration(locations =  {
    "/social/twitter/recieving_tweets_using_ns.xml"}
)
public class TestRecievingUsingNamespace extends AbstractJUnit4SpringContextTests {
    @Autowired
    private TwitterAnnouncer twitterAnnouncer;

    @Test
    public void testIt() throws Throwable {
        while (twitterAnnouncer.getLastRecievedTweet() == null) {
            Thread.sleep(10 * 1000);
        }

        System.out.println("the last recieved tweet was: " + twitterAnnouncer.getLastRecievedTweet().getMessage());
    }
}
