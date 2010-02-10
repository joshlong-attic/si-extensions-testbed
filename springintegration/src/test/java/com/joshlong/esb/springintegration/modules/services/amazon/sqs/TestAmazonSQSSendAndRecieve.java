package com.joshlong.esb.springintegration.modules.services.amazon.sqs;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {"/services/amazon/sqs/send_files_using_si.xml"})

public class TestAmazonSQSSendAndRecieve extends AbstractJUnit4SpringContextTests {
    @Test
    public void testFoo() throws Throwable {
        while (true) Thread.sleep(1000);
    }
}
