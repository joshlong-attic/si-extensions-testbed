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

package com.joshlong.esb.springintegration.modules.net.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.InputStream;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 * @author <a href="mailto:mario.gray@gmail.com">Mario Gray</a>
 */
public class SFTPSession {
    private static final Logger logger = Logger.getLogger(SFTPSession.class);
    private volatile ChannelSftp channel;
    private volatile Session session;
    private String privateKey;
    private String privateKeyPassphrase;
    private volatile UserInfo userInfo;

    // handle setting up a user/pw connectino
    // different ctors might handle others soon

    /**
     * @param userName              the name of the account being logged into.
     * @param hostName              this should be the host. I found values like <code>foo.com</code> work, where
     *                              <code>http://foo.com</code> don't.
     * @param userPassword          if you are not using key based authentication, then you are likely being prompted
     *                              for a password each time you login. This is that password. It is <em>not</em> the
     *                              passphrase for the private key!
     * @param port                  the default is 22, and if you specify N<0 for this value we'll default it to 22
     * @param knownHostsFile        this is the known hosts file. If you don't specify it, jsch does some magic to work
     *                              without your specification. If you have it in a non well-known location, however,
     *                              this property is for you. An example: <code>/home/user/.ssh/known_hosts</code>
     * @param knownHostsInputStream this is the known hosts file. If you don't specify it, jsch does some magic to work
     *                              without your specification. If you have it in a non well-known location, however,
     *                              this property is for you. An example: <code>/home/user/.ssh/known_hosts</code>. Note
     *                              that you may specify this <em>or</em> the #knownHostsFile  - not both!
     * @param privateKey            this is usually used when you want passwordless automation (obviously, for this
     *                              integration it's useless since this lets you specify a password once, anyway, but
     *                              still good to have if required). This file might be ~/.ssh/id_dsa, or a
     *                              <code>.pem</code> for your remote server (for example, on EC2)
     * @param pvKeyPassPhrase       sometimes, to be extra secure, the private key itself is extra encrypted. In order
     *                              to surmount that, we need the private key passphrase. Specify that here.
     *
     * @throws Exception thrown if any of a myriad of scenarios plays out
     */
    public SFTPSession(String userName,
                       String hostName,
                       String userPassword,
                       int port,
                       String knownHostsFile,
                       InputStream knownHostsInputStream,
                       String privateKey,
                       String pvKeyPassPhrase)
            throws Exception {
        JSch jSch = new JSch();

        if (port <= 0) {
            port = 22;
        }

        // make sure these are set
        this.privateKey = privateKey; //   "/home/cr/users/anand/.ssh/id_dsa"
        this.privateKeyPassphrase = pvKeyPassPhrase;

        // known hosts: /home/jlong/.ssh/known_hosts
        if (!StringUtils.isEmpty(knownHostsFile)) {
            jSch.setKnownHosts(knownHostsFile);
            logger.debug("jsch.setKnownHosts(" + knownHostsFile + ")");
        }
        else if (null != knownHostsInputStream) {
            jSch.setKnownHosts(knownHostsInputStream);
            logger.debug("jsch.setKnownHosts(InputSteam)");
        }

        // private key
        if (!StringUtils.isEmpty(this.privateKey)) {
            if (!StringUtils.isEmpty(privateKeyPassphrase)) {
                jSch.addIdentity(this.privateKey, privateKeyPassphrase);
                logger.debug(" jSch.addIdentity(" + this.privateKey + ", " + privateKeyPassphrase + ");");
            }
            else {
                jSch.addIdentity(this.privateKey);
                logger.debug(" jSch.addIdentity(" + this.privateKey + ");");
            }
        }

        session = jSch.getSession(userName, hostName, port);

        if (!StringUtils.isEmpty(userPassword)) {
            session.setPassword(userPassword);
        }

        userInfo = new MyUserInfo(userPassword);
        session.setUserInfo(userInfo);
        session.connect();
        channel = (ChannelSftp) session.openChannel("sftp");
    }

    public ChannelSftp getChannel() {
        return channel;
    }

    public Session getSession() {
        return session;
    }

    public void start() throws Exception {
        if (!channel.isConnected()) {
            logger.debug("channel is not connected, connecting.");
            channel.connect();
        }
    }

    /**
     * this is a simple, optimistic implementation of this interface. It simply returns in the positive where possible
     * and handles interactive authentication (ie, 'Please enter your password: ' prompts are dispatched automatically
     * using this)
     *
     * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
     */
    private static class MyUserInfo implements UserInfo {
        private String pw;

        public MyUserInfo(String password) {
            this.pw = password;
        }

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
            return true;
        }

        public void showMessage(String string) {
        }
    }
}
