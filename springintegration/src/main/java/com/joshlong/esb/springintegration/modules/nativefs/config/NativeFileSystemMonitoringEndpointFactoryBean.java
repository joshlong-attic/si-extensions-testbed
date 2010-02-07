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

package com.joshlong.esb.springintegration.modules.nativefs.config;

import com.joshlong.esb.springintegration.modules.nativefs.NativeFileSystemMonitoringEndpoint;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.integration.core.MessageChannel;

import java.io.File;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
public class NativeFileSystemMonitoringEndpointFactoryBean extends org.springframework.beans.factory.config.AbstractFactoryBean<com.joshlong.esb.springintegration.modules.nativefs.NativeFileSystemMonitoringEndpoint>
        implements ResourceLoaderAware, InitializingBean {

    private transient boolean autoStartup = true;
    private transient int maxQueuedValue;
    private transient ResourceLoader resourceLoader;
    private transient String directory;
    private transient Resource directoryResource;
    private transient boolean autoCreateDirectory;
    private transient MessageChannel requestChannel;

    public int getMaxQueuedValue() {
        return maxQueuedValue;
    }

    public void setMaxQueuedValue(int maxQueuedValue) {
        this.maxQueuedValue = maxQueuedValue;
    }

    public boolean isAutoStartup() {
        return autoStartup;
    }

    public void setAutoStartup(boolean autoStartup) {
        this.autoStartup = autoStartup;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public boolean isAutoCreateDirectory() {
        return autoCreateDirectory;
    }

    public void setAutoCreateDirectory(boolean autoCreateDirectory) {
        this.autoCreateDirectory = autoCreateDirectory;
    }

    public MessageChannel getRequestChannel() {
        return requestChannel;
    }

    public void setRequestChannel(MessageChannel requestChannel) {
        this.requestChannel = requestChannel;
    }

    @Override
    public Class<? extends NativeFileSystemMonitoringEndpoint> getObjectType() {
        return NativeFileSystemMonitoringEndpoint.class;
    }

    @Override
    protected NativeFileSystemMonitoringEndpoint createInstance() throws Exception {

        File f = new File(this.directory);
        if (this.isAutoCreateDirectory()) {
            if (!f.exists() && !f.mkdirs()) {
                throw new RuntimeException("couldn't create directory '" + this.directory + "'.");
            }
        }

        ResourceEditor editor = new ResourceEditor(this.resourceLoader);
        editor.setAsText(this.directory);
        this.directoryResource = (Resource) editor.getValue();
        NativeFileSystemMonitoringEndpoint nativeFileSystemMonitoringEndpoint = new NativeFileSystemMonitoringEndpoint();
        nativeFileSystemMonitoringEndpoint.setDirectory(this.directoryResource);
        nativeFileSystemMonitoringEndpoint.setRequestChannel(this.requestChannel);
        nativeFileSystemMonitoringEndpoint.setAutoCreateDirectory(this.autoCreateDirectory);
        nativeFileSystemMonitoringEndpoint.setMaxQueuedValue(this.maxQueuedValue);
        nativeFileSystemMonitoringEndpoint.setAutoStartup(this.isAutoStartup());
        nativeFileSystemMonitoringEndpoint.afterPropertiesSet();

        // todo add support for a filter 
        // todo add support for an 'auto-startup' boolean

        return nativeFileSystemMonitoringEndpoint;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;

    }
}
