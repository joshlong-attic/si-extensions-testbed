package com.joshlong.esb.springintegration.modules.net.sftp;

public interface SFTPSessionPool {

    /**
     * this returns a session that can be used to connct to an sftp instance and perform operations
     *
     * @return
     * @throws Exception
     */
    SFTPSession  getSession() throws Exception;
}
