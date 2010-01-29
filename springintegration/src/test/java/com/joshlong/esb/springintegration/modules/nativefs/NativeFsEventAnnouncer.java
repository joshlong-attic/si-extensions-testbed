package com.joshlong.esb.springintegration.modules.nativefs;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class NativeFsEventAnnouncer {

    private transient ConcurrentLinkedQueue<File> files = new ConcurrentLinkedQueue<File>();


    public ConcurrentLinkedQueue<File> getFiles() {
        return files;
    }

    @ServiceActivator
    public void announceIncomingFile(File file) {
        System.out.println("new file=" + file.getAbsolutePath());
        files.add(file);

    }
}
