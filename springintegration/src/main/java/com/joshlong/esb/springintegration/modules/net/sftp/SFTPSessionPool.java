package com.joshlong.esb.springintegration.modules.net.sftp;

/**
 * Maintains a pool of configured, but not connected, SFTP connections.
 */
public interface SFTPSessionPool {
    /**
     * Frees up the client. Im not sure what the meaningful semantics of this are.
     * Perhaps it just calls <code>(session ,channel).disconnect()</code> ?
     *
     * @param session the session to relinquish / renew
     */
    void release(SFTPSession session);

    /**
     * this returns a session that can be used to connct to an sftp instance and perform operations
     *
     * @return the session from the pool ready to be connected to.
     * @throws Exception thrown if theres any of the numerous faults possible when trying to connect to the remote server
     */
    SFTPSession getSession() throws Exception;
}
