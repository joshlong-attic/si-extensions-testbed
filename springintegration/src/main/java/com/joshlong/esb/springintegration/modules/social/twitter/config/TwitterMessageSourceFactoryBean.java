package com.joshlong.esb.springintegration.modules.social.twitter.config;

import com.joshlong.esb.springintegration.modules.social.twitter.TwitterMessageSource;
import com.joshlong.esb.springintegration.modules.social.twitter.TwitterMessageSourceType;
import org.springframework.beans.factory.config.AbstractFactoryBean;

public class TwitterMessageSourceFactoryBean  extends AbstractFactoryBean<TwitterMessageSource> {


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
        return TwitterMessageSource.class ;
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
