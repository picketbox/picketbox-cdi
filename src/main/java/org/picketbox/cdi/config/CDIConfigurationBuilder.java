/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.picketbox.cdi.config;

import javax.enterprise.inject.spi.BeanManager;

import org.picketbox.core.config.AuthenticationConfigurationBuilder;
import org.picketbox.core.config.ConfigurationBuilder;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class CDIConfigurationBuilder extends ConfigurationBuilder {

    private BeanManager beanManager;

    public CDIConfigurationBuilder(BeanManager beanManager) {
        this.beanManager = beanManager;
    }

    /* (non-Javadoc)
     * @see org.picketbox.core.config.ConfigurationBuilder#createAuthenticationBuilder()
     */
    @Override
    protected AuthenticationConfigurationBuilder createAuthenticationBuilder() {
        return new CDIAuthenticationConfigurationBuilder(this);
    }

    /* (non-Javadoc)
     * @see org.picketbox.core.config.ConfigurationBuilder#createIdentityManager()
     */
    @Override
    protected CDIIdentityManagerConfigurationBuilder createIdentityManager() {
        return new CDIIdentityManagerConfigurationBuilder(this);
    }

    /* (non-Javadoc)
     * @see org.picketbox.core.config.ConfigurationBuilder#authentication()
     */
    @Override
    public CDIAuthenticationConfigurationBuilder authentication() {
        return (CDIAuthenticationConfigurationBuilder) super.authentication();
    }

    /* (non-Javadoc)
     * @see org.picketbox.core.config.ConfigurationBuilder#identityManager()
     */
    @Override
    public CDIIdentityManagerConfigurationBuilder identityManager() {
        return (CDIIdentityManagerConfigurationBuilder) super.identityManager();
    };

    BeanManager getBeanManager() {
        return this.beanManager;
    }
}