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

import com.joshlong.esb.springintegration.modules.net.sftp.*;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.FileSystemResource;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;

import java.io.File;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a> TODO flesh this out
 */
public class SFTPMessageSourceFactoryBean extends AbstractFactoryBean<SFTPMessageSource> implements ApplicationContextAware {

    final private static Logger logger = Logger.getLogger(SFTPMessageSourceFactoryBean.class);

    /// implementation properties
    private Trigger trigger;
    private TaskScheduler taskScheduler;
    private FileReadingMessageSource fileReadingMessageSource;
    private SFTPInboundSynchronizer synchronizer;

    // connectivity properties
    private ApplicationContext applicationContext;
    private String username, password, host, keyFile, keyFilePassword;
    private boolean autoDeleteRemoteFilesOnSync;
    private int port = 22;
    private boolean autoCreateDirectories;
    private File localDirectory;

    private String remotePath;

    /**
     * This method hides the minutae required to build an #SFTPSessionFactory.
     *
     * @param host      the host to connect to.
     * @param usr       this is required. It is the username of the credentials being authenticated.
     * @param pw        if password authentication is being used (as opposed to key-based authentication) then this is
     *                  where you configure the password.
     * @param pvKey     the file that is the private key
     * @param pvKeyPass the passphrase used to use the key file
     * @param port      the default (22) is used if the value here is N< 0. The value should be only be set if the port
     *                  is non-standard (not 22)
     *
     * @return the SFTPSessionFactory that's used to create connections and get us in the right state to start issue
     *         commands against a remote SFTP/SSH filesystem
     *
     * @throws Throwable thrown in case of darned near <em>anything</em>
     */
    private SFTPSessionFactory buildSftpSessionFactory(String host, String pw, String usr,
                                                       String pvKey, String pvKeyPass,
                                                       int port) throws Throwable {
        SFTPSessionFactory sftpSessionFactory = new SFTPSessionFactory();
        sftpSessionFactory.setPassword(pw);
        sftpSessionFactory.setPort(port);
        sftpSessionFactory.setRemoteHost(host);
        sftpSessionFactory.setUser(usr);
        sftpSessionFactory.setPrivateKey(pvKey);
        sftpSessionFactory.setPrivateKeyPassphrase(pvKeyPass);
        sftpSessionFactory.afterPropertiesSet();

        return sftpSessionFactory;
    }

    @Override
    public Class<? extends SFTPMessageSource> getObjectType() {
        return SFTPMessageSource.class;
    }

    @Override
    protected SFTPMessageSource createInstance() throws Exception {

        try {
            fileReadingMessageSource = new FileReadingMessageSource();

            synchronizer = new SFTPInboundSynchronizer();
            if (null == taskScheduler) {
                taskScheduler = applicationContext.getBean(TaskScheduler.class);
            }

            if (null == taskScheduler) {    // todo what's the right behavior here??
                String msg = "couldn't get a taskScheduler!";
                logger.debug(msg);
                throw new RuntimeException(msg);
            }

            SFTPSessionFactory sessionFactory = this.buildSftpSessionFactory(
                    this.getHost(), this.getPassword(), this.getUsername(), this.getKeyFile(),
                    this.getKeyFilePassword(), this.getPort());

            SFTPSessionPool pool = new QueuedSFTPSessionPool(15, sessionFactory);

            synchronizer.setRemotePath(this.getRemotePath());
            synchronizer.setPool(pool);

            SFTPMessageSource sftpMessageSource = new SFTPMessageSource(fileReadingMessageSource, synchronizer);

            sftpMessageSource.setTaskScheduler(taskScheduler);
            sftpMessageSource.setTrigger(getTrigger());
            sftpMessageSource.setLocalDirectory(new FileSystemResource(this.getLocalDirectory()));
            sftpMessageSource.afterPropertiesSet();

            return sftpMessageSource;

        }
        catch (Throwable thr) {
            logger.debug("error occurred when trying to configure SFTPmessageSource ", thr);
        }

        return null;
    }

    // this is the ultimate layer of control
    // users will configure theeir entire experience using this class and trust that a working
    // component comes out as a result of their input
    // we need to support user/pw/keys/host/port/auto-delete properties

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
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

    public boolean isAutoDeleteRemoteFilesOnSync() {
        return autoDeleteRemoteFilesOnSync;
    }

    public void setAutoDeleteRemoteFilesOnSync(final boolean autoDeleteRemoteFilesOnSync) {
        this.autoDeleteRemoteFilesOnSync = autoDeleteRemoteFilesOnSync;
    }

    public int getPort() {
        return port;
    }

    public FileReadingMessageSource getFileReadingMessageSource() {
        return fileReadingMessageSource;
    }

    public void setFileReadingMessageSource(final FileReadingMessageSource fileReadingMessageSource) {
        this.fileReadingMessageSource = fileReadingMessageSource;
    }

    public SFTPInboundSynchronizer getSynchronizer() {
        return synchronizer;
    }

    public void setSynchronizer(final SFTPInboundSynchronizer synchronizer) {
        this.synchronizer = synchronizer;
    }

    public void setPort(final int port) {
        this.port = port;
    }

    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public boolean isAutoCreateDirectories() {
        return autoCreateDirectories;
    }

    public void setAutoCreateDirectories(final boolean autoCreateDirectories) {
        this.autoCreateDirectories = autoCreateDirectories;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public void setTrigger(final Trigger trigger) {
        this.trigger = trigger;
    }

    public File getLocalDirectory() {
        return localDirectory;
    }

    public void setLocalDirectory(final File localDirectory) {
        this.localDirectory = localDirectory;
    }

    public TaskScheduler getTaskScheduler() {
        return taskScheduler;
    }

    public void setTaskScheduler(final TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public String getRemotePath() {
        return remotePath;
    }

    public void setRemotePath(final String remotePath) {
        this.remotePath = remotePath;
    }
}
