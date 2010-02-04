package com.joshlong.esb.springintegration.modules.net.sftp;

import com.jcraft.jsch.ChannelSftp;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.integration.core.MessagingException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * this class takes files in a given remote directory and moves them to the local directory.
 * <p/>
 * It does not move files in a local server and move them to a remote directory.
 * <p/>
 * <p/>
 * <p/>
 * TODO get this working (once)
 * TODO then make it so that this thing is multi threaded using a TaskExecutor implementation (make it so that the taskExecutor is injectable and works
 * with Spring 3.0s impleentations)
 */
public class SFTPInboundSynchronizer implements InitializingBean/*, Lifecycle*/ {

    private static final Logger logger = Logger.getLogger(SFTPInboundSynchronizer.class);

    /**
     * taken from <code>FtpInboundSynchronizer</code>
     */
    static final String INCOMPLETE_EXTENSION = ".INCOMPLETE";
    private volatile String remotePath;
    private volatile boolean autoCreatePath;
    private volatile SFTPSessionPool pool;
    private volatile Resource localDirectory;


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


    /*public boolean isAutoCreatePath() {
        return autoCreatePath;
    }public String getRemotePath() {
        return remotePath;
    }
 public Resource getLocalDirectory() {
        return localDirectory;
    }
    public QueuedSFTPSessionPool getPool() {
        return pool;
    }*/

    @SuppressWarnings("ignored")
    private boolean copyFromRemoteToLocalDirectory(SFTPSession sftpSession, ChannelSftp.LsEntry entry, Resource localDir) throws Exception {
        logger.debug(String.format("attempting to sync remote file %s/%s to local file %s",
                remotePath,
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

                if (tmpLocalTarget.renameTo(localFile))
                    return true;

            } catch (Throwable th) {
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(fos);
            }
        } else {
            logger.debug("local file %s already exists. Not re-downloading it.");
        }


        return false;

    }

    @SuppressWarnings("unchecked")
    public void synchronize() throws Exception {
        SFTPSession session = null;
        try {
            session = pool.getSession();
            assert (session != null) : "the session can't be null!";

            session.start();

            ChannelSftp channelSftp = session.getChannel();
            Collection<ChannelSftp.LsEntry> files = channelSftp.ls(remotePath);

            for (ChannelSftp.LsEntry lsEntry : files) {
                if (lsEntry != null && !lsEntry.getAttrs().isDir() && !lsEntry.getAttrs().isLink()) {
                    copyFromRemoteToLocalDirectory(session, lsEntry, this.localDirectory);
                }
            }
        } catch (IOException e) {
            throw new MessagingException("couldn't synchronize remote to local directory", e);
        }
        finally {
            if (session != null && pool != null)
                pool.release(session);
        }

    }

    public void afterPropertiesSet() throws Exception {

        assert (localDirectory != null) : "the localDirectory property must not be null!";

        File localDir = localDirectory.getFile();
        if (!localDir.exists())
            if (autoCreatePath)
                if (!localDir.mkdirs())
                    throw new RuntimeException(String.format("couldn't create localDirectory %s", this.localDirectory.getFile().getAbsolutePath()));

    }

    /*
             FTPFile[] fileList = client.listFiles();
             try {
                 for (FTPFile ftpFile : fileList) {

                     if (ftpFile != null && ftpFile.isFile()) {
                         copyFileToLocalDirectory(client, ftpFile,
                                 this.localDirectory);
                     }
                 }
             }
             finally {
                 if (client != null) {
                     this.clientPool.releaseClient(client);
                 }
             }
         }
         catch (IOException e) {
             throw new MessagingException(
                     "Problem occurred while synchronizing remote to local directory",
                     e);
         }*/


    /*private boolean copyFileToLocalDirectory(FTPClient client, FTPFile ftpFile,
             Resource localDirectory) throws IOException, FileNotFoundException {
         String remoteFileName = ftpFile.getName();
         String localFileName = localDirectory.getFile().getPath() + "/"
                 + remoteFileName;
         File localFile = new File(localFileName);
         if (!localFile.exists()) {
             String tempFileName = localFileName + INCOMPLETE_EXTENSION;
             File file = new File(tempFileName);
             FileOutputStream fos = new FileOutputStream(file);
             try {
                 client.retrieveFile(remoteFileName, fos);
             }
             finally {
                 fos.close();
             }
             file.renameTo(localFile);
             return true;
         }
         else {
             return false;
         }
     }*/


    /*   public void start() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void stop() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isRunning() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }*/

}
