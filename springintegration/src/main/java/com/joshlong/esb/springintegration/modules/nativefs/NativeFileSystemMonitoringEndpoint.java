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

package com.joshlong.esb.springintegration.modules.nativefs;

import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.integration.channel.MessageChannelTemplate;
import org.springframework.integration.core.MessageChannel;
import org.springframework.integration.message.MessageBuilder;

import java.io.File;

/**
 * This component will react to the file system and publish any new files (or updated files) on the request channel.
 * <p/>
 * TODO this should be a drop-in replacement for the polling version
 * TODO this should throw a unholy fit if we detect that java.library.path isn't set since this (clearly) won't work without it.
 */
public class NativeFileSystemMonitoringEndpoint {

    private static final Logger logger = Logger.getLogger(NativeFileSystemMonitoringEndpoint.class);

    private final MessageChannelTemplate channelTemplate = new MessageChannelTemplate();
    private transient NativeFileSystemMonitor nativeFileSystemMonitor;
    private transient Resource directory;
    private transient boolean autoCreateDirectory;

    public int getMaxQueuedValue() {
        return maxQueuedValue;
    }

    public void setMaxQueuedValue(int maxQueuedValue) {
        this.maxQueuedValue = maxQueuedValue;
    }

    private transient int maxQueuedValue = 1000;

    public Resource getDirectory() {
        return directory;
    }

    public void setDirectory(Resource directory) {
        this.directory = directory;
    }

    public void setRequestChannel(MessageChannel requestChannel) {
        this.channelTemplate.setDefaultChannel(requestChannel);
    }


    public boolean isAutoCreateDirectory() {
        return autoCreateDirectory;
    }

    public void setAutoCreateDirectory(boolean autoCreateDirectory) {
        this.autoCreateDirectory = autoCreateDirectory;
    }

    public NativeFileSystemMonitor getNativeFileSystemMonitor() {
        return nativeFileSystemMonitor;
    }

    public void setNativeFileSystemMonitor(NativeFileSystemMonitor nativeFileSystemMonitor) {
        this.nativeFileSystemMonitor = nativeFileSystemMonitor;
    }

    public void start() {

        try {

            nativeFileSystemMonitor = new NativeFileSystemMonitor(this.directory.getFile());
            nativeFileSystemMonitor.setAutoCreateDirectory(isAutoCreateDirectory());
            nativeFileSystemMonitor.setMaxQueueValue(getMaxQueuedValue());
            nativeFileSystemMonitor.monitor(new NativeFileSystemMonitor.FileAddedListener() {
                public void fileAdded(File dir, String fn) {

                    // todo make this send all the same headers as the polling one does
                    File file = new File(dir, fn);
                    channelTemplate.send(MessageBuilder.withPayload(file).build());
                }
            });

        } catch (Throwable th) {
            throw new RuntimeException(th);
        }
    }

}
