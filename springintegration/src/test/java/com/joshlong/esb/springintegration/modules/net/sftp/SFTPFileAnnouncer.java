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

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

import java.io.File;


/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Component("sftpFileAnnouncer")
public class SFTPFileAnnouncer {
    @ServiceActivator
    public void announceNewSftpFile(File f) throws Throwable {
        System.out.println("new file recieved: " + f.getAbsolutePath());
    }
}