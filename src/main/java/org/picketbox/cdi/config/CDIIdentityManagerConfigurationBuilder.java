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

import org.jboss.picketlink.idm.spi.IdentityStore;
import org.picketbox.cdi.idm.PicketLinkIdentityManager;
import org.picketbox.core.config.ConfigurationBuilder;
import org.picketbox.core.config.IdentityManagerConfigurationBuilder;
import org.picketbox.core.exceptions.ConfigurationException;
import org.picketbox.core.identity.IdentityManager;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class CDIIdentityManagerConfigurationBuilder extends IdentityManagerConfigurationBuilder {

    public CDIIdentityManagerConfigurationBuilder(ConfigurationBuilder builder) {
        super(builder);
    }

    public CDIIdentityManagerConfigurationBuilder providedStore() {
        IdentityStore identityStore = resolveIdentityStore();

        if (identityStore != null) {
            super.manager(resolveIdentityManager());
        } else {
            throw new ConfigurationException("No provided IdentityStore configuration was found.");
        }

        return this;
    }

    /**
     * <p>
     * Resolves the {@link IdentityStore} instance to be used with the {@link PicketLinkIdentityManager}.
     * </p>
     *
     * @return
     */
    @SuppressWarnings({ "unchecked", "serial" })
    private IdentityStore resolveIdentityStore() {
        Set<Bean<?>> beans = getBuilder().getBeanManager().getBeans(IdentityStore.class, new AnnotationLiteral<Any>() {
        });

        if (beans.isEmpty()) {
            throw new ConfigurationException("JPAIdentityStore was not provided.");
        }

        Bean<IdentityStore> bean = (Bean<IdentityStore>) beans.iterator().next();

        CreationalContext<IdentityStore> createCreationalContext = getBuilder().getBeanManager().createCreationalContext(bean);

        return bean.create(createCreationalContext);
    }

    /**
     * <p>
     * Resolves the {@link PicketLinkIdentityManager} instance to be used.
     * </p>
     *
     * @return
     */
    @SuppressWarnings({ "unchecked", "serial" })
    private IdentityManager resolveIdentityManager() {
        Set<Bean<?>> beans = getBuilder().getBeanManager().getBeans(PicketLinkIdentityManager.class, new AnnotationLiteral<Any>() {
        });

        if (beans.isEmpty()) {
            return null;
        }

        Bean<PicketLinkIdentityManager> bean = (Bean<PicketLinkIdentityManager>) beans.iterator().next();

        CreationalContext<PicketLinkIdentityManager> createCreationalContext = getBuilder().getBeanManager().createCreationalContext(bean);

        return bean.create(createCreationalContext);
    }

    private CDIConfigurationBuilder getBuilder() {
        return (CDIConfigurationBuilder) this.builder;
    }
}