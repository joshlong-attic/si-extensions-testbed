/*
 * Copyright 2010 the original author or authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.joshlong.esb.springintegration.modules.social.twitter;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Josh Long
 *         <p/>
 *         Simple struct/DTO for a Tweet from users.
 */
public class Tweet implements Serializable, Comparable<Tweet> {

    private static final long serialVersionUID = 1L;

    private long tweetId;

    public long getTweetId() {
        return tweetId;
    }

    public void setTweetId(long tweetId) {
        this.tweetId = tweetId;
    }

    private String message;
    private Date received;
    private String user;

    public Tweet(long tweetId, String fromUser, Date received, String msg) {
        this.received = received;
        this.tweetId = tweetId;
        this.message = msg;
        this.user = fromUser;
    }

    public Tweet() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getReceived() {
        return received;
    }

    public void setReceived(Date received) {
        this.received = received;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.tweetId).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Tweet))
            return false;
        Tweet other = (Tweet) obj;
        return new EqualsBuilder().append(this.tweetId, other.tweetId)
                .isEquals();
    }

    public int compareTo(Tweet o) {
        return ((Long) this.tweetId).compareTo(o.tweetId);
    }
}
