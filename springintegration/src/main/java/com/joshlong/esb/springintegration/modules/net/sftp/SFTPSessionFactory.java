package com.joshlong.esb.springintegration.modules.net.sftp;

import org.springframework.beans.factory.FactoryBean;

// todo support keys, and all that other stuff thats requited to connect via sftp
public class SFTPSessionFactory implements FactoryBean<SFTPSession> {
    volatile private int port;
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
     * 
     *
     * @return a valid, connectable SFTPSession
     *
     *
     *
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


}
