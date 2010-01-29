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

package com.joshlong.esb.springintegration.modules.nativefs;

import org.springframework.util.Assert;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The ideal behind this is to have each OS use it's own version of the library, optimized for that system.
 * <p/>
 * The native library that backs this code can tap into the event dispatching logic that all operating systems have
 * and react to it much quicker than we could using Spring Integration and pollers.
 * <p/>
 * - For Linux, we use inotify (this is the only one implemented at the moment)
 * - For BSD/MacOSX we could use kqueue
 * - For Solaris we could use ... Dtrace??
 * - For Windows we could use ... ???
 * <p/>
 * <p/>
 * <p/>
 * To Run:
 * <p/>
 * add the following to your VM arguments:
 * <p/>
 * -Djava.library.path=/home/jlong/Desktop/fsmon/
 * <p/>
 * <p/>
 * A file named libsifsmon.so should be there.
 * <p/>
 *
 * @author Josh Long
 */
public class NativeFileSystemMonitor {
    static{
        
        System.loadLibrary("sifsmon"); // todo  : should I make this in turn delegate to a System.getProperty call so we can move this data to launch arguments?
    }
    static interface FileAddedListener {
        void fileAdded(File dir, String fn);
    }


    /**
     * This method is what is called from our code to talk to the native code:
     * <p/>
     * Behind the scenes, this offers a native inotify based event driven mechanism.
     */
    public native void monitor(String path);

   public NativeFileSystemMonitor(){

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
            if (!directoryToMonitor.exists())
                if (this.autoCreateDirectory)
                    directoryToMonitor.mkdirs();


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

        // todo make this use an executor with a thread pool
        // I have no idea the implications of thread safety for this sort of thing
        new Thread(new Runnable() {
            public void run() {
                do {
                    try {
                        fal.fileAdded(nFile, additions.take());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (true);
            }
        }).start();

        monitor(nFile.getAbsolutePath());

    }


    public static void main(String[] args) throws Throwable {

        File watchme = new File("/home/jlong/Desktop/foo/");

        NativeFileSystemMonitor nativeFileSystemMonitor = new NativeFileSystemMonitor(watchme);
        nativeFileSystemMonitor.setAutoCreateDirectory(true);
        nativeFileSystemMonitor.setMaxQueueValue(1000); // only 1000 items in the queue at a time!
        nativeFileSystemMonitor.init();
        nativeFileSystemMonitor.monitor(new FileAddedListener() {
            public void fileAdded(File dir, String fn) {
                try {
                    final File absFile = new File(dir, fn);
                    String line = String.format("just noticed the addition of file %s with size: %s", absFile.getAbsolutePath(), absFile.length());
                    System.out.println(line);

                    /*// i just want to see the file size a little bit later to see if it ever veers away
                    new Thread(new Runnable(){
                       public void run() {
                           try {
                               Thread.sleep( 10000 );
                           } catch (InterruptedException e) {
                               e.printStackTrace();
                           }
                           System.out.println( String.format("the file size of %s is now %s",absFile.getAbsolutePath(),absFile.length()));
                        }
                    }).start();*/


                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });

    }
}