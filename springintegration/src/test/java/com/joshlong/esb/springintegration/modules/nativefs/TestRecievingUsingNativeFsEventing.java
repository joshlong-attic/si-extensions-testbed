package com.joshlong.esb.springintegration.modules.nativefs;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

@ContextConfiguration(locations = {"/nativefs/recieving_native_fs_events_using_ns.xml"})
public class TestRecievingUsingNativeFsEventing extends AbstractJUnit4SpringContextTests {

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private NativeFsEventAnnouncer nativeFsEventAnnouncer;
    @Resource(name = "fileSystemResource")
    private FileSystemResource fileSystemResource;

    private File fsfile;

    @Before
    public void setup() throws Throwable {
        fsfile = fileSystemResource.getFile();
    }

    void write(String fileName, String msg) {
        try {

            File nFile = new File(fsfile, fileName);
            OutputStream outputStream = new FileOutputStream(nFile);
            IOUtils.write(msg, outputStream);
            IOUtils.closeQuietly(outputStream);
        } catch (Throwable t) {
            // don't care
        }
    }

    @Test
    public void testHavingRecievedEvents() throws Throwable {
        for (File f : fsfile.listFiles())
            f.delete();

        Assert.assertTrue(fsfile.list().length == 0);

        Assert.assertTrue(fsfile.exists());

        for (int i = 0; i < 10; i++)
            write(i + ".txt", "now is the time for " + i);


        System.in.read();

    }
}
