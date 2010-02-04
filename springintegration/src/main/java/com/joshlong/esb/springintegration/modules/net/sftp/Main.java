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

import org.apache.commons.lang.SystemUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;


/**
 * OSCONTRIB-5
 * <p/>
 * todo: threading (so that the synchronizer works with a thread to constantly work)
 * todo investigate more effective ways of downloading (right now its an input strean, but hwo does that approach scale w/ 20gb files?)
 * todo : add in a MessageSource so that this can play well with Spring Integration.
 * Also, add in support for delivering newly minted files using one of the FileReadingMssageSource or my native filesystem message source.
 * this message source is ultimately what 'delivers' news of the files that have been synched from the remote server
 * <p/>
 * <p/>
 * <code>SFTPMain</code> was more a dry run then my test harness.. I need a Main to do work against
 */
public class Main {
    static public void main(String[] args) throws Throwable {

        // configuration
        String host = "jlong",
                pw = "cowbell",
                usr = "jlong",
                remotePath = "/home/jlong/remote_mount",
                localPath = "local_mount";
        int port = 22;

        // local path
        File local = new File(SystemUtils.getUserHome(), localPath); // obviously this is just for test. Do what you need to do in your own
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