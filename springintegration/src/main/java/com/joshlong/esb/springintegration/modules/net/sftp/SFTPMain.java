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

import com.jcraft.jsch.*;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import java.util.Collection;


/* Want to test the SFTP supor from Apache commons -vfs  */
public class SFTPMain {


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


    static private Collection<ChannelSftp.LsEntry> listRemoteSystemWithUserAndPassword(String host, int port, String usr, String pw, String remotePath)
            throws Throwable {
        JSch jSch = new JSch();
        Session session = jSch.getSession(usr, host, port);
        UserInfo userInfo = new MyUserInfo(usr, pw, null);
        session.setUserInfo(userInfo);
        session.connect();
        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp c = (ChannelSftp) channel;
        return c.ls(remotePath);

    }

    // TODO test with my joshlong.com site using the key i have (~/jlong.pem)
    public static void main(String[] args) throws Throwable {

        for (ChannelSftp.LsEntry l : listRemoteSystemWithUserAndPassword("richelle", 22, "richelle", "starbucks", "."))
            System.out.println("remote path: " + l.getFilename());

/*
        JSch jSch = new JSch();
        File privateKeyFile = new File(SystemUtils.getUserHome(), "sftp_key.pub");

        String passphrase = "cow", userName = "dgq9n6v6", pw = "Pass1234#", host = "safetrans-sit.wellsfargo.com";
        int port = 2022;

        InputStream in = new FileInputStream(privateKeyFile);
        byte[] bytesForPrivateKey = IOUtils.toByteArray(in);
        IOUtils.closeQuietly(in);


        byte[] publicKey = new byte[0];
        final byte[] emptyPassPhrase = new byte[0]; // Empty passphrase for now, get real passphrase from MyUserInfo


        jSch.addIdentity(
                userName, passphrase
                *//*  bytesForPrivateKey,
                  null,
                  emptyPassPhrase*//*
        );
        Session session = jSch.getSession(userName, host, port);

        Map<String, String> props = new HashMap<String, String>();

        props.put("StrictHostKeyChecking", "no");
        session.setConfig(MapUtils.toProperties(props));

        UserInfo myUserInfoui = new MyUserInfo(userName, pw, passphrase);

        session.setHost(host);
        session.connect();
        Channel channel = session.openChannel("sftp");
        ChannelSftp sftp = (ChannelSftp) channel;
        sftp.connect();

        final Vector files = sftp.ls(".");
        for (Object obj : files) {
            // Do stuff with files
        }
        sftp.disconnect();
        session.disconnect();*/


    }
}
/*

// Locate the Jar file
        FileSystemManager fsManager = VFS.getManager();

        String uri = "sftp://"+ richellesUser + ":"+ richellesPw + "@"+richellesMachine ;
        FileObject richellesBox = fsManager.resolveFile(  uri );


// List the children of the Jar file
        FileObject[] children = richellesBox.getChildren();
        System.out.println( "Children of " + richellesBox.getName().getURI() );
        for (FileObject aChildren : children) {
            System.out.println(aChildren.getName().getBaseName());
        }

     */
/*   StaticUserAuthenticator auth = new StaticUserAuthenticator("username", "password", null);
        FileSystemOptions opts = new FileSystemOptions();
        DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(opts, auth);
        FileObject fo = VFS.getManager().resolveFile("smb://host/anyshare/dir", opts);
        *//*



    }
} */
