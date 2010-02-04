package com.joshlong.esb.springintegration.modules.nativefs;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class NativeFsEventAnnouncer {


    @ServiceActivator
    @SuppressWarnings("unused")
    public void announceIncomingFile(File file) {
        System.out.println("new file=" + file.getAbsolutePath());
    }
}
