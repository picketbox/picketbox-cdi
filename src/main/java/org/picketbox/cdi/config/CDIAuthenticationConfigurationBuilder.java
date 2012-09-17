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

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.util.AnnotationLiteral;

import org.picketbox.cdi.authentication.IDMAuthenticationManager;
import org.picketbox.cdi.idm.PicketLinkIdentityManager;
import org.picketbox.core.config.AuthenticationConfigurationBuilder;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class CDIAuthenticationConfigurationBuilder extends AuthenticationConfigurationBuilder {

    public CDIAuthenticationConfigurationBuilder(CDIConfigurationBuilder builder) {
        super(builder);
    }

    private CDIConfigurationBuilder getBuilder() {
        return (CDIConfigurationBuilder) this.builder;
    }

    public CDIAuthenticationConfigurationBuilder idmAuthentication() {
        super.authManager(resolveIDMAuthenticationManager());
        return this;
    }

    @Override
    public CDIIdentityManagerConfigurationBuilder identityManager() {
        return (CDIIdentityManagerConfigurationBuilder) super.identityManager();
    }

    /**
     * <p>
     * Resolves the {@link PicketLinkIdentityManager} instance to be used.
     * </p>
     *
     * @return
     */
    @SuppressWarnings({ "unchecked", "serial" })
    private IDMAuthenticationManager resolveIDMAuthenticationManager() {
        Set<Bean<?>> beans = getBuilder().getBeanManager().getBeans(IDMAuthenticationManager.class, new AnnotationLiteral<Any>() {
        });

        if (beans.isEmpty()) {
            return null;
        }

        Bean<IDMAuthenticationManager> bean = (Bean<IDMAuthenticationManager>) beans.iterator().next();

        CreationalContext<IDMAuthenticationManager> createCreationalContext = getBuilder().getBeanManager().createCreationalContext(bean);

        return bean.create(createCreationalContext);
    }
}
