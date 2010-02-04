package com.joshlong.esb.springintegration.modules.nativefs;

import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
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

@SuppressWarnings("unchecked")
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

    @SuppressWarnings("")
    @Test
    public void testHavingRecievedEvents() throws Throwable {
        for (File f : fsfile.listFiles())
            if (!f.delete()) throw new RuntimeException(String.format("couldn't delete file %s!", f.getAbsolutePath()));

        Assert.assertTrue(fsfile.list().length == 0);

        Assert.assertTrue(fsfile.exists());

        for (int i = 0; i < 10; i++)
            write(i + ".txt", "now is the time for " + i);


        if (System.in.read() <= 0) {
            logger.debug("returning after test");
        }


    }
}
