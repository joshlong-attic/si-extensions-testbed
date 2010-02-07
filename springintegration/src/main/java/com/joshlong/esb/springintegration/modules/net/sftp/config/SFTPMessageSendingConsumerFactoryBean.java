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

package com.joshlong.esb.springintegration.modules.net.sftp.config;

import com.joshlong.esb.springintegration.modules.net.sftp.QueuedSFTPSessionPool;
import com.joshlong.esb.springintegration.modules.net.sftp.SFTPSendingMessageHandler;
import com.joshlong.esb.springintegration.modules.net.sftp.SFTPSessionFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Supports the construction of a MessagHandler that knows how to take inbound #java.io.File objects and send them to a
 * remote destination.
 *
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
public class SFTPMessageSendingConsumerFactoryBean implements InitializingBean, FactoryBean<SFTPSendingMessageHandler> {
    private String username, host, keyFile, keyFilePassword, remoteDirectory, password;
    private boolean autoCreateDirectories;
    private int port;

    public SFTPSendingMessageHandler getObject() throws Exception {

        SFTPSessionFactory sessionFactory = SFTPSessionUtils.buildSftpSessionFactory(
                this.getHost(), this.getPassword(), this.getUsername(), this.getKeyFile(),
                this.getKeyFilePassword(), this.getPort());

        QueuedSFTPSessionPool queuedSFTPSessionPool = new QueuedSFTPSessionPool(15, sessionFactory);

        SFTPSendingMessageHandler sftpSendingMessageHandler = new SFTPSendingMessageHandler(queuedSFTPSessionPool);
        sftpSendingMessageHandler.setRemoteDirectory(this.getRemoteDirectory());
        return sftpSendingMessageHandler;
    }

    public Class<? extends SFTPSendingMessageHandler> getObjectType() {
        return SFTPSendingMessageHandler.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public int getPort() {
        return port;
    }

    public void setPort(final int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public String getKeyFile() {
        return keyFile;
    }

    public void setKeyFile(final String keyFile) {
        this.keyFile = keyFile;
    }

    public String getKeyFilePassword() {
        return keyFilePassword;
    }

    public void setKeyFilePassword(final String keyFilePassword) {
        this.keyFilePassword = keyFilePassword;
    }

    public String getRemoteDirectory() {
        return remoteDirectory;
    }

    public void setRemoteDirectory(final String remoteDirectory) {
        this.remoteDirectory = remoteDirectory;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public boolean isAutoCreateDirectories() {
        return autoCreateDirectories;
    }

    public void setAutoCreateDirectories(final boolean autoCreateDirectories) {
        this.autoCreateDirectories = autoCreateDirectories;
    }

    public void afterPropertiesSet() throws Exception {
        if (isAutoCreateDirectories()) {
            // todo figure out this value
        }

    }
}
