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

import org.springframework.util.Assert;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
public class NativeFileSystemMonitor {
    static {
        System.loadLibrary(
                "sifsmon"); // todo  : should I make this in turn delegate to a System.getProperty call so we can move this data to launch arguments?
    }

    static interface FileAddedListener {
        void fileAdded(File dir, String fn);
    }

    /**
     * This method is what is called from our code to talk to the native code: Behind the scenes, this offers a native
     * inotify based event driven mechanism.
     *
     * @param path the path that should be monitored. I haven't done any checking to see well this plays with the C
     *             libraries that we're using.
     */
    public native void monitor(String path);

    public NativeFileSystemMonitor() {
    }

    private transient LinkedBlockingQueue<String> additions;
    private int maxQueueValue;
    private File directoryToMonitor;
    private boolean autoCreateDirectory;

    public File getDirectoryToMonitor() {
        return directoryToMonitor;
    }

    public void setAutoCreateDirectory(boolean autoCreateDirectory) {
        this.autoCreateDirectory = autoCreateDirectory;
    }

    public NativeFileSystemMonitor(File file) {
        this.directoryToMonitor = file;

    }

    public void setMaxQueueValue(int maxQueueValue) {
        this.maxQueueValue = maxQueueValue;
    }

    public void init() {

        additions = new LinkedBlockingQueue<String>(this.maxQueueValue);

        boolean goodDirToMonitor = (directoryToMonitor.isDirectory() && directoryToMonitor.exists());
        if (!goodDirToMonitor) {
            if (!directoryToMonitor.exists()) {
                if (this.autoCreateDirectory) {
                    directoryToMonitor.mkdirs();
                }
            }

        }

        Assert.state(directoryToMonitor.exists(), "the directory " +
                                                  directoryToMonitor.getAbsolutePath() + " doesn't exist");

    }

    /**
     * This method is invoked FROM the C code, delegating to the
     */
    public void nativeFileRecieved(String fileName) {
        additions.add(fileName);
    }

    public void monitor(final FileAddedListener fal) {
        final File nFile = new File(this.getDirectoryToMonitor().getAbsolutePath());

        final String absPath = nFile.getAbsolutePath();

        // todo make this use an executor with a thread pool
        // I have no idea the implications of thread safety for this sort of thing
        Thread t = new Thread(new Runnable() {
            public void run() {
                do {
                    try {

                        fal.fileAdded(nFile, additions.take());

                    }
                    catch (Throwable e) {
                        e.printStackTrace();
                    }

                }
                while (true);
            }
        });
        t.start();

        monitor(absPath);
    }
}
