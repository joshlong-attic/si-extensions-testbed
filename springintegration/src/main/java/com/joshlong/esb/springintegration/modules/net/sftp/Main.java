package com.joshlong.esb.springintegration.modules.net.sftp;

import org.apache.commons.lang.SystemUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;


/**
 * <code>SFTPMain</code> was more a dry run then my test harness.. I need a Main to do work against
 */
public class Main {
    static public void main(String[] args) throws Throwable {

        // configuration
        String host = "jlong", pw = "cowbell", usr = "jlong", remotePath = "/home/jlong/remote_mount", localPath = "local_mount";
        int port = 22;

        // local path
        File local = new File(SystemUtils.getUserHome(), localPath);
        Resource localDirectory = new FileSystemResource(local);

        // factory
        SFTPSessionFactory sftpSessionFactory = new SFTPSessionFactory();
        sftpSessionFactory.setPassword(pw);
        sftpSessionFactory.setPort(port);
        sftpSessionFactory.setRemoteHost(host);
        sftpSessionFactory.setUser(usr);
        sftpSessionFactory.afterPropertiesSet();


        // pool
        QueuedSFTPSessionPool queuedSFTPSessionPool = new QueuedSFTPSessionPool(sftpSessionFactory);
        queuedSFTPSessionPool.afterPropertiesSet();


        SFTPInboundSynchronizer sftpInboundSynchronizer = new SFTPInboundSynchronizer();
        sftpInboundSynchronizer.setLocalDirectory(localDirectory);
        sftpInboundSynchronizer.setRemotePath(remotePath);
        sftpInboundSynchronizer.setAutoCreatePath(true);
        sftpInboundSynchronizer.setPool(queuedSFTPSessionPool);
        sftpInboundSynchronizer.afterPropertiesSet();
        sftpInboundSynchronizer.synchronize();


    }
}
