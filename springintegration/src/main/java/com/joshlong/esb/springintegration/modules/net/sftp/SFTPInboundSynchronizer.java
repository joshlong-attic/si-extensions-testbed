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

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.integration.core.MessagingException;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.concurrent.ScheduledFuture;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
public class SFTPInboundSynchronizer implements InitializingBean/*, Lifecycle*/ {

    private static final Logger logger = Logger.getLogger(SFTPInboundSynchronizer.class);

    /**
     * taken from <code>FtpInboundSynchronizer</code>
     */
    static final String INCOMPLETE_EXTENSION = ".INCOMPLETE";
    private static final long DEFAULT_REFRESH_RATE = 10 * 1000; // 10 seconds 
    private volatile TaskScheduler taskScheduler;
    private volatile String remotePath;
    private volatile boolean autoCreatePath;
    private volatile SFTPSessionPool pool;
    private volatile Resource localDirectory;
    private volatile boolean running;
    private volatile ScheduledFuture<?> scheduledFuture;
    private volatile Trigger trigger = new PeriodicTrigger(DEFAULT_REFRESH_RATE);
    private volatile boolean shouldDeleteDownloadedRemoteFiles = false; // obviously this is fale

    public boolean isShouldDeleteDownloadedRemoteFiles() {
        return shouldDeleteDownloadedRemoteFiles;
    }

    public void setShouldDeleteDownloadedRemoteFiles(boolean shouldDeleteDownloadedRemoteFiles) {
        this.shouldDeleteDownloadedRemoteFiles = shouldDeleteDownloadedRemoteFiles;
    }

    public TaskScheduler getTaskScheduler() {
        return taskScheduler;
    }

    public void setTaskScheduler(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public ScheduledFuture<?> getScheduledFuture() {
        return scheduledFuture;
    }

    public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public void setTrigger(Trigger t) {
        this.trigger = t;
    }

    public void setAutoCreatePath(boolean autoCreatePath) {
        this.autoCreatePath = autoCreatePath;
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
    }

    public void setLocalDirectory(Resource localDirectory) {
        this.localDirectory = localDirectory;
    }

    public void setPool(SFTPSessionPool pool) {
        this.pool = pool;
    }

    @SuppressWarnings("ignored")
    private boolean copyFromRemoteToLocalDirectory(SFTPSession sftpSession,
                                                   ChannelSftp.LsEntry entry,
                                                   Resource localDir) throws Exception {

        logger.debug(String.format("attempting to sync remote file %s/%s to local file %s", remotePath,
                                   entry.getFilename(), localDir.getFile().getAbsolutePath()));

        File fileForLocalDir = localDir.getFile();

        File localFile = new File(fileForLocalDir, entry.getFilename());
        if (!localFile.exists()) {
            InputStream in = null;
            FileOutputStream fos = null;
            try {
                File tmpLocalTarget = new File(localFile.getAbsolutePath() + INCOMPLETE_EXTENSION);

                fos = new FileOutputStream(tmpLocalTarget);
                String remoteFqPath = this.remotePath + "/" + entry.getFilename();
                in = sftpSession.getChannel().get(remoteFqPath);
                IOUtils.copy(in, fos);

                if (tmpLocalTarget.renameTo(localFile)) {
                    // last step
                    if (isShouldDeleteDownloadedRemoteFiles()) {
                        logger.debug(String.format(
                                "isShouldDeleteDownloadedRemoteFiles == true; " + "attempting to remove remote path '%s'",
                                remoteFqPath));
                        sftpSession.getChannel().rm(remoteFqPath);
                    }

                }
                return true;
            }
            catch (Throwable th) {
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(fos);
            }
        }
        else {
            logger.debug(String.format("local file %s already exists. Not re-downloading it.",
                                       localFile.getAbsolutePath()));
            return true;
        }

        return false;

    }

    @SuppressWarnings("unchecked")
    public void synchronize() throws Exception {
        SFTPSession session = null;
        try {
            session = pool.getSession();
            session.start();
            ChannelSftp channelSftp = session.getChannel();
            Collection<ChannelSftp.LsEntry> files = channelSftp.ls(remotePath);
            for (ChannelSftp.LsEntry lsEntry : files) {
                if (lsEntry != null && !lsEntry.getAttrs().isDir() && !lsEntry.getAttrs().isLink()) {
                    copyFromRemoteToLocalDirectory(session, lsEntry, this.localDirectory);
                }
            }
        }
        catch (IOException e) {
            throw new MessagingException("couldn't synchronize remote to local directory", e);
        }
        finally {
            if (session != null && pool != null) {
                pool.release(session);
            }
        }

    }

    public boolean isRunning() {
        return running;
    }

    class SynchronizeTask implements Runnable {
        public void run() {
            try {
                synchronize();
            }
            catch (Throwable e) {
                logger.debug("couldn't invoke synchronize()", e);
            }
        }
    }

    /**
     * there be dragons this way ... This method will check to ensure that the remote directory exists. If the directory
     * doesnt exist, and autoCreatePath is configured to be true, then this method makes a few reasonably sane attempts
     * to create it. Otherwise, it fails fast.
     *
     * @param rPath the path on the remote SSH / SFTP server to create.
     *
     * @return whether or not the directory is there (regardless of whether we created it in this method or it already
     *         existed.)
     */
    private boolean checkThatRemotePathExists(String rPath) {
        SFTPSession session = null;
        ChannelSftp channelSftp = null;
        try {
            session = pool.getSession();
            assert session != null : "session's not null";
            session.start();
            channelSftp = session.getChannel();

            SftpATTRS attrs = channelSftp.stat(rPath);
            assert attrs != null && attrs.isDir() : "attrs can't be null, and should indicate that it's a directory!";

            return true;

        }
        catch (Throwable th) {
            logger.debug(
                    "exception throwing when trying to verify the presence of the remote rPath '" + rPath + "'. Will try to create the directory.",
                    th);
            if (this.autoCreatePath && pool != null && session != null) {
                try {
                    if (channelSftp != null) {
                        channelSftp.mkdir(rPath);
                        if (channelSftp.stat(rPath).isDir()) {
                            logger.debug(String.format("created directory successfully: %s", rPath));
                            return true;
                        }
                    }
                }
                catch (Throwable t) {
                    return false;
                }
            }
        }
        finally {
            if (pool != null && session != null) {
                pool.release(session);
            }
        }
        return false;

    }

    public void start() {
        if (running) {
            return;
        }

        assert checkThatRemotePathExists(remotePath) : "the remotePath had better exist!";

        assert taskScheduler != null : "'taskScheduler' is required";

        scheduledFuture = taskScheduler.schedule(new SynchronizeTask(), trigger);

        this.running = true;
        if (logger.isInfoEnabled()) {
            logger.info("Started " + this);
        }
    }

    public void stop() {
        if (!running) {
            return;
        }
        assert scheduledFuture != null : "scheduledFuture is null!";
        this.scheduledFuture.cancel(true);
        this.running = false;
        if (logger.isInfoEnabled()) {
            logger.info("Stopped " + this);
        }

    }

    public void afterPropertiesSet() throws Exception {
        assert (taskScheduler != null) : "taskScheduler can't be null!";
        assert (localDirectory != null) : "the localDirectory property must not be null!";

        File localDir = localDirectory.getFile();
        if (!localDir.exists()) {
            if (autoCreatePath) {
                if (!localDir.mkdirs()) {
                    throw new RuntimeException(String.format("couldn't create localDirectory %s",
                                                             this.localDirectory.getFile().getAbsolutePath()));
                }
            }
        }

    }

}
