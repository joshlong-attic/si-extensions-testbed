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

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

// todo support keys, and all that other stuff thats requited to connect via sftp

public class SFTPSessionFactory implements FactoryBean<SFTPSession>, InitializingBean {

    volatile private int port = 22; // the default
    volatile private String user;
    volatile private String password;
    volatile private String remoteHost;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    /*

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

    */

    /**
     * note that the client must still call <code>connect()</code> on the resulting SFTPSession.
     *
     * @return a valid, connectable SFTPSession
     * @throws Exception
     */
    public SFTPSession getObject() throws Exception {
        return new SFTPSession(this.getUser(), this.getRemoteHost(), this.getPassword(), this.getPort());
    }

    public Class<? extends SFTPSession> getObjectType() {
        return SFTPSession.class;
    }

    public boolean isSingleton() {
        return false;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }


    public void afterPropertiesSet() throws Exception {
        // for now were just working on the use case that u have a usr/pw/host combo
        // TODO key based authentication
        assert !StringUtils.isEmpty(this.remoteHost) : "remoteHost can't be empty!";
        assert !StringUtils.isEmpty(this.user) : "user can't be empty!";
        assert !StringUtils.isEmpty(this.password) : "password can't be empty!";
        assert this.port >= 0 : "port must be a valid number! ";
    }
}
