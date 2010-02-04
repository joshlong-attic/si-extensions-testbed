package com.joshlong.esb.springintegration.modules.net.sftp;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.Lifecycle;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageSource;

import java.io.File;

/**
 * this will rely on a filereadingmessgesource to scan a file system for all files that *dont* match a given glob
 *
 *
 * TODO were going to use a FileReadingMessageSource to scan the local directory for any files that have been 'added' by the ftp process. is there some way to use our Native one instead? Is there some ommon, injectable interface? (MessageSource?)
 *
 *
 *
 */
public class SFTPFileSource implements MessageSource<File>, InitializingBean, Lifecycle {
    public void start() {

    }

    public void stop() {
     }

    public boolean isRunning() {
        return false;
    }

    public void afterPropertiesSet() throws Exception {
    }

    public Message<File> receive() {
        return null;
    }
}
