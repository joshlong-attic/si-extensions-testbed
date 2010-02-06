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


/**
 * // TODO public key example
 * import com.jcraft.jsch.*;
 * <p/>
 * public class UserAuthPubKey{
 * public static void main(String[] arg){
 * <p/>
 * String pubkeyfile="/home/cr/users/anand/.ssh/id_dsa";
 * String passphrase="";
 * String host="gs00", user="root";
 * <p/>
 * try{
 * JSch jsch=new JSch();
 * jsch.addIdentity(pubkeyfile);
 * //   jsch.addIdentity(pubkeyfile, passphrase);
 * jsch.setKnownHosts("/home/cr/users/anand/.ssh/known_hosts");
 * <p/>
 * Session session=jsch.getSession(user, host, 22);
 * session.connect();
 * <p/>
 * Channel channel=session.openChannel("shell");
 * <p/>
 * channel.setInputStream(System.in);
 * channel.setOutputStream(System.out);
 * <p/>
 * channel.connect();
 * }
 * catch(Exception e){
 * System.out.println(e); e.printStackTrace();
 * }
 * } //end of main
 * } //end of class
 */
public class SFTPSessionFactory implements FactoryBean<SFTPSession>, InitializingBean {

    volatile private int port = 22; // the default
    volatile private String user;
    volatile private String password;
    volatile private String remoteHost;

    volatile private String knownHosts;

    volatile private String privateKey, privateKeyPassphrase;

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPrivateKeyPassphrase() {
        return privateKeyPassphrase;
    }

    public void setPrivateKeyPassphrase(String privateKeyPassphrase) {
        this.privateKeyPassphrase = privateKeyPassphrase;
    }

    public String getKnownHosts() {
        return knownHosts;
    }

    public void setKnownHosts(String knownHosts) {
        this.knownHosts = knownHosts;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    /* todo support the known hosts input stream thingy. or maybe using a resource and an editor? */

    public SFTPSession getObject() throws Exception {
        return new SFTPSession(this.getUser(), this.getRemoteHost(), this.getPassword(),
                this.getPort(), this.getKnownHosts(), null, this.getPrivateKey(), this.getPrivateKeyPassphrase());
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
        assert !StringUtils.isEmpty(this.password) ||
                !StringUtils.isEmpty(this.privateKey) || !StringUtils.isEmpty(this.privateKeyPassphrase)
                : "you must configure either a password or a private key and/or a private key passphrase!";
        assert this.port >= 0 : "port must be a valid number! ";
    }
}
