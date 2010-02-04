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

package com.joshlong.esb.springintegration.modules.net.sftp;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.Lifecycle;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageSource;

import java.io.File;

/**
 * this will rely on a filereadingmessgesource to scan a file system for all files that *dont* match a given glob
 * <p/>
 * <p/>
 * TODO were going to use a FileReadingMessageSource to scan the local directory for any files that have been 'added' by the ftp process. is there some way to use our Native one instead? Is there some ommon, injectable interface? (MessageSource?)
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
