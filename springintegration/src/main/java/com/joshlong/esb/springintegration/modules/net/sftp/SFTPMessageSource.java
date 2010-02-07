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
import org.springframework.core.io.Resource;
import org.springframework.integration.core.Message;
import org.springframework.integration.file.AcceptOnceFileListFilter;
import org.springframework.integration.file.CompositeFileListFilter;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.PatternMatchingFileListFilter;
import org.springframework.integration.message.MessageSource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;

import java.io.File;
import java.util.regex.Pattern;

/**
 * this creates the message source that ultimately 'see's files on a local directory and forwards them on to the bus.
 * These files are asynchronously deposited into a folder via the SFTP synchronizer. This code is <i>very</i> influenced
 * by the #FtpFileSource class.
 *
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
public class SFTPMessageSource implements MessageSource<File>, InitializingBean, Lifecycle {

    private Trigger trigger;
    private TaskScheduler taskScheduler;
    private Resource localDirectory;
    private SFTPInboundSynchronizer sftpInboundSynchronizer;
    private FileReadingMessageSource fileReadingMessageSource;

    public SFTPMessageSource(FileReadingMessageSource fileSource,
                             SFTPInboundSynchronizer synchronizer) {
        this.fileReadingMessageSource = fileSource;
        this.sftpInboundSynchronizer = synchronizer;
        Pattern completePattern = Pattern.compile("^.*(?<!"
                                                  + SFTPInboundSynchronizer.INCOMPLETE_EXTENSION + ")$");
        fileSource.setFilter(new CompositeFileListFilter(
                new AcceptOnceFileListFilter(),
                new PatternMatchingFileListFilter(completePattern)));
    }

    public Resource getLocalDirectory() {
        return localDirectory;
    }

    public void setLocalDirectory(final Resource localDirectory) {
        this.localDirectory = localDirectory;
        this.fileReadingMessageSource.setInputDirectory(localDirectory);
        this.sftpInboundSynchronizer.setLocalDirectory(localDirectory);
    }

    public SFTPInboundSynchronizer getSftpInboundSynchronizer() {
        return sftpInboundSynchronizer;
    }

    public void setSftpInboundSynchronizer(final SFTPInboundSynchronizer sftpInboundSynchronizer) {
        this.sftpInboundSynchronizer = sftpInboundSynchronizer;
    }

    public FileReadingMessageSource getFileReadingMessageSource() {
        return fileReadingMessageSource;
    }

    public void setFileReadingMessageSource(final FileReadingMessageSource fileReadingMessageSource) {
        this.fileReadingMessageSource = fileReadingMessageSource;
    }

    public void start() {
        sftpInboundSynchronizer.start();
    }

    public void stop() {
        sftpInboundSynchronizer.stop();
    }

    public boolean isRunning() {
        return this.sftpInboundSynchronizer.isRunning();
    }

    public void afterPropertiesSet() throws Exception {
        sftpInboundSynchronizer.afterPropertiesSet();
    }

    public Message<File> receive() {
        return this.fileReadingMessageSource.receive();
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public void setTrigger(final Trigger trigger) {
        this.trigger = trigger;
        this.sftpInboundSynchronizer.setTrigger(trigger);
    }

    public TaskScheduler getTaskScheduler() {
        return taskScheduler;
    }

    public void setTaskScheduler(final TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
        sftpInboundSynchronizer.setTaskScheduler(taskScheduler);
    }

}
