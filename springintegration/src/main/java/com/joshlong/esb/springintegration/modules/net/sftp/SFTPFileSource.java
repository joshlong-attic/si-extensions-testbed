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

package com.joshlong.esb.springintegration.modules.net.sftp;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.Lifecycle;
import org.springframework.integration.core.Message;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.message.MessageSource;

import java.io.File;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
public class SFTPFileSource implements MessageSource<File>, InitializingBean, Lifecycle {

    private SFTPInboundSynchronizer sftpInboundSynchronizer;
    private FileReadingMessageSource fileReadingMessageSource;

    public SFTPInboundSynchronizer getSftpInboundSynchronizer() {
        return sftpInboundSynchronizer;
    }

    public void setSftpInboundSynchronizer(SFTPInboundSynchronizer sftpInboundSynchronizer) {
        this.sftpInboundSynchronizer = sftpInboundSynchronizer;
    }

    public FileReadingMessageSource getFileReadingMessageSource() {
        return fileReadingMessageSource;
    }

    public void setFileReadingMessageSource(FileReadingMessageSource fileReadingMessageSource) {
        this.fileReadingMessageSource = fileReadingMessageSource;
    }

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
