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

package com.joshlong.esb.springintegration.modules.net.sftp.config;

import com.joshlong.esb.springintegration.modules.net.sftp.SFTPMessageSource;
import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a> TODO flesh this out
 */
public class SFTPMessageSourceFactoryBean extends AbstractFactoryBean<SFTPMessageSource> {

    @Override
    public Class<? extends SFTPMessageSource> getObjectType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected SFTPMessageSource createInstance() throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
