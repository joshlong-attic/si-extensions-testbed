package com.joshlong.esb.springintegration.modules.social.twitter.test;

import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.util.Date;

@Component
public class TwitterProducer {

    public String tweet() {
        Date d = new Date();
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        return String.format("The time is %s and all is well!", dateFormat.format(d));
    }
}
