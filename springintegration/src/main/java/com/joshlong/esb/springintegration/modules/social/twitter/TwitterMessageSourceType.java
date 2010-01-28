package com.joshlong.esb.springintegration.modules.social.twitter;

public enum TwitterMessageSourceType {
    @Deprecated DM, // TODO not supported atm
    FRIENDS,
    MENTIONS
}
