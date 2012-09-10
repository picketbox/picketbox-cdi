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

package org.picketbox.cdi.test;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.picketbox.cdi.event.CDIAuthenticationEventManager;
import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.PicketBoxSubject;
import org.picketbox.core.config.ConfigurationBuilder;
import org.picketbox.core.config.PicketBoxConfiguration;
import org.picketbox.core.identity.IdentityManager;

/**
 * <p>Bean responsible for produce the {@link PicketBoxConfiguration}. This configuration will be used during the {@link PicketBoxManager} startup.</p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
@ApplicationScoped
public class PicketBoxConfigurer {

    @Inject
    private BeanManager beanManager;

    @Produces
    public PicketBoxConfiguration createConfiguration() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder
            .authentication()
                .eventManager().manager(new CDIAuthenticationEventManager(this.beanManager))
            .identityManager()
                .manager(createIdentityManager());

        return builder.build();
    }

    private IdentityManager createIdentityManager() {
        return new IdentityManager() {

            @Override
            public PicketBoxSubject getIdentity(PicketBoxSubject resultingSubject) {
                List<String> roles = new ArrayList<String>();

                roles.add("Manager");
                roles.add("Financial");

                resultingSubject.setRoleNames(roles);

                return resultingSubject;
            }
        };
    }

}
