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
package com.joshlong.esb.springintegration.modules.nativefs;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import org.springframework.core.task.SimpleAsyncTaskExecutor;

import org.springframework.util.Assert;

import java.io.File;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang.SystemUtils;


/**
 * TODO make this class use SI hooks ({@link org.springframework.context.Lifecycle#start()}, {@link
 * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()}, etc
 *
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 * @author <a href="mailto:mario.gray@gmail.com">Mario Gray</a>
 */
public class NativeFileSystemMonitor {
    private static final Logger logger = Logger.getLogger(NativeFileSystemMonitor.class);

    static {
        try {
        System.loadLibrary("sifsmon"); // todo  : should I make this in turn delegate to a System.getProperty call so we can move this data to launch arguments?
    }       catch (Throwable  t){
           logger. info ( "Received exception " + ExceptionUtils.getFullStackTrace( t)); ;

        }}


    private File directoryToMonitor;
    private volatile LinkedBlockingQueue<String> additions;
    private boolean autoCreateDirectory;
    private int maxQueueValue;
    private volatile Executor executor;

    public NativeFileSystemMonitor() {
    }

    public NativeFileSystemMonitor(File file) {
        this.directoryToMonitor = file;
    }

    public File getDirectoryToMonitor() {
        return directoryToMonitor;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public void init() {
        additions = new LinkedBlockingQueue<String>(this.maxQueueValue);

        boolean goodDirToMonitor = (directoryToMonitor.isDirectory() && directoryToMonitor.exists());

        if (!goodDirToMonitor) {
            if (!directoryToMonitor.exists()) {
                if (this.autoCreateDirectory) {
                    if (!directoryToMonitor.mkdirs()) {
                        logger.debug(String.format("couldn't create directory %s", directoryToMonitor.getAbsolutePath()));
                    }
                }
            }
        }

        if (this.executor == null) {
            this.executor = new SimpleAsyncTaskExecutor();
        }

        Assert.state(directoryToMonitor.exists(), "the directory " + directoryToMonitor.getAbsolutePath() + " doesn't exist");
    }

    /**
     * This method is what is called from our code to talk to the native code: Behind the scenes, this offers a native
     * inotify based event driven mechanism.
     *
     * @param path the path that should be monitored. I haven't done any checking to see well this plays with the C
     *             libraries that we're using.
     */
    public native void monitor(String path);

    public void monitor(final FileAddedListener fal) {
        final File nFile = new File(this.getDirectoryToMonitor().getAbsolutePath());

        final String absPath = nFile.getAbsolutePath();

        // I have no idea the implications of thread safety for this sort of thing
        this.executor.execute(new Runnable() {
            public void run() {
                do {
                    try {
                        fal.fileAdded(nFile, additions.take());
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                } while (true);
            }
        });

        monitor(absPath);
    }

    /**
     * This method is invoked FROM the C code, delegating to the
     */
    public void nativeFileRecieved(String fileName) {
        additions.add(fileName);
    }

    public void setAutoCreateDirectory(boolean autoCreateDirectory) {
        this.autoCreateDirectory = autoCreateDirectory;
    }

    public void setMaxQueueValue(int maxQueueValue) {
        this.maxQueueValue = maxQueueValue;
    }

    static interface FileAddedListener {
        void fileAdded(File dir, String fn);
    }


    public static void main(String[] args) throws Throwable {
        NativeFileSystemMonitor nfsm = new NativeFileSystemMonitor();
        nfsm.psvm();
    }

    public void psvm() throws Throwable {

        File desktop = new File(new File(SystemUtils.getUserHome(), "Desktop"), "test");
        NativeFileSystemMonitor nativeFileSystemMonitor = new NativeFileSystemMonitor(desktop);
        nativeFileSystemMonitor.setAutoCreateDirectory(true);
        nativeFileSystemMonitor.setMaxQueueValue(1000);
        nativeFileSystemMonitor.init();
        nativeFileSystemMonitor.monitor(new FileAddedListener() {
            public void fileAdded(File dir, String fn) {
                System.out.println("Added" + fn);
            }
        });
    }
}
