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

import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.ErrorHandler;

import java.io.File;

public class Main {

    static private final Logger logger = Logger.getLogger(Main.class);

    static SFTPSessionFactory sftpSessionFactory(String host, String pw, String usr,
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

    static void run(SFTPSessionFactory sftpSessionFactory, String lp, String rp) throws Throwable {

        // local path
        File local = new File(lp); // obviously this is just for test. Do what you need to do in your own

        // we are testing, after all
        if (local.exists() && local.list().length > 0) {
            for (File f : local.listFiles()) {
                if (!f.delete()) logger.debug("couldn't delete " + f.getAbsolutePath());
            }
        }

        Resource localDirectory = new FileSystemResource(local);

        // pool
        QueuedSFTPSessionPool queuedSFTPSessionPool = new QueuedSFTPSessionPool(sftpSessionFactory);
        queuedSFTPSessionPool.afterPropertiesSet();

        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10);
        taskScheduler.setErrorHandler(new ErrorHandler() {
            public void handleError(Throwable t) {
                logger.debug("error! ", t);
            }
        });

        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        taskScheduler.initialize();

        // synchronizer
        final SFTPInboundSynchronizer sftpInboundSynchronizer = new SFTPInboundSynchronizer();
        sftpInboundSynchronizer.setLocalDirectory(localDirectory);
        sftpInboundSynchronizer.setRemotePath(rp);
        sftpInboundSynchronizer.setAutoCreatePath(true);
        sftpInboundSynchronizer.setPool(queuedSFTPSessionPool);
        sftpInboundSynchronizer.setShouldDeleteDownloadedRemoteFiles(false);
        sftpInboundSynchronizer.setTaskScheduler(taskScheduler);
        sftpInboundSynchronizer.afterPropertiesSet();
        sftpInboundSynchronizer.start();
/*
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(60 * 1000); // 1 minute

                    sftpInboundSynchronizer.stop();

                } catch (InterruptedException e) {
                    // don't care
                }
            }
        }).start();
*/

    }

    static public void main(String[] args) throws Throwable {
        boolean testKey = true;
        SFTPSessionFactory factory = testKey ?
                                     sftpSessionFactory("joshlong.com", null, "ubuntu",
                                                        SystemUtils.getUserHome() + "/jlongec2.pem", null,
                                                        22) : // this wont work on your machine. get yer own!
                                     sftpSessionFactory("jlong", "cowbell", "jlong", null, null, 22);

        String suffix = (testKey ? "key" : "pass");
        run(factory, SystemUtils.getUserHome() + "/local_mount_" + suffix, "remote_mount_" + suffix);

    }
}
