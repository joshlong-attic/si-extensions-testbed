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

package com.joshlong.esb.springintegration.modules.social.twitter.config;

import com.joshlong.esb.springintegration.modules.social.twitter.TwitterMessageSource;
import com.joshlong.esb.springintegration.modules.social.twitter.TwitterMessageSourceType;
import org.springframework.beans.factory.config.AbstractFactoryBean;

public class TwitterMessageSourceFactoryBean extends AbstractFactoryBean<TwitterMessageSource> {


    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public Class<? extends TwitterMessageSource> getObjectType() {
        return TwitterMessageSource.class;
    }

    @Override
    protected TwitterMessageSource createInstance() throws Exception {

        TwitterMessageSource twitterMessageSource = new TwitterMessageSource();
        twitterMessageSource.setPassword(this.password);
        twitterMessageSource.setUserId(this.username);
        twitterMessageSource.setTwitterMessageSourceType(TwitterMessageSourceType.FRIENDS);
        twitterMessageSource.afterPropertiesSet();

        return twitterMessageSource;
    }
}
