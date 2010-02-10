package com.joshlong.esb.springintegration.modules.services.amazon.sqs;

import org.springframework.stereotype.Component;

@Component
public class Producer {
    public String message() {
        return System.currentTimeMillis() + "";
    }
}
