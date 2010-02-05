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

    public SFTPSession(String usr, String host, String pw, int port) throws Exception {
        JSch jSch = new JSch();
        session = jSch.getSession(usr, host, port);

        userInfo = new MyUserInfo(usr, pw, null);

        session.setUserInfo(userInfo);
        session.connect();
        channel = (ChannelSftp) session.openChannel("sftp");
    }

    public void start() throws Exception {
        if(  !channel.isConnected() )
            channel.connect();
    }
}
