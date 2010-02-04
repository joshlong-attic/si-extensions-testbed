package com.joshlong.esb.springintegration.modules.net.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;


/**
 * this contains the information we need to talk to a working Jsch session.
 */
public class SFTPSession {


    private volatile Session session;
    private volatile UserInfo userInfo;
    private volatile ChannelSftp channel;
    

    static private class MyUserInfo implements UserInfo {

        @Override
        public String toString() {
            return new ReflectionToStringBuilder(this).toString();
        }

        public MyUserInfo(String user, String password, String passphrase) {
            this.usr = user;
            this.pw = password;
            this.pass = passphrase;
        }


        private String usr, pw, pass;
        private int count = 0;

        public String getPassphrase() {
            return null; // pass
        }

        public String getPassword() {
            return pw;
        }

        public boolean promptPassphrase(String string) {
            return true;
        }

        public boolean promptPassword(String string) {
            return true;
        }

        public boolean promptYesNo(String string) {
            count = +1;
            return true;
        }

        public void showMessage(String string) {
        }

    }

    public Session getSession() {
        return session;
    } 

    public ChannelSftp getChannel() {
        return channel;
    }

    // handle setting up a user/pw connectino
    // different ctors might handle others soon
    public SFTPSession(String usr, String host, String pw, int port) throws Exception{
        JSch jSch = new JSch();
        session = jSch.getSession(usr, host, port);
        userInfo = new MyUserInfo(usr, pw, null);
        session.setUserInfo(userInfo);
        session.connect();
        channel = (ChannelSftp) session.openChannel("sftp");
    }

    public void start() throws Exception {
        channel.connect();
    }
}
